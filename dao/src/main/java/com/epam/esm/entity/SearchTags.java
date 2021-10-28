package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "searchtags")
public class SearchTags {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "stag_id")
    private Integer id;

    @Column(name = "stag_name")
    private String name;
}
