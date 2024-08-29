package com.zerobase.memberapi.domain.member.dto;

import lombok.*;
import com.zerobase.memberapi.domain.member.entity.Seller;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SellerDto {
    private Long id;

    private String email;
    private String name;
    private String phone;
    private List<String> roles;
    private int income;


    public static SellerDto from(Seller seller) {
        return SellerDto.builder()
                .id(seller.getId())
                .email(seller.getEmail())
                .name(seller.getName())
                .phone(seller.getPhone())
                .roles(seller.getRoles())
                .income(seller.getIncome())
                .build();
    }

}