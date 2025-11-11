package smartTransport.com.graphOpperImpl;

public class RoutingResponse {
	
	private double distance; //Meters
	private long temps;  //secondes
	private long cout;
	
	public RoutingResponse(double dist, long temps, long d) {
		super();
		this.distance = dist;
		this.temps = temps;
		this.cout = d;
	}
	public double getDistance() {
		return distance;
	}
	public void setDistance(long distance) {
		this.distance = distance;
	}
	public long getTemps() {
		return temps;
	}
	public void setTemps(long temps) {
		this.temps = temps;
	}
	public long getCout() {
		return cout;
	}
	public void setCout(long cout) {
		this.cout = cout;
	}
}
