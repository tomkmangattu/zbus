package zbus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Booking {
	
	ArrayList<Bus> availableBuses;
	int seatsNeeded = 0;
	Bus selectedBus;
	int userId;
	
	
	void startBooking(Scanner scanner, Database database, int userId) throws InputExceptions, SQLException, DatabaseException{
		this.userId = userId;
		System.out.println("Enter the number of seats you want to book :");
		String inputString = scanner.nextLine();
		int number = 0;
		try {
			number = Integer.parseInt(inputString);
		}catch (NumberFormatException e) {
			throw new InputExceptions("Please enter a number");
		}
		seatsNeeded = number;
		
		int busNumber =  selectbus(number, database, scanner);
		
		selectedBus = database.selectFullDetailsOfBus(busNumber);
		selectedBus.initSeats(database.selectBookedSeatsOfBus(busNumber, selectedBus.totalSeats));
		
		bookSeats(scanner, database);
				
	}
	
	void bookSeats(Scanner scanner, Database database) throws SQLException {
		int seatsToBeBooked = seatsNeeded;
		String inputString;
		int seatNo = 0;
		String passenger;
		char gender;
		boolean copassengerOnlyFemale = false;
		
		selectedBus.displayAllSeats();
		while(seatsToBeBooked != 0) {
			System.out.println("remaining "  + seatsToBeBooked + " to be booked");
			System.out.println("Enter the seat number you want to book :");
			inputString = scanner.nextLine();
			try {
				seatNo = Integer.parseInt(inputString);
			}catch (NumberFormatException e) {
				System.out.println("Please enter a number");
				continue;
			}
			System.out.println("Enter name of the passenger :");
			passenger = scanner.nextLine();
			System.out.println("Enter gender of the passenger :");
			inputString = scanner.nextLine();
			gender = inputString.toLowerCase().charAt(0);
			if(gender != 'm' && gender != 'f' ) {
				System.out.println("Gender should be m or f");
				continue;
			}
			if(gender == 'f') {
				System.out.println("Do you want to set co passenger as only female (y/n)");
				inputString = scanner.nextLine();
				char coPassengerCondition = inputString.toLowerCase().charAt(0);
				if(coPassengerCondition == 'y') {
					copassengerOnlyFemale = true;
				}
			}
			
			try {
				selectedBus.bookTicket(selectedBus.id, seatNo, passenger, gender, copassengerOnlyFemale, database, userId);
			}catch (BookingException e) {
				System.out.println(e.getMessage());
				System.out.println("Booking failed");
				continue;
			}
			
			seatsToBeBooked--;
		}
		
	}
	

	int selectbus(int number, Database database, Scanner scanner) throws SQLException, NumberFormatException {
		availableBuses =  database.selectBusesWithSeatsMoreThan(number);
		displayBusesWithAvailableSeats(availableBuses);
		
		System.out.println("Enter the bus number you want to book");
		String inputString = scanner.nextLine();
		return Integer.parseInt(inputString);	
	}
	
	
	void displayBusesWithAvailableSeats(ArrayList<Bus> availableBuses) {
		for(Bus bus: availableBuses) {
			bus.displayAvailableSeats();
		}
	}
	
	
}
