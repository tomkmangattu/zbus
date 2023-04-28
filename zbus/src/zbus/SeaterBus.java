package zbus;

public class SeaterBus extends Bus{

	public SeaterBus(int id, String busTypeString, int totalSeats, int bookedSeats) {
		super(id, busTypeString, totalSeats, bookedSeats);
	}
	
	public SeaterBus(Bus bus) {
		super(bus.id, bus.busTypeString, bus.totalSeats, bus.bookedSeats);
	}
}
