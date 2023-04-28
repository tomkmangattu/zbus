package zbus;

public class SleeperBus extends Bus{

	public SleeperBus(int id, String busTypeString, int totalSeats, int bookedSeats) {
		super(id, busTypeString, totalSeats, bookedSeats);
		// TODO Auto-generated constructor stub
	}
	
	public SleeperBus(Bus bus) {
		super(bus.id, bus.busTypeString, bus.totalSeats, bus.bookedSeats);
	}

}
