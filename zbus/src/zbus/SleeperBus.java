package zbus;

import java.sql.SQLException;

public class SleeperBus extends Bus {
	int seatsRight;
	int seatsLeft;
	Seats[] seats;

	public SleeperBus(int id, String busTypeString, int totalSeats, int seatsLeft, int seatsRight) {
		super(id, busTypeString, totalSeats, 0);
		this.seatsLeft = seatsLeft;
		this.seatsRight = seatsRight;

	}

	public SleeperBus(Bus bus) {
		super(bus.id, bus.busTypeString, bus.totalSeats, bus.bookedSeats);
	}

	@Override
	void displayAllSeats() {
		System.out.println("Displaying information about all seats in " + busTypeString);
		System.out.format("%-6s %-20s %-10s \n", "No", "Passenger Name", "Gender");
		int seatsInOneRow = seatsLeft + seatsRight;

		for (int i = 0; i < totalSeats; i++) {
			if (i % seatsInOneRow == 0) {
				System.out.println("__________________________________");
			} else if ((i - seatsLeft) % seatsInOneRow == 0) {
				System.out.println("- - - - - - - - - - - - - - - - - ");
			}

			if (seats[i] != null) {
				System.out.format("%-6s %-20s %-10s \n", (i + 1), seats[i].nameString, seats[i].gender);
			} else {
				System.out.format("%-6s %-20s %-10s \n", (i + 1), "-", "-");

			}
		}
	}

	@Override
	void initSeats(Seats[] seats) {
		this.seats = seats;
	}

	@Override
	void bookTicket(int busNo, int seatNo, String passengerName, char gender, boolean coPassengerOnlyFemale,
			Database database, int userId) throws SQLException, BookingException {

		boolean isSeatEmpty = database.checkIfSeatIsEmpty(busNo, seatNo);
		boolean femaleOnlyConditionPresent = false;

		if (isSeatEmpty) {
			int totalSeatsPerRow = seatsLeft + seatsRight;
			if (gender == 'm') {
				int positionOnRow = seatNo % totalSeatsPerRow;
				if (positionOnRow == 0) {

					femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo - 1, userId);

				} else if (positionOnRow == 2) {
					femaleOnlyConditionPresent = database.checkFemaleOnlyCondition(busNo, seatNo + 1, userId);
				}
				if (!femaleOnlyConditionPresent) {
					database.bookTicket(userId, busNo, seatNo, passengerName, gender, coPassengerOnlyFemale);
				} else {
					throw new BookingException(
							"Sorry booking failed copassenger has choosed to only travel with female passenger");
				}
			} else {
				database.bookTicket(userId, busNo, seatNo, passengerName, gender, coPassengerOnlyFemale);
			}
		} else {
			throw new BookingException("Sorry the seat you choose is occupied");
		}

	}

}
