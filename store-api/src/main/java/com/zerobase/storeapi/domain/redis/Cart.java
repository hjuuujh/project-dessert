package com.zerobase.storeapi.domain.redis;

import com.zerobase.storeapi.domain.redis.form.AddItemCartForm;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.redis.core.RedisHash;

import javax.persistence.Id;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
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
    @NotNull(message = "주문 아이템은 필수입니다.")
    private List<Item> items;
    private List<String> messages;
    @NotNull(message = "주문 가격은 필수입니다.")
    private int totalPrice;

    public void addMessage(String message) {
        messages.add(message);
    }



    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    @Builder
    public static class Item implements Comparable<Item> {
        @NotNull(message = "주문 아이템 id는 필수입니다.")
        private Long id;
        @NotNull(message = "주문 아이템의 스토어 id는 필수입니다.")
        private Long storeId;
        @NotNull(message = "주문 아이템의 seller id는 필수입니다.")
        private Long sellerId;
        @NotBlank(message = "주문 매장명은 필수입니다.")
        private String storeName;
        @NotBlank(message = "주문 아이템 명은 필수입니다.")
        private String name;
        @NotNull(message = "주문 아이템의 옵션은 필수입니다.")
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
        @NotNull(message = "주문 옵션 id는 필수입니다.")
        private Long id;
        @NotBlank(message = "주문 옵션명은 필수입니다.")
        private String name;
        @NotNull(message = "주문 수량은 필수입니다.")
        private Integer quantity;
        @NotNull(message = "주문 가격은 필수입니다.")
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
