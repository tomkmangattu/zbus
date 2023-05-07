package zbus;

import java.sql.*;
import java.util.ArrayList;
import java.util.stream.Collectors;


public class Database {

	static final String DATABASE_URL = "jdbc:mysql://localhost:3306/zbus";
	static final String USERNAME = "root";
	static final String PASSWORD = "rootuser";
	Connection connection;

	public Database() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(DATABASE_URL, USERNAME, PASSWORD);

		createTable();

	}

	void createTable() throws SQLException {

		// create customers table if not exist
		String query = """
				create table if not exists customers(
				id int not null auto_increment,
				name varchar(30) not null unique,
				passwordString varchar(30) not null,
				phoneNumber long,
				age int,
				gender varchar(1),
				primary key(id)
				)
				""";

		Statement statement = null;

		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);

		} finally {
			if (statement != null)
				statement.close();
		}
		
		// create buses table if not exist
		query = """
				create table if not exists buses(
				id int not null unique,
				bus_type varchar(30) not null,
				total_seats int not null,
				seats_per_row_right int not null,
				seats_per_row_left int not null,
				primary key(id)
				);
				""";

		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);

		} finally {
			if (statement != null)
				statement.close();
		}
		

		// create values into buses table if not exist
		query = """
			insert ignore into buses(id, bus_type, total_seats, seats_per_row_right, seats_per_row_left)
			values	(1, "AC Sleeper", 30, 2, 1),
			(2, "AC Seater", 40, 3, 2),
			(3, "Non-AC Sleeper", 30, 2, 1),
			(4, "Non-AC Seater", 40, 3, 2)""";

		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);

		} finally {
			if (statement != null)
				statement.close();
		}
		
		// insert values into buses table if not exist
		query = """
			insert ignore into buses(id, bus_type, total_seats, seats_per_row_right, seats_per_row_left)
			values	(1, "AC Sleeper", 30, 2, 1),
			(2, "AC Seater", 40, 3, 2),
			(3, "Non-AC Sleeper", 30, 2, 1),
			(4, "Non-AC Seater", 40, 3, 2)""";

		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);

		} finally {
			if (statement != null)
				statement.close();
		}
		
		// create bookings table if not exist
		query = """
			create table if not exists bookings(
			id int not null auto_increment,
			booked_by int,
			bus_id int not null,
			seat int,
			name varchar(30),
			gender varchar(1),
			female_only boolean,
			unique(bus_id, seat),
			primary key(id),
			constraint booking_to_customer
				foreign key(booked_by)
				references customers(id)
				on delete set null,
			constraint booking_to_bus
				foreign key(bus_id)
				references buses(id) 
				on delete cascade
		)""";

		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);

		} finally {
			if (statement != null)
				statement.close();
		}
		
		// create cancelled bookings table if not exist
		query = """
			create table if not exists cancelled_bookings
			select * from bookings 
			where 3 = 2""";

		try {
			statement = connection.createStatement();
			statement.executeUpdate(query);

		} finally {
			if (statement != null)
				statement.close();
		}

	}

	boolean checkForSameUserName(String username) throws SQLException {

		String queryString = """
				select *
				from customers
				where name = ?
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			ResultSet resultSet = preparedStatement.executeQuery();
			return resultSet.next();

		} finally {
			if (preparedStatement != null)
				preparedStatement.close();
		}

	}

	int createAccount(String username, String password) throws SQLException {
		final String queryString = "insert into customers (name, passwordString)\n" + "values(?, ?)";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();
			return getLastInsertedId();
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	int getLastInsertedId() throws SQLException {
		final String queryString = "SELECT LAST_INSERT_ID()";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery(queryString);
			resultSet.next();
			return resultSet.getInt(1);
		} finally {
			if (statement != null)
				statement.close();
		}

	}

	void insertUserDetails(int id, long phoneNumber, int age, char gender) throws SQLException {
		final String queryString = """
				UPDATE customers
				set phoneNumber = ?, age = ?, gender = ?, userType = "user"
				where id = ?
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setLong(1, phoneNumber);
			preparedStatement.setInt(2, age);
			preparedStatement.setString(3, String.valueOf(gender));
			preparedStatement.setInt(4, id);

			preparedStatement.execute();
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	User userLogin(String username, String password) throws SQLException, LoginFailedException {
		final String queryString = """
				select *
				from customers
				where name = ? and passwordString = ?
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);

			final ResultSet resultSet = preparedStatement.executeQuery();
			if (resultSet.next()) {
				final int id = resultSet.getInt(1);
				String nameString = resultSet.getString(2);
				final long phoneNumber = resultSet.getLong(4);
				final int age = resultSet.getInt(5);
				final char gender = resultSet.getString(6).charAt(0);
				String userTypeString = resultSet.getString(7);
				return new User(id, nameString, phoneNumber, age, gender, userTypeString);
				
			} else {
				throw new LoginFailedException("User name or password is wrong");
			}
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	ArrayList<Bus> selectBusesWithSeatsMoreThan(int number) throws SQLException {
		final String queryString = """
				select bs.id, bs.bus_type, bs.total_seats, count(bk.id)
				from buses bs
				left join bookings bk 
				on bk.bus_id  = bs.id
				group by bs.id
				having bs.total_seats > count(*) + ?;
				""";
		PreparedStatement preparedStatement = null;
		ArrayList<Bus> availableBuses = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, number);

			final ResultSet resultSet = preparedStatement.executeQuery();
			while (resultSet.next()) {
				int idString = resultSet.getInt(1);
				String busTypeString = resultSet.getString(2);
				final int totalSeats = resultSet.getInt(3);
				final int availableSeats = resultSet.getInt(4);
				availableBuses.add(new Bus(idString, busTypeString, totalSeats, availableSeats, 0));
			}
			return availableBuses;
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	Bus selectFullDetailsOfBus(int busId) throws SQLException, DatabaseException {
		final String busQueryString = """
				select *
				from  buses
				where id = ?""";

		PreparedStatement preparedStatement = null;
		Bus selectedBus;

		try {
			preparedStatement = connection.prepareStatement(busQueryString);
			preparedStatement.setInt(1, busId);

			final ResultSet resultSet = preparedStatement.executeQuery();
			int id;
			String busTypeString;
			int totalSeats;
			int seatsRight;
			int seatsLeft;
			int fare;

			if (resultSet.next()) {
				id = resultSet.getInt(1);
				busTypeString = resultSet.getString(2);
				totalSeats = resultSet.getInt(3);
				seatsRight = resultSet.getInt(4);
				seatsLeft = resultSet.getInt(5);
				fare = resultSet.getInt(6);

				if (busTypeString.toLowerCase().contains("sleeper")) {
					selectedBus = new SleeperBus(id, busTypeString, totalSeats, seatsLeft, seatsRight, fare);
				} else {
					selectedBus = new SeaterBus(id, busTypeString, totalSeats, seatsLeft, seatsRight, fare);
				}
				return selectedBus;
			} else {
				throw new DatabaseException("No details found for bus Id " + busId);
			}
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	Seats[] selectBookedSeatsOfBus(int busId, int totalSeats) throws SQLException {
		final String bookingQueyString = """
				select *
				from bookings
				where bus_id = ?""";

		PreparedStatement preparedStatement = null;

		try {
			preparedStatement = connection.prepareStatement(bookingQueyString);
			preparedStatement.setInt(1, busId);

			final ResultSet resultSet = preparedStatement.executeQuery();

			Seats[] seats = new Seats[totalSeats];
			int id;
			int bookedBy;
			int seatNo;
			String passengerNameString;
			char gender;
			boolean femaleOnlyInNearBySeats;

			while (resultSet.next()) {
				id = resultSet.getInt(1);
				bookedBy = resultSet.getInt(2);
				seatNo = resultSet.getInt(4);
				passengerNameString = resultSet.getString(5);
				gender = resultSet.getString(6).charAt(0);
				femaleOnlyInNearBySeats = resultSet.getBoolean(7);
				seats[seatNo - 1] = new Seats(id, bookedBy, seatNo, passengerNameString, gender,
						femaleOnlyInNearBySeats);
			}

			return seats;

		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}

	}

	boolean checkIfSeatIsEmpty(int busNo, int seatNo) throws SQLException {
		final String queryString = """
				select *
				from bookings
				where bus_id = ?
				and seat = ?;
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, busNo);
			preparedStatement.setInt(2, seatNo);

			ResultSet resultSet = preparedStatement.executeQuery();
			return !(resultSet.next());
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	boolean checkFemaleOnlyCondition(int busNo, int seatNo, int userId) throws SQLException {
		final String queryString = """
				select *
				from bookings
				where bus_id = ?
				and seat = ?
				and booked_by != ?
				and female_only = TRUE;
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, busNo);
			preparedStatement.setInt(2, seatNo);
			preparedStatement.setInt(3, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			return resultSet.next();
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	void bookTicket(int userId, int busId, Passenger passenger)
			throws SQLException {
		final String queryString = """
				insert into bookings(booked_by, bus_id, seat, name, gender, female_only)
				values(?, ?, ?, ?, ?, ?)
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, busId);
			preparedStatement.setInt(3, passenger.seat);
			preparedStatement.setString(4, passenger.nameString);
			preparedStatement.setString(5, String.valueOf(passenger.gender));
			preparedStatement.setBoolean(6, passenger.coPassengerFemaleOnly);

			int rowCount = preparedStatement.executeUpdate();

		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	ArrayList<Ticket> showTicketsBookedByUser(int userId) throws SQLException {
		String queryString = """
				select bk.id, bs.bus_type, bk.seat, bk.name, bk.gender, bk.female_only , bs.ticket_fare
				from bookings bk
				join buses bs
				on bs.id = bk.bus_id
				where booked_by = ?
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, userId);

			ResultSet resultSet = preparedStatement.executeQuery();

			ArrayList<Ticket> tickets = new ArrayList<>();
			int id;
			String busTypeString;
			int seat;
			String nameString;
			char gender;
			boolean coPassengerFemaleOnly;
			int ticketFare;

			while (resultSet.next()) {
				id = resultSet.getInt(1);
				busTypeString = resultSet.getString(2);
				seat = resultSet.getInt(3);
				nameString = resultSet.getString(4);
				gender = resultSet.getString(5).charAt(0);
				coPassengerFemaleOnly = resultSet.getBoolean(6);
				ticketFare = resultSet.getInt(7);

				tickets.add(new Ticket(id, busTypeString, seat, nameString, gender, coPassengerFemaleOnly, ticketFare));
			}

			return tickets;

		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}

	void deleteAllTickets(int userId) throws SQLException {
		final String queryString = """
				delete from bookings
				where booked_by = ?;
				""";
		final String insertQueryString = """
				insert into cancelled_bookings (booked_by, bus_id, seat, name, gender, female_only)
				select booked_by, bus_id, seat, name, gender, female_only
				from bookings 
				where booked_by = ?
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(insertQueryString);
			preparedStatement.setInt(1, userId);
			int rowCount = preparedStatement.executeUpdate();
			preparedStatement.close();
			
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, userId);
			rowCount = preparedStatement.executeUpdate();
			
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	void deleteTickets(ArrayList<Integer> ticketIds) throws SQLException {
		
		String deleteQuery = """
				delete from bookings 
				where id in (
				""";
		String insertQueryString = """
				insert into cancelled_bookings (booked_by, bus_id, seat, name, gender, female_only)
				select booked_by, bus_id, seat, name, gender, female_only
				from bookings 
				where id in (
				""";
		String idString = ticketIds.stream().map(String::valueOf).collect(Collectors.joining(","));
		Statement statement = null;
		try {
			statement = connection.createStatement();
			int rowCount = statement.executeUpdate(insertQueryString + idString + ")");
			statement.close();
			
			statement = connection.createStatement();
			rowCount =  statement.executeUpdate(deleteQuery + idString + ")");
			
		} finally {
			if (statement != null) {
				statement.close();
			}
		}
	}
	
	ArrayList<Bus> showFilteredAndOrderedData(String filterString) throws SQLException {
		String filterQueryString = """
				select bs.id, bs.bus_type,  bs.total_seats - count(bk.id) remainingSeats
				from buses bs
				left join bookings bk
				on bs.id = bk.bus_id 
				where bs.bus_type REGEXP ?
				group by bs.id
				order by remainingSeats desc, bs.bus_type, substring_index(bs.bus_type, ' ', -1) desc
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(filterQueryString);
			preparedStatement.setString(1, filterString);
			
			ResultSet resultSet = preparedStatement.executeQuery();
			int id;
			String busTypeString;
			int availableSeats;
			ArrayList<Bus> filteredBuses = new ArrayList<>();
			
			while(resultSet.next()) {
				id = resultSet.getInt(1);
				busTypeString = resultSet.getString(2);
				availableSeats  = resultSet.getInt(3);
				
				filteredBuses.add(new Bus(id, busTypeString, availableSeats));
			}
			return filteredBuses;
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	ArrayList<Bus> selectAllBuses() throws SQLException {
		String queryString = """
				select id, bus_type, total_seats, ticket_fare 
				from buses
				""";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			ResultSet resultSet = statement.executeQuery(queryString);
			int id;
			String busTypeString;
			int totalSeats;
			int ticketFare;
			ArrayList<Bus> allBusesArrayList = new ArrayList<>();
			while(resultSet.next()) {
				id = resultSet.getInt(1);
				busTypeString = resultSet.getString(2);
				totalSeats = resultSet.getInt(3);
				ticketFare = resultSet.getInt(4);
				allBusesArrayList.add(new Bus(id, busTypeString, totalSeats, ticketFare));
			}
			
			return allBusesArrayList;
		}finally {
			if(statement != null) {
				statement.close();
			}
		}
	}
	
	ArrayList<Seats> getBookedSeats(int busId) throws SQLException {
		String queryString = """
				select *
				FROM bookings 
				WHERE bus_id = ?
				order by seat
				""";
		PreparedStatement preparedStatement = null;
		ArrayList<Seats> bookedSeats = new ArrayList<>();
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, busId);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			int id;
			int bookedBy;
			int seatNo;
			String passengerNameString;
			char gender;
			boolean femaleOnlyInNearBySeats;
			
			while(resultSet.next()) {
				id = resultSet.getInt(1);
				bookedBy = resultSet.getInt(2);
				seatNo = resultSet.getInt(4);
				passengerNameString = resultSet.getString(5);
				gender = resultSet.getString(6).charAt(0);
				femaleOnlyInNearBySeats = resultSet.getBoolean(7);
				
				bookedSeats.add(new Seats(id, bookedBy, seatNo, passengerNameString, gender,
						femaleOnlyInNearBySeats));
			}
			return bookedSeats;
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	int countCancelledSeats(int busId) throws SQLException {
		String queryString = """
				select count(*)
				from cancelled_bookings
				where bus_id = ?;
				""";
		int cancelledTicketCount = 0;
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, busId);
			ResultSet resultSet = preparedStatement.executeQuery();
			
			if(resultSet.next()) {
				cancelledTicketCount = resultSet.getInt(1);
			}
			return cancelledTicketCount;
			
		} finally {
			if (preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
}
