package com.zerobase.orderapi.domain.order;

import com.zerobase.orderapi.domain.BaseEntity;
import com.zerobase.orderapi.domain.type.OrderStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.envers.AuditOverride;

import javax.persistence.*;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "orders")
@AuditOverride(forClass = BaseEntity.class)
public class Orders extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private Long customerId;
    private Long sellerId;
//    @ManyToOne
//    @JoinColumn(name = "seller_id")
//    private Seller seller;
    private Long storeId;

    private Long itemId;
    private String  itemName;
    private Long optionId;
    private String  optionName;
    private Integer totalPrice;
    private Integer price;
    private Integer quantity;

    @Enumerated(EnumType.STRING)
    private OrderStatus orderStatus;

    public void updateStatus(OrderStatus orderStatus) {
        this.orderStatus = orderStatus;
    }
}


