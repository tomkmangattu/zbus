package zbus;

import java.util.Scanner;

public class User {
	int id;
	String name;
	long phoneNumber;
	int age;
	char gender;
	
	
	void signUp() throws Exception {
		Scanner scanner = new Scanner(System.in);
		System.out.println("Enter user details (userId name phoneNumber age gender(M/F) : ");
		String input = scanner.nextLine();
		scanner.close();
		
		String[] inputStrings = input.split(" ");
		if(inputStrings.length != 5) {
			throw new Exception("Some inputs are missing in " + input);
		}
		
		id = Integer.parseInt(inputStrings[0]);
		name = inputStrings[1];
		phoneNumber = Long.parseLong(inputStrings[2]);
		age = Integer.parseInt(inputStrings[3]);
		gender = inputStrings[4].charAt(0);
		
		displayUserDetails();
		scanner.close();
		
	}
	

	
	void displayUserDetails() {
		System.out.println("Displaying user details");
		System.out.println("id :" + id);
		System.out.println("Name :" + name);
		System.out.println("Phone number :" + phoneNumber);
		System.out.println("Age :" + age);
		System.out.println("Gender :" + gender  + "\n");
	}
}
