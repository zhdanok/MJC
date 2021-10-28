package com.epam.esm.entity;

import lombok.*;

import javax.persistence.*;

@Data
@Entity
@Builder
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tag")
public class Tag {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer id;

    @Column(name = "tag_name", unique = true)
    private String name;

}
