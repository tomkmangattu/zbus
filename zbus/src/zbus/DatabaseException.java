package zbus;

public class DatabaseException extends Exception {
	private String messageString;
	
	public DatabaseException(String messageString) {
		this.messageString = messageString;
	}
	
	@Override
	public String getMessage() {
		return messageString;
	}
}
