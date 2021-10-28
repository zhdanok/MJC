package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_order")
public class UsersOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @ManyToOne
    @JoinColumn(name = "gift_id", nullable = false)
    private GiftCertificate giftCertificate;

    @Column(name = "gift_name")
    private String giftName;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "date_of_buy")
    private Instant dateOfBuy;
}
