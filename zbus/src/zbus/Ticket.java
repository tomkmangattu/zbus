package zbus;

public class Ticket {
	int	id;
	String busTypeString;
	int seatNo;
	String nameString;
	char gender;
	boolean coPassengerFemaleOnly;
	int ticketFare;
	
	
	public Ticket(int id, String busTypeString, int seatNo, String nameString, char gender,
			boolean coPassengerFemaleOnly, int ticketFare) {
		super();
		this.id = id;
		this.busTypeString = busTypeString;
		this.seatNo = seatNo;
		this.nameString = nameString;
		this.gender = gender;
		this.coPassengerFemaleOnly = coPassengerFemaleOnly;
		this.ticketFare = ticketFare;
	}
	
	void displayTicketWithIndex(int idx) {
		System.out.format(" %-5s %-5s %-10s %-6s %-20s %-4s %-10s \n", idx, id, busTypeString, seatNo, nameString, gender, coPassengerFemaleOnly);
	}
}
