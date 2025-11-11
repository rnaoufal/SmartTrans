package smartTransport.com.data;

import java.util.ArrayList;
import java.util.HashMap;

public class TimeSlotOrder {

	private HashMap<Long, Order> ordersHashMap;
	private ArrayList<Order> ordresList;
	private String houre;
	private String Day;

	public TimeSlotOrder(String houre, String Day) {
		super();
		this.houre = houre;
		this.Day = Day;
		ordersHashMap = new HashMap<Long, Order>();
		ordresList = new ArrayList<Order>();
	}

	public void addOrder(Order order) {
		ordersHashMap.put(order.getId(), order);
		ordresList.add(order);
	}
	
	public int getSumPax() {
		int sumPax = 0;
		for (Order order : this.ordresList) 
			sumPax += order.getPax();
		return sumPax;
	}

	public Order getOrder(long id) {
		return ordersHashMap.get(id);
	}

	public ArrayList<Order> getOrdresList() {
		return ordresList;
	}

	public void setOrdresList(ArrayList<Order> ordresList) {
		this.ordresList = ordresList;
	}

	public HashMap<Long, Order> getOrders() {
		return ordersHashMap;
	}

	public void setOrders(HashMap<Long, Order> orders) {
		this.ordersHashMap = orders;
	}

	public String getHoure() {
		return houre;
	}

	public void setHoure(String houre) {
		this.houre = houre;
	}

	public String getDay() {
		return Day;
	}

	public void setDay(String day) {
		Day = day;
	}
}
