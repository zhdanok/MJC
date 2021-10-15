package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Order {

    private Integer id;

    private Integer userId;

    private Integer giftId;

    private Double cost;

    private Instant dateOfBuy;
}
