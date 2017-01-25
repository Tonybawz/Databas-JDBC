package main;

import controller.Controller;
import controller.ProductionController;
import model.Database;
import view.*;

/**
 * MovieBooking is the main class for the movie ticket booking 
 * application. It creates a database object and the GUI to
 * interface to the database.
 */
public class Main {
    public static void main(String[] args) {
        Database database = new Database();
        /* --- connects to puccini with our login --- */
		database.openConnection("db79", "callecuc");
		database.setNbrOfProduced();
		//passes database as an argument so connection can be closed - toDo fix!
        KrustyGUIview krustyGUI = new KrustyGUIview(database);
        Controller controller = new Controller(database, krustyGUI);
        ProductionController production =new ProductionController(5, database, krustyGUI);
    }
}