package zbus;

public class Bus {
	int id;
	String busTypeString;
	int totalSeats;
	int bookedSeats;
	
	public Bus(int id, String busTypeString, int totalSeats, int bookedSeats) {
		this.id = id;
		this.busTypeString = busTypeString;
		this.totalSeats = totalSeats;
		this.bookedSeats = bookedSeats;
	}
	
	void displayAvailableSeats() {
		System.out.println(id + " " + busTypeString + " " + (totalSeats - bookedSeats));
	}
}
