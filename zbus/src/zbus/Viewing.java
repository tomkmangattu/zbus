package zbus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Viewing {
	void showFilteringOptions(Scanner scanner, Database database) throws InputExceptions, SQLException {
		System.out.println("Choose any how to filter the result");
		System.out.println("1. AC");
		System.out.println("2. Non AC");
		System.out.println("3. Both");
		
		System.out.println("Enter your choice");
		String inputString = scanner.nextLine();
		
		int choice = 0;
		try {
			choice = Integer.parseInt(inputString);
		}catch (NumberFormatException e) {
			System.out.println("Please enter a number");
			return;
		}
		
		StringBuffer queryString = new StringBuffer("^");
		
		switch (choice) {
		case 1:
			queryString.append("AC");
			break;
		case 2:
			queryString.append("Non-AC");
			break;
		case 3:
			break;
		default: throw new InputExceptions("Choose a number between 1 to 3");
			
		}
		
		queryString.append(".*");
		
		System.out.println("Choose any how to filter the result");
		System.out.println("1. Sleeper");
		System.out.println("2. Seater");
		System.out.println("3. Both");
		
		System.out.println("Enter your choice");
		inputString = scanner.nextLine();
		
		choice = 0;
		try {
			choice = Integer.parseInt(inputString);
		}catch (NumberFormatException e) {
			System.out.println("Please enter a number");
			return;
		}
		
		switch (choice) {
		case 1:
			queryString.append("Sleeper");
			break;
		case 2:
			queryString.append("Seater");
			break;
		case 3:
			break;
		default: throw new InputExceptions("Choose a number between 1 to 3");
			
		}
		
		ArrayList<Bus> busesArrayList =  database.showFilteredAndOrderedData(String.valueOf(queryString));
		
		int idx = 1;
		for(Bus bus : busesArrayList) {
			bus.displayFilteredResults(idx ++);
		}
	}
	
	void showBusSummary(Database database, Scanner scanner) throws SQLException {
		ArrayList<Bus> allBusesArrayList = database.selectAllBuses();
		int idx = 1;
		for(Bus bus : allBusesArrayList) {
			bus.displayBusNameWithIndex(idx++);
		}
		System.out.println("Select bus id to display information");
		String inputString = scanner.nextLine();
		int choice = 0;
		try {
			choice = Integer.parseInt(inputString);
		} catch (NumberFormatException e) {
			System.out.println("Please enter a number");
			return;
		}
		if(choice < 1 || choice > allBusesArrayList.size()) {
			System.out.println("Please enter a number between 1 and " + allBusesArrayList.size());
			return;
		}
		
		int selectId = allBusesArrayList.get(choice -1).id;
		String nameString = allBusesArrayList.get(choice - 1).busTypeString;
		int ticketFare = allBusesArrayList.get(choice - 1).fare;
		
		ArrayList<Seats> bookedSeatsArrayList = database.getBookedSeats(selectId);
		int bookedSeats = bookedSeatsArrayList.size();
		int cancelledTicketCount = database.countCancelledSeats(selectId);
		int totalFareCollected = bookedSeats * ticketFare;
		
		if(nameString.toLowerCase().contains("sleeper")){
			totalFareCollected += cancelledTicketCount * ticketFare * 0.5;
		}else {
			totalFareCollected += cancelledTicketCount * ticketFare * 0.25;
		}
		
		System.out.println(nameString);
		System.out.println("Number of seats filled: " + bookedSeats);
		System.out.print("Total fare collected: " + totalFareCollected);
		System.out.format(" ( %s tickets + %s cancellation )\n", bookedSeats, cancelledTicketCount);
		
		System.out.format("%-5s %-10s %-5s %-10s %-5s %-10s \n", "id", "bookedBy", "seatNo", "name", "gender", "copassengerFemaleOnly");
		for(Seats seat : bookedSeatsArrayList) {
			seat.displayInfo();
		}
		
		
	}
}
