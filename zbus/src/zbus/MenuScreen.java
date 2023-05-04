package zbus;

public class MenuScreen {
	void loginOrSignInScreen() {
		System.out.println("1. Sign to proceed");
		System.out.println("2. Login to proceed");
		System.out.println("3. Exit");
		System.out.println("Enter your choice :");
	}
	
	void signedInUserActions() {
		System.out.println("1. Book Tickets");
		System.out.println("2. Ticket Cancellation");
		System.out.println("3. Ticket Filtering");
		System.out.println("4. Exit");
	}
	
	void filteringOption1() {
		System.out.println("1. AC");
		System.out.println("2. Non AC");
		System.out.println("3. Both");
	}
	
	void filteringOption2() {
		System.out.println("1. Seater");
		System.out.println("2. Sleeper");
		System.out.println("3. Both");
	}
}
