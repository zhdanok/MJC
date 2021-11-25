package com.epam.esm.entity;

import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import org.hibernate.envers.Audited;
import org.springframework.hateoas.RepresentationModel;

import javax.persistence.*;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@Audited
@EqualsAndHashCode
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "tag")
public class Tag extends RepresentationModel<Tag> {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "tag_id")
    private Integer id;

    @Column(name = "tag_name", unique = true)
    private String name;

    @JsonIgnore
    @ManyToMany(mappedBy = "tags", cascade = CascadeType.ALL)
    private Set<GiftCertificate> gifts;

}
