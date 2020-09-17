package com.techelevator.view;

import static org.junit.Assert.*;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;

import org.junit.Test;

import com.techelevator.Snack;
import com.techelevator.VendingMachineCLI;

public class VendingMachineCLITest {

	Menu menu = new Menu(System.in, System.out);
	VendingMachineCLI money = new VendingMachineCLI(menu);
	
	@Test
	public void moneyTowardsBalance() {
		BigDecimal actual = money.addBalance(new BigDecimal(1));
		assertEquals(new BigDecimal (1).setScale(2, RoundingMode.HALF_UP), actual);
		
		actual = money.addBalance(new BigDecimal(5));
		assertEquals(new BigDecimal(6).setScale(2, RoundingMode.HALF_UP), actual);
	}
	
	@Test
	public void getSnackPotatoChips() throws FileNotFoundException {
		Snack actual = money.getSnack("A1");
		
		assertEquals("Potato Crisps", actual.getName());
	}
	@Test
	public void getC3returnsMountainMelter() throws FileNotFoundException {
		Snack actual = money.getSnack("C3");
		
		assertEquals("Mountain Melter", actual.getName());
		assertEquals(new BigDecimal("1.50"), actual.getPrice());
		assertEquals("Glug Glug, Yum!", actual.itemMessage());
	}
	
	@Test
	public void invalidOptionReturnsNull() throws FileNotFoundException {
		Snack actual = money.getSnack("Z99");
		
		assertEquals(null, actual);
	}
	
	@Test
	//wont pass because of the seconds slot
	public void testingLog() throws IOException{
		String actual = money.logReport(new BigDecimal("5.00"), "Feed Money");
		assertEquals("< 08/07/2020 03:03:43 PM Feed Money : 5.00", actual);
	}
	
	@Test
	public void testingChangeGiven() {
		BigDecimal actualChange = money.getChange(new BigDecimal(5.00));
		
		assertEquals(new BigDecimal("-5.00").setScale(2, RoundingMode.HALF_UP), actualChange);
		
		
	}
	@Test
	public void insertCashAndSpendingGiveBackChange()
	{
		money.addBalance(new BigDecimal(100));
		
		BigDecimal actualChange = money.getChange(new BigDecimal(99.95));

		assertEquals(new BigDecimal(0.05).setScale(2, RoundingMode.HALF_UP), actualChange);
	}
	
	

}
