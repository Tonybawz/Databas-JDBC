package controller;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.sql.Date;
import java.util.ArrayList;
import java.util.Calendar;

import view.KrustyGUIview;
import model.Customer;
import model.Database;
import model.Pallet;
import model.Recipe;

public class Controller {
	
	private KrustyGUIview gui;
	private Database db;
	
	public Controller(Database db, KrustyGUIview gui) {
		this.db = db;
		this.gui = gui;
		gui.addSearchListener(new SearchListener());
		gui.addBlockedButtonListener(new BlockedListener());
		gui.addNotBlockedButtonListener(new UnBlockedListener());
	}
	
	private boolean validateDates() {
		Date fromDate = formatDate(gui.getFromDate());
		Date toDate = formatDate(gui.getToDate());
		if (fromDate == null || toDate == null) {
			gui.showErrorDialog("Invalid date, correct format: YYYY-MM-DD!");
			return false;
		} else if (fromDate.getTime() > toDate.getTime()) {
			gui.showErrorDialog("Error, fromDate must be earlier than toDate!");
			return false;
		}
		return true;
	}
	
	private Date formatDate(String date) {
		String[] dateSplit = date.split("-");
		if (dateSplit.length == 3) {
			try {
				Calendar cal = Calendar.getInstance();
				cal.setLenient(false);
				cal.set(Calendar.YEAR, Integer.parseInt(dateSplit[0]));
				cal.set(Calendar.MONTH, Integer.parseInt(dateSplit[1]) - 1);
				cal.set(Calendar.DAY_OF_MONTH, Integer.parseInt(dateSplit[2]));
				Date dateRepresentation = new Date(cal.getTimeInMillis());
				return dateRepresentation;
			} catch (NumberFormatException e) {
				return null;
			} catch (ArrayIndexOutOfBoundsException e) {
				return null;
			} catch (IllegalArgumentException e) {
				return null;
			}
		}
		return null;
	}
	
	private boolean validateInput() {
		if (checkInputBoxes() || gui.getSelectedAction() == KrustyGUIview.SEARCH_FOR_PALLET) {
			return true;
		} else {
			gui.showErrorDialog("Please fill in all input fields!");
			return false;
		}
	}
	
	private boolean checkInputBoxes() {
		String searchText = gui.getSearchText().trim();
		if (gui.getSelectedAction() == KrustyGUIview.SEARCH_FOR_PALLET) {
			return searchText.length() != 0;
		} else {
			String fromDate = gui.getFromDate().trim();
			String toDate = gui.getToDate().trim();
			return !(searchText.length() == 0 || fromDate.length() == 0
					|| toDate.length() == 0);
		}
	}
	
	private boolean producePalletFields() {
		if (!gui.getFromDate().isEmpty() && !gui.getSearchText().isEmpty()) {
			return true;
		} else {
			return false;
		}
	}
	
	class SearchListener implements ActionListener {
		@Override
		public void actionPerformed(ActionEvent arg0) {
			int selectedAction = gui.getSelectedAction();
			String searchText = gui.getSearchText();
			if (!searchText.isEmpty() && selectedAction == KrustyGUIview.SEARCH_INGREDIENTQUANTITY) {
				int nbr = db.getIngredientQuantity(searchText);
				gui.updateSearchArea("The quantity of " + searchText + " is " + String.valueOf(nbr) + " units");
				return;
			}
			if (producePalletFields() && selectedAction == KrustyGUIview.PRODUCE_PALLET) {
				if(searchText.contains("/")) {
				Date date = formatDate(gui.getFromDate());
				//input[0] == cookieType, input[1] == companyName
				String[] input = searchText.split("/");
				db.orderPallet(input[1].trim(), date);
				db.insertPallet(input[0].trim());
				gui.updateSearchArea("Pallet with id: " + String.valueOf(db.getNbrOfProduced()) + " produced");
				} else {
					gui.showErrorDialog("Please use syntax cookieType/companyName");
				}
				return;
			}
				
			if (validateInput()) {
				
				switch (selectedAction) {
				case KrustyGUIview.SEARCH_FOR_PALLET:
					palletSearch(searchText, gui.getFromDate(), gui.getToDate());
					break;
				case KrustyGUIview.BLOCK_PALLET:
					if (validateDates()) {
						int numBlocked=  db.blockPallets(searchText,
								formatDate(gui.getFromDate()),
								formatDate(gui.getToDate()));
						gui.updateSearchArea(String.format("Blocked %d pallets", numBlocked));
						
					}
					break;
				case KrustyGUIview.SEARCH_PALLETQUANTITY:
					if (validateDates()) {
						int quantityResult = db.getPalletQuantity(searchText,
								formatDate(gui.getFromDate()),
								formatDate(gui.getToDate()));
						gui.updateSearchArea(Integer.toString(quantityResult)
								+ " pallets of "
								+ searchText
								+ " has been produced during the time period between "
								+ gui.getFromDate() + " and "
								+ gui.getToDate());
					}
					break;
				case KrustyGUIview.UNBLOCK_PALLET:
					if (validateDates()) {
						int numUnblocked=  db.blockPallets(searchText,
								formatDate(gui.getFromDate()),
								formatDate(gui.getToDate()));
						gui.updateSearchArea(String.format("Unblocked %d pallets", numUnblocked));
						
					}
					break;
				}
			}
		}
	}
	
	private void palletSearch(String search, String fromDate, String toDate) {
		//error due to empty fields
		if(search.length() == 0 && fromDate.length() == 0 && toDate.length() == 0) {
			gui.showErrorDialog("Please fill all input fields.");
		}else if(fromDate.length() == 0 && toDate.length() == 0) {	
			//Search for id without dates
			try {
				long palletId = Long.parseLong(search);
				Pallet result = db.palletSearch(palletId);
				ArrayList<Pallet> list = new ArrayList<Pallet>();
				list.add(result);
				produceOutputForPallets(list, "found");
			} catch (NumberFormatException e ) {
				//Search for recipe
				ArrayList<Pallet> pallets = db.palletSearch(search);
				produceOutputForPallets(pallets, "found");
			}
			return;
			//Search for pallets produced during a specific time interval
		} else if(search.length() == 0 && fromDate.length() != 0 && toDate.length() != 0) {
			if(validateDates()) {
				ArrayList<Pallet> pallets = db.palletSearch(formatDate(fromDate), formatDate(toDate));
				produceOutputForPallets(pallets, "found");
			}
			//Search for a specific product (recipe) produced during a specific time interval
		} else if(search.length() != 0 && fromDate.length() != 0 && toDate.length() != 0) {
			if(validateDates()) {
				ArrayList<Pallet> pallets = db.palletSearch(search, formatDate(fromDate), formatDate(toDate));
				produceOutputForPallets(pallets, "found");
			}
		} else if(fromDate.length() != 0 || toDate.length() != 0) {
			validateDates();
		} else {
			gui.showErrorDialog("Please make up your mind.");
		}
	}
	
	private void produceOutputForPallets(ArrayList<Pallet> pallets, String action) {
		if(!pallets.isEmpty()) {
			String resultString = "";
			for(Pallet p : pallets)
				resultString += produceOutputForPallet(p, action);
			gui.updateSearchArea(resultString);
		} else {
			gui.updateSearchArea("Nothing " + action);
		}
	}
	
	private String produceOutputForPallet(Pallet pallet, String action) {
		String toReturn;
		if(pallet != null) {
			toReturn = "pallet with palletId: " + Long.toString(pallet.Id) + " " + action + "\n";
			Customer customer = db.getCustomerForPallet(pallet);
			Recipe recipe = db.getRecipeForPallet(pallet);
			String recipeName = recipe != null ? recipe.recipeName : "null";
			toReturn += "Product: " + recipeName + '\n';
			toReturn += "Blocked: " + pallet.isBlocked + '\n';
			String customerName = customer != null ? customer.cName : "null";
			String customerAddress = customer != null ? customer.address : "null";
			toReturn += "Customer name: " + customerName + '\n';
			toReturn += "Address: " + customerAddress + "\n\n";
		} else {
			toReturn = "Nothing found";
		}
		return toReturn;
	}	
	
	class BlockedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent arg0) {
			ArrayList<Pallet> pallets = db.getPalletsWithBlockStatus(true);
			produceOutputForPallets(pallets, "found");
		}
		
	}
	
	class UnBlockedListener implements ActionListener {

		@Override
		public void actionPerformed(ActionEvent e) {
			ArrayList<Pallet> pallets = db.getPalletsWithBlockStatus(false);
			produceOutputForPallets(pallets, "found");
		}
		
	}
}
