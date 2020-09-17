package com.techelevator;

import java.math.BigDecimal;

public class Candy implements Snack {

	private BigDecimal price;
	private String name;
	private int quantity;

	public Candy(String name, BigDecimal price) {
		this.name = name;
		this.price = price;
		this.quantity = 5;
	}

	@Override
	public String getName() {
		// TODO Auto-generated method stub
		return this.name;
	}

	@Override
	public BigDecimal getPrice() {
		// TODO Auto-generated method stub
		return this.price;
	}

	@Override
	public String itemMessage() {
		// TODO Auto-generated method stub
		return "Munch Munch, Yum!";
	}

	@Override
	public int getQuantity() {
		// TODO Auto-generated method stub
		return quantity;
	}

	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
