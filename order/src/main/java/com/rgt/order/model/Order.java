package com.rgt.order.model;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class Order {
	private int orderNum;
	private String food;
	private int quantity;
	private int seatNum;
	private String orderTime;
	private String status;
}
