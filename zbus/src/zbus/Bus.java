package zbus;

import java.sql.SQLException;

public class Bus {
	int id;
	String busTypeString;
	int totalSeats;
	int bookedSeats;
	int fare;
	int availableSeats;
	
	public Bus(int id, String busTypeString, int totalSeats, int bookedSeats, int fare) {
		this.id = id;
		this.busTypeString = busTypeString;
		this.totalSeats = totalSeats;
		this.bookedSeats = bookedSeats;
		this.fare = fare;
	}
	
	public Bus(int id, String busTypeString, int availableSeats) {
		this.id = id;
		this.busTypeString = busTypeString;
		this.availableSeats = availableSeats;
	}
	
	public Bus(int id, String busTypeString, int totalSeats, int ticketFare) {
		this.id = id;
		this.busTypeString = busTypeString;
		this.totalSeats = totalSeats;
		this.fare = ticketFare;
	}
	
	void displayAvailableSeats() {
		System.out.println(id + " " + busTypeString + " " + (totalSeats - bookedSeats));
	}
	
	void displayBusNameWithIndex(int idx) {
		System.out.format("%-5s %-10s \n", idx, busTypeString);
	}
	
	void displayAllSeats() {
		System.out.println("called parent");
	}
	
	void initSeats(Seats[] seats) {
		
	}
	
	boolean bookTicket(int busNo, int seatNo,
			char gender, Database database, int userId) throws SQLException, BookingException {
		return false;
	}
	
	void displayFilteredResults(int idx) {
		System.out.format("%-5s %-10s %-5s\n", idx , busTypeString, availableSeats);
	}
}
