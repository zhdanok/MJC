package com.epam.esm.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class CostAndDateOfBuyDto extends RepresentationModel<CostAndDateOfBuyDto> {

    private Double cost;

    private Instant dateOfBuy;
}
