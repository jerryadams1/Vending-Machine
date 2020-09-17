package com.techelevator;

import java.math.BigDecimal;

public interface Snack {
	public String getName();
	public BigDecimal getPrice();
	public String itemMessage();
	public int getQuantity();
	public void setQuantity(int quantity);
}
