package com.techelevator;

import java.math.BigDecimal;

public class Gum implements Snack {

	private BigDecimal price;
	private String name;
	private int quantity;

	public Gum(String name, BigDecimal price) {
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
		return "Chew Chew, Yum!";
	}

	@Override
	public int getQuantity() {
		// TODO Auto-generated method stub
		return quantity;
	}

	
	@Override
	public void setQuantity(int quantity) {
		this.quantity = quantity;
	}

}
