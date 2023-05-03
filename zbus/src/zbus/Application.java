package zbus;

import java.sql.SQLException;
import java.util.Scanner;

public class Application {

	static boolean userSignedIn = false;
	static MenuScreen menuScreen = new MenuScreen();
	static Database database;
	static User user;
	static Scanner scanner = new Scanner(System.in);
	static Booking booking;

	static boolean siginOrLogin() throws Exception {

		String choiceString;

		menuScreen.loginOrSignInScreen();
		choiceString = scanner.nextLine();
		int choice = 0;
		try {
			choice = Integer.parseInt(choiceString);
		}catch (NumberFormatException e) {
			System.out.println("Please enter a number");
			return false;
		}

		switch (choice) {
		case 1:
			createAccount();
			break;
		case 2:
			login();
			break;
		case 3: return true;
			
		default:
			System.out.println("choice should be 1 or 3");
		}
		
		return false;

	}

	
	static void createAccount() throws SQLException {
		String username = "";
		boolean sameAccountExists = true;
		
		while(sameAccountExists) {
			System.out.println("Enter username :");
			username = scanner.nextLine();
			sameAccountExists = database.checkForSameUserName(username);
			if(sameAccountExists)
				System.out.println("Sorry an account with same user name already exists");
			
		}
		
		System.out.println("Enter account password :");
		String passwordString = scanner.nextLine();
		int id =  database.createAccount(username, passwordString);
		
		user = new User();
		user.storeName(username, id);
		getAccountDetails();
	}

	static void getAccountDetails() throws SQLException{
		boolean gotDetails = false;
		long phoneNumber = 0;
		int age = 0;
		char gender = 'm';

		while (!gotDetails) {

			System.out.println("Enter user details (phoneNumber age gender(M/F) : ");
			String input = scanner.nextLine();
			String[] inputStrings = input.split(" ");
			gotDetails = true;

			if (inputStrings.length != 3) {
				gotDetails = false;
				System.out.println("Some inputs are missing in " + input);

			}

			try {
				phoneNumber = Long.parseLong(inputStrings[0]);
			} catch (NumberFormatException e) {
				System.out.println("Mistake in phone number format " + inputStrings[0]);
				gotDetails = false;
			}

			try {
				age = Integer.parseInt(inputStrings[1]);
			} catch (NumberFormatException e) {
				System.out.println("Mistake in age number format " + inputStrings[1]);
				gotDetails = false;
			}

			char genderChar = inputStrings[2].toLowerCase().charAt(0);
			if (genderChar == 'm' || genderChar == 'f') {
				gender = inputStrings[2].charAt(0);
			} else {
				System.out.println("Gender must be m or f");
				gotDetails = false;
			}

			if (!gotDetails) {
				System.out.print(" Please try again");
			}

		}
		
		database.insertUserDetails(user.id, phoneNumber, age, gender);
		
		user.storeUserDetails(phoneNumber, age, gender);
		userSignedIn = true;
		user.displayUserDetails();
		
	}
	
	static void login() throws SQLException{
		String usernameString = "";
		String passwordString = "";
		User newUser = null;
		
		while(newUser == null) {
			System.out.println("Enter your user name :");
			usernameString = scanner.nextLine();
			System.out.println("Enter password");
			passwordString = scanner.nextLine();
			
			try {
				newUser=  database.userLogin(usernameString, passwordString);
			} catch (LoginFailedException e) {
				System.out.println(e.getMessage() + " try again");
			}
			
		}
		userSignedIn = true;
		user = newUser;
		user.displayUserDetails();
		
	}
	
	static boolean mainMenu() throws InputExceptions, SQLException, DatabaseException {
		menuScreen.signedInUserActions();
		String choiceString = scanner.nextLine();
		int choice = 0;
		try {
			choice = Integer.parseInt(choiceString);
		}catch (NumberFormatException e) {
			System.out.println("Please enter a number");
			return false;
		}
		switch(choice) {
		case 1:	booking.startBooking(scanner, database, user.id);
		break;
		}
		
		return false;
	}
	
	

	public static void main(String[] args) {

		System.out.println("Welecome to Zbus");
		boolean exit = false;

		try {
			database = new Database();
			booking = new Booking();
			
			while(! exit) {
				if (userSignedIn) {
					mainMenu();
				} else {
					exit = siginOrLogin();
				}
			}
			
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
