package com.epam.esm.dto;

import lombok.*;
import org.springframework.hateoas.RepresentationModel;

import java.time.Instant;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class GiftAndTagDto extends RepresentationModel<GiftAndTagDto> {

	private Integer id;

	private String name;

	private String description;

    private Double price;

    private Integer duration;

    private Instant createDate;

    private Instant lastUpdateDate;

    private List<TagDto> tags;

}
