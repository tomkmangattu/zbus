package zbus;

import java.sql.*;
import java.util.ArrayList;



public class Database {
	
	static final String DATABASE_URL = "jdbc:mysql://localhost:3306/zbus";
	static final String USERNAME = "root";
	static final String PASSWORD = "rootuser";
	Connection connection;
	
	
	public Database() throws SQLException, ClassNotFoundException {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(
			DATABASE_URL, USERNAME, PASSWORD);
		
		createTable();
		
	}
	
	void createTable() throws SQLException{
		
		// create customers table if not exist
		final String createCustomersTableQuery = """
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
			statement.executeUpdate(createCustomersTableQuery);
			
		} finally {
			if(statement != null)
				statement.close();
		}
		
	}
	
	boolean checkForSameUserName(String username) throws SQLException {
		
		String queryString ="""
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
		
		}finally {
			if(preparedStatement != null)
				preparedStatement.close();
		}
		
		
		
	}
	
	int createAccount(String username, String password) throws SQLException {
		final String queryString = 
				"insert into customers (name, passwordString)\n"
				+ "values(?, ?)";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setString(1, username);
			preparedStatement.setString(2, password);
			preparedStatement.executeUpdate();
			return getLastInsertedId();
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	int getLastInsertedId() throws SQLException{
		final String queryString = 
				"SELECT LAST_INSERT_ID()";
		Statement statement = null;
		try {
			statement = connection.createStatement();
			final ResultSet resultSet = statement.executeQuery(queryString);
			resultSet.next();
			return resultSet.getInt(1);
		}finally {
			if(statement != null)
				statement.close();
		}	
		
	}
	
	void insertUserDetails(int id, long phoneNumber, int age, char gender)throws SQLException {
		final String queryString = """
				UPDATE customers 
				set phoneNumber = ?, age = ?, gender = ?
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
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	User userLogin(String username, String password)throws SQLException, LoginFailedException {
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
			
			final ResultSet resultSet =  preparedStatement.executeQuery();
			if(resultSet.next()) {
				final int id = resultSet.getInt(1);
				final String nameString = resultSet.getString(2);
				final long phoneNumber = resultSet.getLong(4);
				final int age = resultSet.getInt(5);
				final char gender = resultSet.getString(6).charAt(0);
				return new User(id, nameString, phoneNumber, age, gender);
			}else {
				throw new LoginFailedException("User name or password is wrong");
			}
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	ArrayList<Bus> selectBusesWithSeatsMoreThan(int number) throws SQLException {
		final String queryString = """
				select bs.id, bs.bus_type, bs.total_seats, count(*)
				from buses bs
				join bookings bk 
				on bk.bus_id  = bs.id
				group by bs.id
				having bs.total_seats > count(*) + ?
				""";
		PreparedStatement preparedStatement = null;
		ArrayList<Bus> availableBuses = new ArrayList<Bus>();
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, number);
			
			final ResultSet resultSet = preparedStatement.executeQuery();
			while(resultSet.next()) {
				int idString = resultSet.getInt(1);
				String busTypeString = resultSet.getString(2);
				final int totalSeats = resultSet.getInt(3);
				final int availableSeats = resultSet.getInt(4);
				availableBuses.add(new Bus(idString, busTypeString, totalSeats, availableSeats));
			}
			return availableBuses;
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	Bus selectFullDetailsOfBus(int busId) throws SQLException, DatabaseException{
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
			int seatsRight ;
			int seatsLeft;
			
			if(resultSet.next()) {
				id = resultSet.getInt(1);
				busTypeString = resultSet.getString(2);
				totalSeats = resultSet.getInt(3);
				seatsRight = resultSet.getInt(4);
				seatsLeft = resultSet.getInt(5);
				
				if(busTypeString.toLowerCase().contains("sleeper")) {
					selectedBus = new SleeperBus(id, busTypeString, 
							totalSeats, seatsLeft, seatsRight);
				}else {
					selectedBus = new SeaterBus(id, busTypeString, 
							totalSeats, seatsLeft, seatsRight);
				}
				return selectedBus;
			}else {
				throw new DatabaseException("No details found for bus Id " + busId);
			}
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	Seats[] selectBookedSeatsOfBus(int busId, int totalSeats) throws SQLException{
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
			
			while(resultSet.next()) {
				id = resultSet.getInt(1);
				bookedBy = resultSet.getInt(2);
				seatNo = resultSet.getInt(4);
				passengerNameString = resultSet.getString(5);
				gender = resultSet.getString(6).charAt(0);
				femaleOnlyInNearBySeats = resultSet.getBoolean(7);
				seats[seatNo -1] = new Seats(id, bookedBy, seatNo, passengerNameString, gender, femaleOnlyInNearBySeats);
			}
			
			return seats;
			
		}finally {
			if(preparedStatement != null) {
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
			return ! (resultSet.next());
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	boolean checkFemaleOnlyCondition(int busNo, int seatNo, int userId) throws SQLException{
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
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
	
	void bookTicket(int userId, int busId, int seat, String name, char gender, boolean coPassengerFemaleOnly) throws SQLException {
		final String queryString = """
				insert into bookings(booked_by, bus_id, seat, name, gender, female_only) 
				values(?, ?, ?, ?, ?, ?)
				""";
		PreparedStatement preparedStatement = null;
		try {
			preparedStatement = connection.prepareStatement(queryString);
			preparedStatement.setInt(1, userId);
			preparedStatement.setInt(2, busId);
			preparedStatement.setInt(3, seat);
			preparedStatement.setString(4, name);
			preparedStatement.setString(5, String.valueOf(gender));
			preparedStatement.setBoolean(6, coPassengerFemaleOnly);
			
			int rowCount = preparedStatement.executeUpdate();
			System.out.println("rows affected" + rowCount);
			
		}finally {
			if(preparedStatement != null) {
				preparedStatement.close();
			}
		}
	}
}
