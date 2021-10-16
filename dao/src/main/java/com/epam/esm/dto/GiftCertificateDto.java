package com.epam.esm.dto;

import lombok.*;

import java.time.Instant;

@Data
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class GiftCertificateDto {

    private Integer id;

    private String name;

    private String description;

    private Double price;

    private Integer duration;

    private Instant createDate;

    private Instant lastUpdateDate;
}
