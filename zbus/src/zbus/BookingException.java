package zbus;

public class BookingException extends Exception {
	private String messageString;
	
	public BookingException(String messageString) {
		this.messageString = messageString;
	}
	
	@Override
	public String getMessage() {
		return messageString;
	}
}