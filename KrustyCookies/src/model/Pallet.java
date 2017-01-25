package model;

import java.sql.Date;

public class Pallet {

	public long Id;
	public Date creationDateAndTime;
	public boolean isBlocked;
	public long orderId;
	public String recipeName;
	
	public Pallet(long Id, Date creationDateAndTime, boolean isBlocked, long orderId, String recipeName) {
		this.Id = Id;
		this.creationDateAndTime = creationDateAndTime;
		this.isBlocked = isBlocked;
		this.orderId = orderId;
		this.recipeName = recipeName;
	}
}
