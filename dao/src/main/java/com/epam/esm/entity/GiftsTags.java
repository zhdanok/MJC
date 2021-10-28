package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "gifts_tags")
public class GiftsTags {

    @Id
    @Column(name = "gift_id")
    Integer giftId;

    @Column(name = "tag_id")
    Integer tagId;
}
