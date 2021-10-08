package com.epam.esm.dto;

import lombok.*;

import java.time.Instant;
import java.util.List;

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
    private Instant create_date;
    private Instant last_update_date;


}
