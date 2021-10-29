package com.epam.esm.entity;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
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

	@JsonIgnore
	@ManyToMany(mappedBy = "tags")
	private Set<GiftCertificate> gifts;

}
