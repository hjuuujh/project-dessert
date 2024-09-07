package com.zerobase.storeapi.domain.redis;

import com.zerobase.storeapi.domain.redis.form.AddItemCartForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import java.util.List;
import java.util.stream.Collectors;

@Data
@NoArgsConstructor
@RedisHash("cart")
@Builder
@AllArgsConstructor
public class Cart {
    @Id
    private Long customerId;
    private List<Item> items;
    private List<String> messages;
    private int totalPrice;

    public void addMessage(String message) {
        messages.add(message);
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item implements Comparable<Item> {
        private Long id;
        private Long storeId;
        private Long sellerId;
        private String storeName;
        private String name;
        private List<Option> options;


        public static Item from(AddItemCartForm form, Long sellerId) {
            return Item.builder()
                    .id(form.getId())
                    .storeId(form.getStoreId())
                    .sellerId(sellerId)
                    .storeName(form.getStoreName())
                    .name(form.getName())
                    .options(form.getOptions().stream().map(Option::from).collect(Collectors.toList()))
                    .build();
        }


        @Override
        public int compareTo(Item p) {
            return (int) (getId() - p.getId());
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Option implements Comparable<Option> {
        private Long id;
        private String name;
        private Integer quantity;
        private Integer price;

        public static Option from(AddItemCartForm.Option form) {
            return Option.builder()
                    .id(form.getId())
                    .name(form.getName())
                    .quantity(form.getQuantity())
                    .price(form.getPrice())
                    .build();
        }

        @Override
        public int compareTo(Option pi) {
            return (int) (getId() - pi.getId());
        }
    }
}
