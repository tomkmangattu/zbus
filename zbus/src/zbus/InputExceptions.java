package zbus;

public class InputExceptions  extends Exception{
	private String messageString;
	
	public InputExceptions(String messageString) {
		this.messageString = messageString;
	}
	
	@Override
	public String getMessage() {
		return messageString;
	}
}
