package com.zerobase.storeapi.domain.entity;

import com.fasterxml.jackson.annotation.JsonBackReference;
import com.zerobase.storeapi.domain.BaseEntity;
import com.zerobase.storeapi.domain.form.option.CreateOption;
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
@Entity(name = "\"option\"")
@AuditOverride(forClass = BaseEntity.class)
public class Option extends BaseEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String name;
    private int quantity;
    private int price;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "item_id")
    @JsonBackReference
    private Item item;

    public static Option of(CreateOption form) {
        return Option.builder()
                .name(form.getName())
                .quantity(form.getQuantity())
                .price(form.getPrice())
                .build();
    }


    public void decreaseQuantity(Integer quantity) {
        this.quantity -= quantity;
    }

}
