package com.zerobase.memberapi.domain.member.dto;

import lombok.*;
import com.zerobase.memberapi.domain.member.entity.Customer;

import java.util.List;
import java.util.Set;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class CustomerDto {
    private Long id;

    private String email;
    private String name;
    private String phone;
    private List<String> roles;
    private Set<Long> followList;
    private Set<Long> heartList;
    private int balance;


    public static CustomerDto from(Customer customer) {
        return CustomerDto.builder()
                .id(customer.getId())
                .email(customer.getEmail())
                .name(customer.getName())
                .phone(customer.getPhone())
                .roles(customer.getRoles())
                .balance(customer.getBalance())
                .followList(customer.getFollowList())
                .heartList(customer.getHeartList())
                .build();
    }

}