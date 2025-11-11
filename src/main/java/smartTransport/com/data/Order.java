package smartTransport.com.data;

import org.json.JSONObject;

import smartTransport.com.positionGps.PointGps;

public class Order {

	private PointGps depart;
	private PointGps arrive;
	private int pax;
	private long id;

	public JSONObject getCmdToJsonIN() {
		JSONObject cmdToJson = new JSONObject();
		
		cmdToJson.put("id", this.id);
		cmdToJson.put("type" , "IN");
		cmdToJson.put("Pax" , this.pax);
		cmdToJson.put("WGSLat" , this.depart.getwGSLat());
		cmdToJson.put("WGSLng" , this.depart.getwGSLng());
		
		return cmdToJson;
	}
	
	public JSONObject getCmdToJsonOUT() {
		JSONObject cmdToJson = new JSONObject();

		cmdToJson.put("id", this.id);
		cmdToJson.put("type" , "OUT");
		cmdToJson.put("Pax" , this.pax);
		cmdToJson.put("WGSLat" , this.arrive.getwGSLat());
		cmdToJson.put("WGSLng" , this.arrive.getwGSLng());
		
		return cmdToJson;
	}


	public int getPax() {
		return pax;
	}

	public void setPax(int pax) {
		this.pax = pax;
	}

	public Order(double departWGSLat, double deprtWGSLng, double finWGSLat, double finWGSLng, long id, int pax) {
		super();
		depart = new PointGps(deprtWGSLng, departWGSLat);
		arrive = new PointGps (finWGSLng, finWGSLat);
		this.id = id;
		this.pax = pax;
	}
	
	public PointGps getDepart() {
		return depart;
	}

	public void setDepart(PointGps depart) {
		this.depart = depart;
	}

	public PointGps getArrive() {
		return arrive;
	}

	public void setArrive(PointGps arrive) {
		this.arrive = arrive;
	}

	public long getId() {
		return id;
	}

	public void setId(long id) {
		this.id = id;
	}
	

}
