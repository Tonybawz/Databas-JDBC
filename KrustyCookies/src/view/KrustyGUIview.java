package view;

import javax.swing.*;
import javax.swing.border.TitledBorder;
import javax.swing.event.*;

import java.awt.*;
import java.awt.event.*;

import model.Database;


public class KrustyGUIview extends JFrame {

	private static final long serialVersionUID = -5302982647681520570L;
	private JFrame mainFrame;
	private JTextField inputField, fromDateField, toDateField;
	private JButton searchButton, blockedTrueButton, blockedFalseButton, blockButton, unblockButton;
	private JTextArea searchArea, productionArea;
	private String[] searchText = { "Search for pallet", "Produce pallet",
			"Block pallets", "Unblock pallets", "Search palletquantity", "Search ingredientquantity"};
	private JComboBox searchCombo = new JComboBox();
	private WindowHandler windowHandler;
	
	public static final int SEARCH_FOR_PALLET = 0;
	public static final int PRODUCE_PALLET = 1;
	public static final int BLOCK_PALLET = 2;
	public static final int UNBLOCK_PALLET = 3;
	public static final int SEARCH_PALLETQUANTITY = 4;
	public static final int SEARCH_INGREDIENTQUANTITY = 5;
	
	private Database db;

	public KrustyGUIview(Database db) {
		this.db = db;
		mainFrame = new JFrame();
		mainFrame.setLayout(new GridLayout(7,2));
		JLabel searchLabel = new JLabel("Search/Production parameters (syntax: CookieType/CompanyName)");
		mainFrame.add(searchLabel);
		this.inputField = new JTextField("", 20);
		mainFrame.add(inputField);
		JLabel fromDateLabel = new JLabel("From date: (YYYY-MM-DD)");
		mainFrame.add(fromDateLabel);
		this.fromDateField = new JTextField("", 6);
		mainFrame.add(fromDateField);
		JLabel toDateLabel = new JLabel("To date: (YYYY-MM-DD)");
		mainFrame.add(toDateLabel);
		this.toDateField = new JTextField("", 6);
		mainFrame.add(toDateField);

		for (String s : searchText) {
			searchCombo.addItem(s);
		}
		
		mainFrame.add("tab", searchCombo);
		this.searchButton = new JButton("Search/Block/Produce");
		mainFrame.add("tab", searchButton);
		
		searchArea = new JTextArea();
		searchArea.setEditable(false);
		TitledBorder searchTitle;
		searchTitle = BorderFactory.createTitledBorder("Search results");
		searchArea.setBorder(searchTitle);
		searchArea.setLineWrap(true);
		mainFrame.add(new JScrollPane(searchArea));
		productionArea = new JTextArea();
		productionArea.setEditable(false);
		TitledBorder productionTitle;
		productionTitle = BorderFactory.createTitledBorder("Production status");
		productionTitle.setTitleJustification(TitledBorder.RIGHT);
		productionArea.setBorder(productionTitle);
		productionArea.setLineWrap(true);
		mainFrame.add(new JScrollPane(productionArea));	
		
		this.blockedTrueButton = new JButton("Show Blocked Pallets");
		mainFrame.add("p", blockedTrueButton);
		this.blockedFalseButton = new JButton("Show Unblocked Pallets");
		mainFrame.add("", blockedFalseButton);
		
		mainFrame.setSize(1000, 700);
		mainFrame.setTitle("Krusty Kookies");
		mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		mainFrame.setVisible(true);
		mainFrame.setResizable(false);
		mainFrame.addWindowListener(windowHandler = new WindowHandler());
	}

	/**
	 * WindowHandler is a listener class, called when the user exits the
	 * application.
	 */
	class WindowHandler extends WindowAdapter {
		/**
		 * Called when the user exits the application. Closes the connection to
		 * the database.
		 * 
		 * @param e
		 *            The window event (not used).
		 */
		public void windowClosing(WindowEvent e) {
			db.closeConnection();
			System.exit(0);
		}
	}
	
	public void updateSearchArea(String input) {
		SwingUtilities.invokeLater(new SearchInsertToDo(input));
	}
	
	public void insertToProductionArea(String toInsert) {
		SwingUtilities.invokeLater(new ProductionLog(toInsert));
		productionArea.setCaretPosition(productionArea.getDocument().getLength());
	}
	
	public String getSearchText() {
		return inputField.getText().trim();
	}
	
	public int getSelectedAction() {
		return searchCombo.getSelectedIndex();
	}
	
	public String getFromDate() {
		return fromDateField.getText().trim();
	}

	public String getToDate() {
		return toDateField.getText().trim();
	}
	
	public void showErrorDialog(String message) {
		JOptionPane.showMessageDialog(mainFrame, message, "Error",
				JOptionPane.ERROR_MESSAGE);
	}
	
	public void addSearchListener(ActionListener searchListener) {
		searchButton.addActionListener(searchListener);
	}
	
	public void addBlockedButtonListener(ActionListener actionListener) {
		blockedTrueButton.addActionListener(actionListener);
	}
	
	public void addNotBlockedButtonListener(ActionListener actionListener) {
		blockedFalseButton.addActionListener(actionListener);
	}

	public void addComboBoxActionListener(ActionListener actionListener) {
		searchCombo.addActionListener(actionListener);
	}
	
	class SearchInsertToDo implements Runnable {
		private String toLog;
		public SearchInsertToDo(String toLog) {
			this.toLog = toLog;
		}
		
		@Override
		public void run() {
			searchArea.setText(toLog);
		}
	}
	
	class ProductionLog implements Runnable {
		private String toLog;
		public ProductionLog(String toLog) {
			this.toLog = toLog;
		}
		
		@Override
		public void run() {
			productionArea.append(toLog + '\n');
		}
		
	}
}
