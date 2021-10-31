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
@Table(name = "users_order")
public class UsersOrder {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "order_id")
	private Integer orderId;

	@Column(name = "userId")
	private Integer userId;

	@Column(name = "giftId")
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
