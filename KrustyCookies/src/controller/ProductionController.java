package controller;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Timer;
import java.util.TimerTask;

import model.Ingredient;
import model.Database;
import model.Order;
import model.Pallet;
import model.ProductionOrder;
import model.Recipe;
import view.KrustyGUIview;

public class ProductionController {

	public static final int PRODUCTION = 0;
	public static final int FREEZING = 1;
	public static final int PACKAGING_IN_BAGS = 2;
	public static final int PACKAGING_IN_CARTONS = 3;
	public static final int LOADING_ON_PALLETS = 4;
	
	private Timer timer;
	private int currentStageInProduction = PRODUCTION;
	private KrustyGUIview gui;
	private Database db;
	private ProductionOrder currentProductionOrder;
	private Pallet currentPallet;
	private ArrayList<ProductionOrder> orders;
	private SimpleDateFormat sdf;
	private int currentProductionOrderNumber = Integer.MAX_VALUE;
	
	public ProductionController(int runTime, Database db, KrustyGUIview gui) {
		this.gui = gui;
		this.db = db;
		
		int produced = db.getNbrOfProduced();
		//updates creationTime so that simulation can be run
		for (int i = 0; i < produced; i++) {
			db.updateTestPallets(i);
		}
		
		sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		timer = new Timer();
		timer.schedule(new ProductionTask(), 0, (runTime / 5) * 1000 );

	}
	
	class ProductionTask extends TimerTask {

		@Override
		public void run() {
			Calendar cal = Calendar.getInstance();
			if(currentPallet == null) {
				currentPallet = db.getNextPalletToProduce();
				if(currentPallet == null) {
					gui.insertToProductionArea(sdf.format(cal.getTime()) + " - No orders to produce");
					return;
				}
			}
			String message = sdf.format(cal.getTime()) + " - Order number " + Long.toString(currentPallet.orderId) + ", " ;
			switch (currentStageInProduction) {
			case PRODUCTION :
				//checkMaterials(currentPallet);
				message += currentPallet.recipeName + " in production";
				currentStageInProduction++;
				break;
			case FREEZING :
				message += currentPallet.recipeName + " in freezing";
				currentStageInProduction++;
				break;
			case PACKAGING_IN_BAGS :
				message += currentPallet.recipeName + " in packaging in bags";
				currentStageInProduction++;
				break;
			case PACKAGING_IN_CARTONS :
				message += currentPallet.recipeName + " in packaging in cartons";
				currentStageInProduction++;
				break;
			case LOADING_ON_PALLETS :
				message += currentPallet.recipeName + " in loading on pallets";
				currentStageInProduction = PRODUCTION;
				db.createOrderedPallet(currentPallet);
				currentPallet = null;
				break;
			}
			gui.insertToProductionArea(message);			
		}
		
		private void checkMaterials(Pallet pallet) {
			//lyckas inte implementera, än så länge manuell ändring i sql.
				if(!db.isEnoughRawMaterials(pallet)) {
					
					}
		}
	}
	
}