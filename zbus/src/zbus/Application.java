package zbus;

import java.util.Scanner;

public class Application {
	
	static boolean userSignedIn = false;
	static MenuScreen menuScreen = new MenuScreen();
	static Database database;
	
	static {
		try {
			database = new Database();
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
	}
	
	
	static void siginOrLogin() throws Exception{
		Scanner scanner = new Scanner(System.in);
		String choiceString;
		
		
		menuScreen.loginOrSignInScreen();
		choiceString = scanner.nextLine();
		int choice = Integer.parseInt(choiceString);
		
		switch(choice) {
		case 1: createAccount();
			break;
		case 2:
			break;
		default: System.out.println("choice should be 1 or 2");
		}
		
	}
	
	static void createAccount() throws Exception {
		String username;
		Scanner scanner = new Scanner(System.in);
		boolean sameAccountExists = true;
		while(sameAccountExists) {
			System.out.println("Enter username :");
			username = scanner.nextLine();
			sameAccountExists = database.checkForSameUserName(username);
			if(sameAccountExists) {
				System.out.println("Sorry an account with same user name already exists");
			}else {
				System.out.println("Enter account password :");
				String passwordString = scanner.nextLine();
				database.createAccount(username, passwordString);
			}
		}
		scanner.close();
	}
	
	public static void main(String[] args) throws Exception{
		

		System.out.println("Welecome to Zbus");

		if(userSignedIn) {
			
		}else {
			siginOrLogin();
		}

		
	}
}
