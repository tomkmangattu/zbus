package zbus;

import java.sql.*;

public class Database {
	
	static final String DATABASE_URL = "jdbc:mysql://localhost:3306/zbus";
	static final String USERNAME = "root";
	static final String PASSWORD = "rootuser";
	Connection connection;
	
	
	public Database() throws Exception {
		Class.forName("com.mysql.cj.jdbc.Driver");
		connection = DriverManager.getConnection(
			DATABASE_URL, USERNAME, PASSWORD);
		
		createTable();
		
	}
	
	void createTable() throws Exception{
		final Statement statement = connection.createStatement();
		// create customers table if not exist
		final String createCustomersTableQuery =
				"create table if not exists customers(\n"
				+ "	id int not null auto_increment,\n"
				+ "	name varchar(30) not null unique,\n"
				+ "	passwordString varchar(30) not null,\n"
				+ "	phoneNumber long not null,\n"
				+ "	age int,\n"
				+ "	gender varchar(1) not null,\n"
				+ "	primary key(id)	\n"
				+ ")";
		
		int count = statement.executeUpdate(createCustomersTableQuery);
		System.out.println(count);
		statement.close();
		
	}
	
	boolean checkForSameUserName(String username) throws Exception {
		
		final String queryString = 
				"select *\n"
				+ "from customers \n"
				+ "where name = ?";
		final PreparedStatement preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setString(1, username);
		ResultSet resultSet = preparedStatement.executeQuery();
		return resultSet.next();
	
	}
	
	void createAccount(String username, String password) throws Exception {
		final String queryString = 
				"insert into customers (name, passwordString)\n"
				+ "values(?, ?)";
		final PreparedStatement preparedStatement = connection.prepareStatement(queryString);
		preparedStatement.setString(1, username);
		preparedStatement.setString(2, password);
		
		int count = preparedStatement.executeUpdate();
		if(count == 1) {
			System.out.println("Account created successfully");
		}
	}
}
