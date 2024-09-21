package com.zerobase.storeapi.service;

import com.zerobase.storeapi.client.MemberClient;
import com.zerobase.storeapi.client.OrderClient;
import com.zerobase.storeapi.client.RedisClient;
import com.zerobase.storeapi.client.from.OrderResult;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.Option;
import com.zerobase.storeapi.domain.redis.Cart;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemOptionRepository;
import com.zerobase.storeapi.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static com.zerobase.storeapi.exception.ErrorCode.*;

@Service
@RequiredArgsConstructor
public class CartOrderService {
    private final RedisClient redisClient;
    private final CartService cartService;
    private final StoreItemOptionRepository storeItemOptionRepository;
    private final OrderClient orderClient;
    private final MemberClient memberClient;
    private final StoreItemRepository storeItemRepository;

    @Transactional
    public List<OrderResult> orderCart(String token, Long customerId, Cart cart) {
        cart.setCustomerId(customerId);

        // 기존 카트
        Cart oldCart = cartService.getCart(customerId);

        // 주문 카트
        Cart orderCart = cartService.refreshCart(cart);
        if (!orderCart.getMessages().isEmpty()) {
            // 문제가 있음
            throw new StoreException(ORDER_FAIL_CHECK_CART);
        }

        // 수량 충분한지 확인
        checkEnoughQuantity(orderCart);

        // 고객의 포인트 충분한지 확인
        checkEnoughBalance(token, orderCart);

        // 수량 감소
        decreaseOptionQuantity(orderCart);

        // 주문
        List<OrderResult> orderResults = orderClient.order(token, orderCart);

        // cart 업데이트
        updateCart(customerId, oldCart, orderCart);

        // item order count 증가
        updateItemOrderCount(orderCart);

        return orderResults;
    }

    @Transactional
    public void updateItemOrderCount(Cart orderCart) {
        for(Cart.Item item : orderCart.getItems()) {
            Item dbItem = storeItemRepository.findById(item.getId())
                    .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));

            dbItem.increaseOrderCount();
        }
    }

    public void updateCart(Long customerId, Cart oldCart, Cart orderCart) {
        // 선택주문 : 주문하지 않은 아이템 장바구니에 남아야함

        // item id 별 -> option id별 정렬해서
        // 겹치는 경우 아이템 일부 주문시 남은 옵션 새 카트에 추가
        // 안겹치는 경우 == 주문안함 -> 새 카트에 추가

        // 기존 카트 item id로 정렬
        Collections.sort(oldCart.getItems());
        // 기존 카트 option id로 정렬
        for (Cart.Item item : oldCart.getItems()) {
            Collections.sort(item.getOptions());
        }

        // 주문 카트 item id로 정렬
        Collections.sort(orderCart.getItems());
        // 주문 카트 option id로 정렬
        for (Cart.Item item : orderCart.getItems()) {
            Collections.sort(item.getOptions());
        }

        // 기존 장바구니에서 구매하지 않은 제품들 남겨두기 위해 새 카트 생성
        Cart newCart = new Cart(customerId, new ArrayList<>(), new ArrayList<>(), 0);

        // 기존 장바구니에서 선택 주문하기 때문에 orderCart의 size가 oldCart의 size보다 작음
        // > orderCart에 없는게 oldCart에는 있을 수 있지만 반대는 없음
        // -> orderCart size 기준으로 순회
        List<Cart.Item> newItems = new ArrayList<>();
        while (!orderCart.getItems().isEmpty()) {
            // 하나씩 가져옴
            Cart.Item orderItem = orderCart.getItems().get(0);
            Cart.Item oldItem = oldCart.getItems().get(0);

            // 아이디 비교해서 주문한 경우
            if (orderItem.getId() == oldItem.getId()) {
                System.out.println(oldItem.getId());
                System.out.println(orderItem.getId());

                // 기존 장바구니에서 선택해서 주문 -> orderCart의 option size가 oldCart의 옵션 사이즈보다 작음
                // -> orderCart option 사이즈로 순회
                List<Cart.Option> newOptions = new ArrayList<>();
                while (!orderItem.getOptions().isEmpty()) {
                    // 하나씩 가져옴
                    Cart.Option orderOption = orderItem.getOptions().get(0);
                    Cart.Option oldOption = oldItem.getOptions().get(0);

                    // 아이디 비교해서 주문한 옵션 있으면 두 카트에서 모두 삭제
                    if (orderOption.getId() == oldOption.getId()) {
                        oldItem.getOptions().remove(oldOption);
                        orderItem.getOptions().remove(orderOption);
                    }else {
                        // 주문안한 옵션 그대로 추가
                        newOptions.add(oldOption);
                        // 기존 카트에서만 삭제
                        oldItem.getOptions().remove(oldOption);
                    }

                }
                // 남은 옵션 추가
                newOptions.addAll(oldItem.getOptions());

                // 옵션 모두 주문한 경우 새카트에 아이템 추가
                if (!newOptions.isEmpty()) {
                    Cart.Item newItem = Cart.Item.builder()
                            .id(orderItem.getId())
                            .storeId(orderItem.getStoreId())
                            .storeName(orderItem.getStoreName())
                            .sellerId(orderItem.getSellerId())
                            .name(orderItem.getName())
                            .options(newOptions)
                            .build();

                    newItems.add(newItem);
                }

                // 두카트에서 모두 삭제
                oldCart.getItems().remove(oldItem);
                orderCart.getItems().remove(orderItem);

            }else {
                // 주문 안한 아이템은 그대로 새카트에 추가
                newItems.add(oldItem);
                oldCart.getItems().remove(oldItem);
            }

        }
        // 남은 아이템 추가
        newItems.addAll(oldCart.getItems());

        newCart.setItems(newItems);
        newCart.setTotalPrice(cartService.getTotalPrice(newCart));
        redisClient.put(customerId, newCart);
    }

    private void checkEnoughQuantity(Cart orderCart) {
        orderCart.getItems().forEach(item -> {
            item.getOptions().forEach(option -> {
                Option remainQuantity = storeItemOptionRepository.findById(option.getId())
                        .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
                if (option.getQuantity() > remainQuantity.getQuantity()) {
                    throw new StoreException(ITEM_QUANTITY_NOT_ENOUGH);
                }
            });
        });
    }


    public void decreaseOptionQuantity(Cart orderCart) {
        orderCart.getItems().forEach(item -> {
            item.getOptions().forEach(option -> {
                Option itemOption = storeItemOptionRepository.findById(option.getId())
                        .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));
                itemOption.decreaseQuantity(option.getQuantity());
            });
        });

    }

    private void checkEnoughBalance(String token, Cart cart) {
        int totalPrice = cart.getTotalPrice();
        int balance = memberClient.getBalance(token);
        if (balance < totalPrice) {
            String errorDescription = String.format("[현재 잔액] : %s  [총액] : %s 주문 불가, 잔액 부족입니다.",balance, totalPrice);

            throw new StoreException(ORDER_FAIL_NO_MONEY, errorDescription);
        }
    }


}

