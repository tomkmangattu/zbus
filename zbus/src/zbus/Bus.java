package zbus;

import java.sql.SQLException;

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
	
	
	void displayAllSeats() {
		System.out.println("called parent");
	}
	
	void initSeats(Seats[] seats) {
		
	}
	
	void bookTicket(int busNo, int seatNo, String passengerName, 
			char gender, boolean coPassengerOnlyFemale, Database database, int userId) throws SQLException, BookingException {
		
	}
}
