package zbus;

import java.sql.SQLException;
import java.util.Scanner;

public class Application {

	static boolean userSignedIn = false;
	static Database database;
	static User user;
	static Scanner scanner = new Scanner(System.in);
	static Booking booking;
	static Viewing viewing;

	static boolean siginOrLogin() throws Exception {

		String choiceString;

		System.out.println("1. Sign to proceed");
		System.out.println("2. Login to proceed");
		System.out.println("3. Exit");
		System.out.println("Enter your choice :");

		choiceString = scanner.nextLine();
		int choice = 0;

		try {
			choice = Integer.parseInt(choiceString);
		} catch (NumberFormatException e) {
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
		case 3:
			return true;

		default:
			System.out.println("choice should be 1 or 3");
		}

		return false;

	}

	static void createAccount() throws SQLException {
		String username = "";
		boolean sameAccountExists = true;

		while (sameAccountExists) {
			System.out.println("Enter username :");
			username = scanner.nextLine();
			sameAccountExists = database.checkForSameUserName(username);
			if (sameAccountExists)
				System.out.println("Sorry an account with same user name already exists");

		}

		System.out.println("Enter account password :");
		String passwordString = scanner.nextLine();
		int id = database.createAccount(username, passwordString);

		user = new User();
		user.storeName(username, id);
		getAccountDetails();
	}

	static void getAccountDetails() throws SQLException {
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

	static void login() throws SQLException {
		String usernameString = "";
		String passwordString = "";
		User newUser = null;

		while (newUser == null) {
			System.out.println("Enter your user name :");
			usernameString = scanner.nextLine();
			System.out.println("Enter password");
			passwordString = scanner.nextLine();

			try {
				newUser = database.userLogin(usernameString, passwordString);
			} catch (LoginFailedException e) {
				System.out.println(e.getMessage() + " try again");
			}

		}
		userSignedIn = true;
		user = newUser;
		user.displayUserDetails();

	}

	static boolean mainMenu() throws SQLException {

		try {
			System.out.println("1. Book Tickets");
			System.out.println("2. Ticket Cancellation");
			System.out.println("3. Ticket Filtering");
			System.out.println("4. Log out");

			if ("admin".equalsIgnoreCase(user.userTypeString)) {
				System.out.println("5. Bus summary");
				System.out.println("6. Exit");
			} else {
				System.out.println("5. Exit");
			}
			String choiceString = scanner.nextLine();
			int choice = 0;
			try {
				choice = Integer.parseInt(choiceString);
			} catch (NumberFormatException e) {
				System.out.println("Please enter a number");
				return false;
			}
			switch (choice) {
			case 1:
				booking.startBooking(scanner, database, user.id);
				break;
			case 2:
				booking.cancelTickets(scanner, database, user.id);
				break;
			case 3:
				viewing.showFilteringOptions(scanner, database);
				break;
			case 4:
				userSignedIn = false;
				break;
			case 5:
				if ("admin".equalsIgnoreCase(user.userTypeString)) {
					viewing.showBusSummary(database, scanner);
				} else {
					return true;
				}
				break;
			case 6:
				return true;
			default:
				System.out.println("Please enter a value within options");
			}

		} catch (InputExceptions e) {
			System.out.println(e.getMessage());
		} catch (DatabaseException e) {
			System.out.println(e.getMessage());
		}

		return false;
	}

	public static void main(String[] args) {

		System.out.println("Welecome to Zbus");
		boolean exit = false;

		try {
			database = new Database();
			booking = new Booking();
			viewing = new Viewing();

			while (!exit) {
				if (userSignedIn) {
					exit = mainMenu();
				} else {
					exit = siginOrLogin();
				}
			}

		} catch (Exception e) {
			System.out.println(e.getMessage());
		}

	}
}
