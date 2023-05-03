package zbus;

public class Seats {
	int id;
	int bookedBy;
	int seatNo;
	String nameString;
	char gender;
	boolean copassengerFemaleOnly;
	
	public Seats() {
		
	}
	
	public Seats(int id, int bookedBy, int seatNo, String nameString, char gender,
			boolean copassengerFemaleOnly) {
		this.id = id;
		this.bookedBy = bookedBy;
		this.seatNo = seatNo;
		this.nameString = nameString;
		this.gender = gender;
		this.copassengerFemaleOnly = copassengerFemaleOnly;
	}
	
	public void setInformaton(int id, int bookedBy, int seatNo, String nameString, char gender,
			boolean copassengerFemaleOnly) {
		this.id = id;
		this.bookedBy = bookedBy;
		this.seatNo = seatNo;
		this.nameString = nameString;
		this.gender = gender;
		this.copassengerFemaleOnly = copassengerFemaleOnly;
	}
	
}
