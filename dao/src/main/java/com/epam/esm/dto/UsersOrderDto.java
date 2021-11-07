package com.epam.esm.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UsersOrderDto extends RepresentationModel<UsersOrderDto> {

    private Integer orderId;

    private Integer userId;

    private Integer giftId;

    private String giftName;

    private Double cost;

    private Instant dateOfBuy;

}
