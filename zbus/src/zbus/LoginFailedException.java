package zbus;

public class LoginFailedException  extends Exception {
	String messageString;
	
	public LoginFailedException(String messageString) {
		this.messageString = messageString;
	}
	
	@Override
	public String getMessage() {
		return messageString;
	}
}
