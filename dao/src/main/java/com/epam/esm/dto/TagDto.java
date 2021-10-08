package com.epam.esm.dto;

import lombok.*;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TagDto {

    public TagDto(String name) {
        this.name = name;
    }

    private Integer id;
    private String name;


}
