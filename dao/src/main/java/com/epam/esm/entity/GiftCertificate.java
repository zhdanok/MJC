package com.epam.esm.entity;

import lombok.*;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.Instant;
import java.util.Set;

@Getter
@Setter
@Entity
@Builder
@Audited
@ToString
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gift_certificate")
public class GiftCertificate {

    @ManyToMany(cascade = {CascadeType.MERGE}, fetch = FetchType.EAGER)
    @JoinTable(name = "gifts_tags", joinColumns = {@JoinColumn(name = "gift_id")},
            inverseJoinColumns = {@JoinColumn(name = "tag_id")})
    Set<Tag> tags;

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "gift_id", unique = true)
    private Integer id;

    private String description;

    private Double price;

    private Integer duration;

    @Column(name = "gift_name", unique = true)
    private String name;

    @Column(name = "create_date")
    private Instant createDate;

    @Column(name = "last_update_date")
    private Instant lastUpdateDate;

}
