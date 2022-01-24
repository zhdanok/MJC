package com.epam.esm.entity;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.envers.Audited;

import javax.persistence.*;
import java.time.Instant;

@Data
@Entity
@Builder
@Audited
@NoArgsConstructor
@AllArgsConstructor
@Table(name = "users_order", indexes = @Index(name = "users_order_user_id_index", columnList = "user_Id"))
public class UsersOrder {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "order_id")
    private Integer orderId;

    @Column(name = "user_Id")
    private Integer userId;

    @Column(name = "gift_Id")
    private Integer giftId;

    @Column(name = "gift_name")
    private String giftName;

    @Column(name = "cost")
    private Double cost;

    @Column(name = "date_of_buy")
    private Instant dateOfBuy;

    public UsersOrder(Double cost, Instant dateOfBuy) {
        this.cost = cost;
        this.dateOfBuy = dateOfBuy;
    }

}
