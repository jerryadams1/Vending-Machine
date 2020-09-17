package com.techelevator;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;
import java.util.Scanner;
import java.util.TreeMap;

import com.techelevator.view.Menu;

public class VendingMachineCLI {

	private static final String MAIN_MENU_OPTION_DISPLAY_ITEMS = "Display Vending Machine Items";
	private static final String MAIN_MENU_OPTION_PURCHASE = "Purchase";
	private static final String MAIN_MENU_EXIT = "Exit Vending Machine";
	private static final String[] MAIN_MENU_OPTIONS = { MAIN_MENU_OPTION_DISPLAY_ITEMS, MAIN_MENU_OPTION_PURCHASE,
			MAIN_MENU_EXIT };
	private static final String[] PURCHASE_MENU = { "Feed Money", "Select Product", "Finish Transaction" };
	private static final String[] MONEY_MENU = { "$1 Bill", "$2 Bill", "$5 Bill", "Back" };
	private Menu menu;
	private BigDecimal globalBalance = new BigDecimal("0.00");
	private Map<String, Snack> allSnacks = new HashMap<String, Snack>();
	private Map<String, Snack> orderedMap = null;

	public VendingMachineCLI(Menu menu) {
		this.menu = menu;

	}

	public void addingSnacks() throws FileNotFoundException {

		File inputFile = new File("vendingmachine.csv");

		try (Scanner scan = new Scanner(inputFile)) {

			while (scan.hasNextLine()) {
				String line = scan.nextLine();
				String[] entry = line.split("\\|");

				if (entry[entry.length - 1].equals("Chip")) {
					Snack chip = new Chip(entry[1], new BigDecimal(entry[2]));
					this.allSnacks.put(entry[0], chip);
				} else if (entry[entry.length - 1].equals("Drink")) {
					Snack drink = new Drink(entry[1], new BigDecimal(entry[2]));
					this.allSnacks.put(entry[0], drink);
				} else if (entry[entry.length - 1].equals("Candy")) {
					Snack candy = new Candy(entry[1], new BigDecimal(entry[2]));
					this.allSnacks.put(entry[0], candy);
				} else if (entry[entry.length - 1].equals("Gum")) {
					Snack gum = new Gum(entry[1], new BigDecimal(entry[2]));
					this.allSnacks.put(entry[0], gum);
				}
			}
		}

		this.orderedMap = new TreeMap<>(allSnacks);
	}

	public String logReport(BigDecimal moneyUsed, String log) throws IOException {
		File logFile = new File("Log.txt");
		String writtenLog = "";
		String snackKey = "";

		if (!log.equals("Feed Money") && !log.equals("Give Change")) {
			for (Map.Entry<String, Snack> entry : allSnacks.entrySet()) {
				if (entry.getValue().getName().equals(log)) {
					snackKey = entry.getKey();
				}
			}
		}

		if (!logFile.exists()) {
			logFile.createNewFile();
		}
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("MM/dd/yyyy hh:mm:ss a");

		LocalDateTime now = LocalDateTime.now();

		if (log.equals("Feed Money")) {
			writtenLog = "> " + dtf.format(now) + " " + log + ": $" + moneyUsed.toString() + " $" + this.globalBalance;
		} else if (log.equals("Give Change")) {
			writtenLog = "> " + dtf.format(now) + " " + log + ": " + "$" + this.globalBalance + " " + "$"
					+ this.globalBalance.subtract(this.globalBalance) + " >";
		} else {
			writtenLog = "> " + dtf.format(now) + " " + log + " " + snackKey + " $" + this.globalBalance.add(moneyUsed)
					+ " $" + this.globalBalance + " ";
		}

		PrintWriter entry = new PrintWriter(new FileOutputStream(logFile.getAbsoluteFile(), true));
		entry.append(writtenLog);

		entry.close();
		return writtenLog;
	}

	public void run() {
		// Populating snack Map
		try {
			addingSnacks();
		} catch (FileNotFoundException e) {

		}

		while (true) {
			String choice = (String) menu.getChoiceFromOptions(MAIN_MENU_OPTIONS);

			if (choice.equals(MAIN_MENU_OPTION_DISPLAY_ITEMS)) {
				// display vending machine items
				snackDisplay();
			} else if (choice.equals(MAIN_MENU_OPTION_PURCHASE)) {
				// do purchase
				processPurchaseMenuOptions();
			} else if (choice.equals(MAIN_MENU_EXIT)) {
				System.exit(0);
			}

		}
	}

	public void snackDisplay() {
		String sKey = "";
		String sName = "";
		String sPrice = "";
		String sQuantity = "";
		for (Map.Entry<String, Snack> snack : orderedMap.entrySet()) {
			if (snack.getValue().getQuantity() == 0) {
				sKey = snack.getKey();
				sName = snack.getValue().getName();
				sPrice = "$" + snack.getValue().getPrice();
				sQuantity = "(Sold Out)";
			} else {
				sKey = snack.getKey();
				sName = snack.getValue().getName();
				sPrice = "$" + snack.getValue().getPrice();
				sQuantity = "(" + snack.getValue().getQuantity() + ")";
			}

			System.out.printf("%-2s %-20s %-6s %-5s %n", sKey, sName, sPrice, sQuantity);

		}
	}

	public BigDecimal addBalance(BigDecimal money) {
		this.globalBalance = this.globalBalance.add(money);

		return this.globalBalance.setScale(2, RoundingMode.HALF_UP);
	}

	private void processPurchaseMenuOptions() {
		String purchaseMenuOption = "";
		while (!purchaseMenuOption.equals("Finish Transaction")) {
			purchaseMenuOption = (String) menu.getChoiceFromOptions(PURCHASE_MENU);

			if (purchaseMenuOption.equals("Feed Money")) {
				processMoneyFeed();
			} else if (purchaseMenuOption.equals("Select Product")) {
				processProductSelection();
			}

		}
		System.out.println("Your change is $" + this.globalBalance);

		try {
			logReport(this.globalBalance, "Give Change");
		} catch (IOException e) {

		}

		getChange(this.globalBalance);
	}

	private void processProductSelection() {
		Scanner selection = new Scanner(System.in);
		Snack newSnack = null;

		// Display selection
		snackDisplay();
		System.out.println();

		// Prompt user for choice
		System.out.println("Please choose an option >>> ");
		String usrChoice = selection.nextLine();

		try {
			newSnack = getSnack(usrChoice.toUpperCase());
		} catch (Exception e) {

		}

		if (newSnack == null) {
			System.out.println("Invalid choice!");
		} else if (this.globalBalance.compareTo(newSnack.getPrice()) == -1) {
			System.out.println("Insufficient funds!");
			System.out.println("Total Balance: $" + this.globalBalance);
		} else if (newSnack.getQuantity() == 0) {

			System.out.println("Out of Stock");
		} else {
			newSnack.setQuantity(newSnack.getQuantity() - 1);
			System.out.println(newSnack.itemMessage());
			getChange(newSnack.getPrice());

			try {
				logReport(newSnack.getPrice(), newSnack.getName());
			} catch (IOException e) {

			}

		}
	}

	private void processMoneyFeed() {
		String feedOptions = "";
		while (!feedOptions.equals("Back")) {
			String moneyToAdd = "0.00";
			feedOptions = (String) menu.getChoiceFromOptions(MONEY_MENU);
			if (feedOptions.contains("$1")) {
				moneyToAdd = "1.00";
			} else if (feedOptions.contains("$2")) {
				moneyToAdd = "2.00";
			} else if (feedOptions.contains("$5")) {
				moneyToAdd = "5.00";
			}
			this.addBalance(new BigDecimal(moneyToAdd));

			try {
				if (!moneyToAdd.equals("0.00"))
					logReport(new BigDecimal(moneyToAdd), "Feed Money");
			} catch (IOException e) {

			}
		}
		System.out.println("Total balance: $" + globalBalance);
	}

	public Snack getSnack(String snackKey) throws FileNotFoundException {

		if (allSnacks.isEmpty() == true) {
			addingSnacks();
		}
		Snack tempSnack = null;
		try {
			tempSnack = this.allSnacks.get(snackKey);
		} catch (NullPointerException e) {

		}

		return tempSnack;
	}

	public static void main(String[] args) throws FileNotFoundException {
		Menu menu = new Menu(System.in, System.out);
		VendingMachineCLI cli = new VendingMachineCLI(menu);
		cli.run();
	}

	public BigDecimal getChange(BigDecimal moneyUsed) {
		this.globalBalance = this.globalBalance.subtract(moneyUsed);
		return this.globalBalance.setScale(2, RoundingMode.HALF_UP);
	}

}
