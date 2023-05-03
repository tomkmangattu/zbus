package zbus;

import java.sql.SQLException;

public class SeaterBus extends Bus{
	int seatsRight;
	int seatsLeft;
	Seats[] seats;
	
	
	public SeaterBus(int id, String busTypeString, int totalSeats, int seatsRight, int seatsLeft) {
		super(id, busTypeString, totalSeats, 0);
		this.seatsLeft = seatsLeft;
		this.seatsRight = seatsRight;
		this.seats = new Seats[totalSeats];
	}
	
	public SeaterBus(Bus bus) {
		super(bus.id, bus.busTypeString, bus.totalSeats, bus.bookedSeats);
	}
	
	@Override
	void displayAllSeats() {
		System.out.println("Displaying information about all seats in " + busTypeString);
		System.out.format("%-6s %-20s %-10s \n", "No" , "Passenger Name", "Gender");
		int seatsInOneRow = seatsLeft + seatsRight;
		
		for(int i = 0; i< totalSeats; i++) {
			if(i % seatsInOneRow == 0) {
				System.out.println("__________________________________");
			}
			else if (i % seatsInOneRow == seatsRight) {
				System.out.println("- - - - - - - - - - - - - - - - - ");
			}
			
			if(seats[i] != null) {
				System.out.format("%-6s %-20s %-10s \n", (i+1) , seats[i].nameString, seats[i].gender);
			}
			else {
				System.out.format("%-6s %-20s %-10s \n", (i+1) , "-", "-");
				
			}
		}
	}
	
	@Override
	void initSeats(Seats[] seats) {
		this.seats = seats;
	}
	
	@Override
	void bookTicket(int busNo, int seatNo, String passengerName, char gender,
			boolean coPassengerOnlyFemale,  Database database, int userId) throws SQLException, BookingException {
		
		boolean isSeatEmpty = database.checkIfSeatIsEmpty(busNo, seatNo);
		boolean femaleOnlyConditionPresent = false;
		
		if(isSeatEmpty) {
			int totalSeatsPerRow = seatsLeft + seatsRight;
			if(gender == 'm') {
				int positionOnRow = seatNo % totalSeatsPerRow;
				switch(positionOnRow) {
				case 0 : femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo - 1, userId);
						break;
				case 4: femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo + 1, userId);
						if(!femaleOnlyConditionPresent) {
							femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo - 1, userId);
						}
						break;
				case 3: femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo + 1, userId);
						break;
				case 2: femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo - 1, userId);
						break;
				case 1: femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo + 1, userId);
						break;
				}
				if(!femaleOnlyConditionPresent) {
					database.bookTicket(userId, busNo, seatNo, passengerName, gender, coPassengerOnlyFemale);
				}else {
					throw new BookingException("Sorry booking failed copassenger has choosed to only travel with female passenger");
				}
			}else {
				database.bookTicket(userId, busNo, seatNo, passengerName, gender, coPassengerOnlyFemale);
			}
		}else {
			throw new BookingException("Sorry the seat you choose is occupied");
		}
		
	}
}
