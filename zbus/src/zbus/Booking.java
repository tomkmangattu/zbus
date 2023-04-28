package zbus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Booking {
	
	ArrayList<Bus> availableBuses;
	
	void startBooking(Scanner scanner, Database database) throws InputExceptions, SQLException{
		System.out.println("Enter the number of seats you want to book :");
		String inputString = scanner.nextLine();
		int number = 0;
		try {
			number = Integer.parseInt(inputString);
		}catch (NumberFormatException e) {
			throw new InputExceptions("Please enter a number");
		}
		
		int busNumber =  selectbus(number, database, scanner);
		
		String bustypeString =  availableBuses.get(busNumber).busTypeString;
		
		Bus selectedBus;
		if(bustypeString.toLowerCase().contains("sleeper")) {
			selectedBus = new SleeperBus(availableBuses.get(busNumber));
		}else {
			selectedBus = new SeaterBus(availableBuses.get(busNumber));
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
