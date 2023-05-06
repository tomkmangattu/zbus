package zbus;

public class Passenger {
	
	
	public Passenger(String nameString, int seat, char gender, boolean coPassengerFemaleOnly, int ticketFare) {
		this.nameString = nameString;
		this.seat = seat;
		this.gender = gender;
		this.coPassengerFemaleOnly = coPassengerFemaleOnly;
		this.ticketFare = ticketFare;
	}
	
	
	String nameString;
	int seat;
	char gender;
	boolean coPassengerFemaleOnly;
	int ticketFare;
	
}
