package com.epam.esm.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class GiftAndTagDto {

    private Integer id;
    private String name;
    private String description;
    private Double price;
    private Integer duration;
    private Instant create_date;
    private Instant last_update_date;
    private List<String> tags;
}
