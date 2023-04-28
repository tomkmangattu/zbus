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
}
