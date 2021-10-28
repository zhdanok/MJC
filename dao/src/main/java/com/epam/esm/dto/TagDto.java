package com.epam.esm.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class TagDto extends RepresentationModel<TagDto> {

    private Integer id;

    private String name;
}
