package com.zerobase.orderapi.service;

import com.zerobase.orderapi.client.MemberClient;
import com.zerobase.orderapi.client.from.Cart;
import com.zerobase.orderapi.client.from.DecreaseBalanceForm;
import com.zerobase.orderapi.client.to.OrderResult;
import com.zerobase.orderapi.domain.order.Orders;
import com.zerobase.orderapi.domain.type.OrderStatus;
import com.zerobase.orderapi.repository.OrdersRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class OrderService {

    private final OrdersRepository orderRepository;
    private final MemberClient memberClient;

    @Transactional
    public List<OrderResult> order(String token, Cart cart) {
        // member point 감소
        DecreaseBalanceForm request = DecreaseBalanceForm.builder()
                .customerId(cart.getCustomerId())
                .totalPrice(cart.getTotalPrice())
                .build();
        memberClient.decreaseBalance(token, request);
        List<OrderResult> results = new ArrayList<>();

        // 옵션별 주문 내용 저장
        for (Cart.Item item : cart.getItems()) {
            for (Cart.Option option : item.getOptions()) {
                int totalPrice = option.getPrice() * option.getQuantity();
                Orders order = Orders.builder()
                        .customerId(cart.getCustomerId())
                        .storeId(item.getStoreId())
                        .sellerId(item.getSellerId())
                        .itemId(item.getId())
                        .itemName(item.getName())
                        .optionId(option.getId())
                        .optionName(option.getName())
                        .price(option.getPrice())
                        .totalPrice(totalPrice)
                        .quantity(option.getQuantity())
                        .orderStatus(OrderStatus.ORDERED)
                        .build();

                orderRepository.save(order);
                results.add(OrderResult.from(order));
            }
        }
        return results;
    }

}
