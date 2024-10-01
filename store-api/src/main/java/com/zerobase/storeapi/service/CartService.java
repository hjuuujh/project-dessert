package com.zerobase.storeapi.service;

import com.zerobase.storeapi.client.RedisClient;
import com.zerobase.storeapi.domain.entity.Item;
import com.zerobase.storeapi.domain.entity.Option;
import com.zerobase.storeapi.domain.redis.Cart;
import com.zerobase.storeapi.domain.redis.form.AddCartResult;
import com.zerobase.storeapi.domain.redis.form.AddItemCartForm;
import com.zerobase.storeapi.domain.redis.form.DeleteOptionCartForm;
import com.zerobase.storeapi.domain.redis.form.UpdateOptionCartForm;
import com.zerobase.storeapi.exception.StoreException;
import com.zerobase.storeapi.repository.StoreItemRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static com.zerobase.storeapi.exception.ErrorCode.NOT_FOUND_ITEM;

@Service
@RequiredArgsConstructor
public class CartService {
    private final StoreItemRepository storeItemRepository;
    private final RedisClient redisClient;

    public AddCartResult addCart(Long customerId, AddItemCartForm form) {
        Item item = storeItemRepository.findById(form.getId())
                .orElseThrow(() -> new StoreException(NOT_FOUND_ITEM));

        Cart cart = redisClient.get(customerId, Cart.class);
        if (cart == null) {
            cart = new Cart(customerId, new ArrayList<>(), new ArrayList<>(), 0);
        }

        // 이전에 같은 상품 있는지 확인
        Optional<Cart.Item> itemOptional = cart.getItems().stream()
                .filter(i -> i.getId().equals(form.getId()))
                .findFirst();

        if (itemOptional.isPresent()) {
            // 같은 상품있으면 해당 상품 가져옴
            Cart.Item redisItem = itemOptional.get();

            // 담은 상품 상세
            List<Cart.Option> options = form.getOptions().stream()
                    .map(Cart.Option::from).collect(Collectors.toList());

            // 장바구니에서 상품 가져와 item id와 option넣은 map 생성해 속도향상
            Map<Long, Cart.Option> optionMap = redisItem.getOptions().stream().collect(Collectors.toMap(Cart.Option::getId, it -> it));

            cart.setMessages(new ArrayList<>());

            // 추가된 제품의 이름이 기존과 달라진 경우 알림
            if (!redisItem.getName().equals(form.getName())) {
                cart.addMessage(redisItem.getName() + "의 정보가 변경되었습니다. 확인 부탁드립니다.");
            }

            for (Cart.Option option : options) {
                Cart.Option redisOption = optionMap.get(option.getId());
                // 장바구니에 없는 상품이면 추가
                if (redisOption == null) {
                    redisItem.getOptions().add(option);
                } else {
                    // 이미 있던 상품이면 변경사항 있는경우 message에 넣어 알려줌
                    if (!redisOption.getName().equals(option.getName())) {
                        cart.addMessage(redisOption.getName() + "의 옵션명이 변경되었습니다.");
                    } else if (!redisOption.getPrice().equals(option.getPrice())) {
                        cart.addMessage(redisOption.getName() +" "+ option.getPrice() + "의 가격이 변경되었습니다.");
                    }
                    // 장바구니에 추가로 넣은만큼 개수 증가
                    redisOption.setQuantity(redisOption.getQuantity() + option.getQuantity());
                }
            }
        } else {
            // cart에 같은 상품 없으면 변환해서 add
            Cart.Item newItem = Cart.Item.from(form, item.getSellerId());
            cart.getItems().add(newItem);
        }

        cart.setTotalPrice(getTotalPrice(cart));

        redisClient.put(customerId, cart);

        return AddCartResult.builder()
                .storeName(cart.getItems().get(0).getStoreName())
                .name(cart.getItems().get(0).getName())
                .price(cart.getItems().get(0).getOptions().get(0).getPrice())
                .build();
    }


    // 상품의 가격이나 수량이 변동 될 수 있음 -> 알림 필요
    // 메세지를 보고난 다음에는 확인 했으므로 제거
    public Cart getCart(Long customerId) {
        Cart cart = redisClient.get(customerId, Cart.class);
        cart = cart != null ? cart : new Cart(customerId, new ArrayList<>(), new ArrayList<>(), 0);
        Cart refreshCart = refreshCart(cart);
        Cart returnCart = new Cart();
        returnCart.setCustomerId(customerId);
        returnCart.setItems(refreshCart.getItems());
        returnCart.setTotalPrice(refreshCart.getTotalPrice());
        returnCart.setMessages(refreshCart.getMessages());
        // 메세지 리턴하고 확인했으니 기존 알림 삭제
        refreshCart.setMessages(new ArrayList<>());
        redisClient.put(customerId, cart);
        return returnCart;
    }

    protected Cart refreshCart(Cart cart) {
        // 1. 상품이나 상품의 아이템의 정보, 가격, 수량이 변경되었는지 체크하고
        // 그에 맞는 알림 제공
        // 2. 상품의 수량, 가격을 임의로 변경
        Map<Long, Item> itemMap = storeItemRepository.findAllByIdIn(
                        cart.getItems().stream().map(Cart.Item::getId).collect(Collectors.toList()))
                .stream()
                .collect(Collectors.toMap(Item::getId, item -> item));

        for (int i = 0; i < cart.getItems().size(); i++) {
            Cart.Item item = cart.getItems().get(i);
            Item it = itemMap.get(item.getId());

            // 카트에 담은 아이템이 삭제된경우
            if (it == null) {
                // 카트에서 삭제하고
                cart.getItems().remove(item);
                i--;
                // 메세지 추가
                cart.addMessage(item.getName() + " 상품이 삭제되었습니다.");
                continue;
            }

            // 아이템이 존재하는 경우 옵션 확인
            Map<Long, Option> optionMap = it.getOptions().stream()
                    .collect(Collectors.toMap(Option::getId, option -> option));

            List<String> tmpMessages = new ArrayList<>();
            for (int j = 0; j < item.getOptions().size(); j++) {
                Cart.Option cartOption = item.getOptions().get(j);
                Option op = optionMap.get(cartOption.getId());

                // 카트에 담은  옵션이 삭제된경우
                if (op == null) {
                    // 카트에서 삭제하고
                    item.getOptions().remove(cartOption);
                    j--;
                    // 메세지 추가
                    tmpMessages.add(cartOption.getName() + " 옵션이 삭제되었습니다.");
                    continue;
                }


                boolean isPriceChanged = false, isQuantityNotEnough = false;
                // 카트에 옵션을 담을 당시의 가격과 현재의 가격이 다른경우
                if (!cartOption.getPrice().equals(op.getPrice())) {
                    // flag -> true
                    isPriceChanged = true;
                    // 현재 가격으로 카트 내용 변경
                    cartOption.setPrice(op.getPrice());
                }

                if (isPriceChanged) {
                    tmpMessages.add(cartOption.getName() + " 가격이 변동되었습니다.");

                }

            }

        }
        cart.setTotalPrice(getTotalPrice(cart));
        return cart;
    }

    public Cart deleteCartOption(Long customerId, DeleteOptionCartForm form) {
        Cart cart = redisClient.get(customerId, Cart.class);
        // 원하는 option 삭제
        cart.getItems().forEach(item -> {
            item.getOptions().removeIf(option -> form.getOptionIds().contains(option.getId()));
        });

        // item에 모든 옵션이 삭제된경우 item 삭제
        List<Long> ids = new ArrayList<>();
        cart.getItems().forEach(item -> {
            if (item.getOptions().isEmpty()) {
                ids.add(item.getId());
            }
        });
        cart.getItems().removeIf(item -> ids.contains(item.getId()));
        cart.setTotalPrice(getTotalPrice(cart));

        redisClient.put(customerId, cart);
        return cart;
    }

    public Cart updateCartOption(Long customerId, UpdateOptionCartForm form) {
        Cart cart = redisClient.get(customerId, Cart.class);

        // 변경원하는 옵션 찾기
        Cart.Option newOption = cart.getItems().stream().filter(item -> item.getId().equals(form.getItemId()))
                .findFirst()
                .get()
                .getOptions().stream().filter(option -> option.getId().equals(form.getOptionId()))
                .findFirst()
                .get();

        newOption.setQuantity(form.getQuantity());
        cart.setTotalPrice(getTotalPrice(cart));

        redisClient.put(customerId, cart);
        return cart;
    }

    public int getTotalPrice(Cart cart) {
        return cart.getItems().stream().flatMapToInt(
                p -> p.getOptions().stream().flatMapToInt(
                        option -> IntStream.of(option.getPrice() * option.getQuantity()))).sum();
    }
}

