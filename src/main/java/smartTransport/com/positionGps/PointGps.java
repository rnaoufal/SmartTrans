package smartTransport.com.positionGps;

public class PointGps {
	
	private double wGSLng;
	private double wGSLat;
	
	public PointGps(double wGSLng, double wGSLat) {
		super();
		this.wGSLng = wGSLng;
		this.wGSLat = wGSLat;
	}
	public double getwGSLng() {
		return wGSLng;
	}
	public void setwGSLng(double wGSLng) {
		this.wGSLng = wGSLng;
	}
	public double getwGSLat() {
		return wGSLat;
	}
	public void setwGSLat(double wGSLat) {
		this.wGSLat = wGSLat;
	}	
}
