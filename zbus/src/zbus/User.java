package zbus;


public class User {
	int id;
	String name;
	long phoneNumber;
	int age;
	char gender;
	String userTypeString;
	
	public User() {
		
	}
	
	public User(int id, String name, long phoneNumber, int age, char gender, String userTypeString) {
		this.id = id;
		this.name = name;
		this.phoneNumber = phoneNumber;
		this.age = age;
		this.gender = gender;
		this.userTypeString = userTypeString;
	}
	
	void storeName(String name, int id) {
		this.name = name;
		this.id = id;
	}
	
	void storeUserDetails(long phoneNumber, int age, char gender) {
		this.phoneNumber = phoneNumber;
		this.age = age;
		this.gender = gender;
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
