package model;

import java.sql.Date;
import java.util.ArrayList;

	public class Order {
		
		public int Id;
		public Date requestedDeliveryDate;
		public ArrayList<ProductionOrder> productionorders;
		
		public Order(int Id, Date requestedDeliveryDate, ArrayList<ProductionOrder> productionorders) {
			this.Id = Id;
			this.requestedDeliveryDate = requestedDeliveryDate;
			this.productionorders = productionorders;
		}
}
