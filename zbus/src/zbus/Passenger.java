package zbus;

public class Passenger {
	
	
	public Passenger(String nameString, int seat, char gender, boolean coPassengerFemaleOnly) {
		super();
		this.nameString = nameString;
		this.seat = seat;
		this.gender = gender;
		this.coPassengerFemaleOnly = coPassengerFemaleOnly;
	}
	
	
	String nameString;
	int seat;
	char gender;
	boolean coPassengerFemaleOnly;
	
	
}
