package zbus;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Scanner;

public class Booking {

	ArrayList<Bus> availableBuses;
	int seatsNeeded = 0;
	Bus selectedBus;
	int userId;

	void startBooking(Scanner scanner, Database database, int userId)
			throws InputExceptions, SQLException, DatabaseException {
		this.userId = userId;
		System.out.println("Enter the number of seats you want to book :");
		String inputString = scanner.nextLine();
		int number = 0;
		try {
			number = Integer.parseInt(inputString);
		} catch (NumberFormatException e) {
			throw new InputExceptions("Please enter a number");
		}
		seatsNeeded = number;

		int busNumber = selectbus(number, database, scanner);

		selectedBus = database.selectFullDetailsOfBus(busNumber);
		selectedBus.initSeats(database.selectBookedSeatsOfBus(busNumber, selectedBus.totalSeats));

		bookSeats(scanner, database);

	}

	void bookSeats(Scanner scanner, Database database) throws SQLException {
		int seatsToBeBooked = seatsNeeded;
		String inputString;

		Passenger[] passengers = new Passenger[seatsNeeded];
		boolean canBeBooked = false;

		selectedBus.displayAllSeats();
		while (seatsToBeBooked != 0) {

			canBeBooked = false;
			System.out.println("remaining " + seatsToBeBooked + " to be booked");
			Passenger passenger;
			try {
				passenger = getPassengerDetails(scanner, selectedBus.fare);
			} catch (InputExceptions e) {
				System.out.println(e.getMessage());
				continue;
			}

			try {
				canBeBooked = selectedBus.bookTicket(selectedBus.id, passenger.seat, passenger.gender, database,
						userId);
			} catch (BookingException e) {
				System.out.println(e.getMessage());
				System.out.println("Booking failed");
				continue;
			}
			if (canBeBooked) {
				passengers[seatsNeeded - seatsToBeBooked] = passenger;
				seatsToBeBooked--;
			}

		}

		System.out.println("Total fare will for " + seatsNeeded + " is equal to " + (seatsNeeded * selectedBus.fare));
		System.out.println("Do you conform booking (y/n)");
		
		inputString = scanner.nextLine();
		if (inputString.toLowerCase().charAt(0) == 'y') {
			for (int idx = 0; idx < seatsNeeded; idx++) {
				database.bookTicket(userId, selectedBus.id, passengers[idx]);
			}
		}else {
			System.out.println("Tickets are cancelled");
		}

	}

	void cancelTickets(Scanner scanner, Database database, int userId) throws SQLException {
		this.userId = userId;
		ArrayList<Ticket> tickets = database.showTicketsBookedByUser(userId);
		
		if(! tickets.isEmpty()) {
			displayTickets(tickets);
			System.out.println("1. Cancel all the tickets");
			System.out.println("2. Cancel only specific tickets");
			System.out.println("Enter your choice :");

			String inputString = scanner.nextLine();
			int choice = 0;
			try {
				choice = Integer.parseInt(inputString);
			} catch (NumberFormatException e) {
				System.out.println("Please Enter a number");
				return;
			}

			switch (choice) {
			case 1:
				database.deleteAllTickets(userId);
				break;
			case 2:
				deleteSpecificTickets(scanner, tickets, database);
				break;
			default:
				break;
			}
		}else {
			System.out.println("You have not booked any tickets");
		}
		
	}

	void deleteSpecificTickets(Scanner scanner, ArrayList<Ticket> tickets, Database database) throws SQLException {
		System.out.println("Enter the numbers corresponding to the ticket you have to delete (seperated by spaces)");
		String inputString = scanner.nextLine();
		String[] indexStrings = inputString.split(" ");
		ArrayList<Integer> ticketIdsToDelete = new ArrayList<>();
		int refund = 0;

		for (String indexString : indexStrings) {
			try {
				int index = Integer.parseInt(indexString);
				if (index <= tickets.size()) {
					Ticket ticketToDelete = tickets.get(index - 1);

					if (ticketToDelete.busTypeString.toLowerCase().contains("sleeper")) {
						refund += ticketToDelete.ticketFare * 0.5;
					} else {
						refund += ticketToDelete.ticketFare * 0.75;
					}
					ticketIdsToDelete.add(tickets.get(index - 1).id);

				}
			} catch (NumberFormatException e) {
				System.out.println("Expected numbers not able understand " + indexString);
			}
		}
		System.out.println("Refund amount is :" + refund);
		database.deleteTickets(ticketIdsToDelete);

	}

	void displayTickets(ArrayList<Ticket> tickets) {
		int idx = 0;
		System.out.format(" %-5s %-5s %-10s %-6s %-20s %-6s %-10s \n", "No", "Id", "Bus Type", "seat No",
				"Passenger name", "gender", "co passenger FemaleOnly");
		System.out.println("-------------------------------------------------------------------");
		for (Ticket ticket : tickets) {
			ticket.displayTicketWithIndex(idx + 1);
			idx++;
		}
	}

	Passenger getPassengerDetails(Scanner scanner, int fare) throws InputExceptions {
		String inputString;
		int seatNo;
		String passengerName;
		char gender;
		boolean coPassengerFemaleOnly = false;

		System.out.println("Enter the seat number you want to book :");
		inputString = scanner.nextLine();
		try {
			seatNo = Integer.parseInt(inputString);
		} catch (NumberFormatException e) {
			throw new InputExceptions("Please enter a number");

		}

		System.out.println("Enter name of the passenger :");
		passengerName = scanner.nextLine();

		System.out.println("Enter gender of the passenger (m/f) :");
		inputString = scanner.nextLine();
		gender = inputString.toLowerCase().charAt(0);
		if (gender != 'm' && gender != 'f') {
			throw new InputExceptions("Gender should be m or f");

		}
		if (gender == 'f') {
			System.out.println("Do you want to set co passenger as only female (y/n)");
			inputString = scanner.nextLine();
			char coPassengerCondition = inputString.toLowerCase().charAt(0);
			if (coPassengerCondition == 'y') {
				coPassengerFemaleOnly = true;
			}
		}

		return new Passenger(passengerName, seatNo, gender, coPassengerFemaleOnly, fare);

	}

	int selectbus(int number, Database database, Scanner scanner) throws SQLException, NumberFormatException, InputExceptions {
		availableBuses = database.selectBusesWithSeatsMoreThan(number);
		displayBusesWithAvailableSeats(availableBuses);

		System.out.println("Enter the bus number you want to book");
		String inputString = scanner.nextLine();
		int choice =  Integer.parseInt(inputString);
		if(choice > availableBuses.size()) {
			throw new InputExceptions("Please enter a valid number");
		}
		return choice;
	}

	void displayBusesWithAvailableSeats(ArrayList<Bus> availableBuses) {
		for (Bus bus : availableBuses) {
			bus.displayAvailableSeats();
		}
	}

}
