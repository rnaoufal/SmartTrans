package smartTransport.com.graphOpperImpl;

import com.graphhopper.GHRequest;
import com.graphhopper.GHResponse;
import com.graphhopper.GraphHopper;
import com.graphhopper.PathWrapper;
import com.graphhopper.reader.osm.GraphHopperOSM;
import com.graphhopper.routing.util.EncodingManager;
import com.graphhopper.util.shapes.GHPoint;

import smartTransport.com.positionGps.PointGps;


public class GraphHopperImpl {

	private static String osmFile;

	private static String graphDir;

	static String vehicule;

	static String weighting;

	static GraphHopper graphHopper;

	public GraphHopperImpl(String osmFile, String graphDir, String vehicule, String weighting) {
		GraphHopperImpl.osmFile = osmFile;
		GraphHopperImpl.graphDir = graphDir;
		GraphHopperImpl.vehicule = vehicule;
		GraphHopperImpl.weighting = weighting;
		init();
	}

	public static RoutingResponse computeRouteBetween(PointGps... points) {

		RoutingResponse routingResponse = null;
		GHRequest request = new GHRequest();
		GHResponse response;

		for (PointGps point : points)
			request.addPoint(new GHPoint(point.getwGSLat(), point.getwGSLng()));
		request.setVehicle(vehicule);
		request.setWeighting(weighting);
		request.getHints().put("instructions", false);
		request.getHints().put("calc_points", false);
		response = graphHopper.route(request);

		if (!response.hasErrors()) {
			PathWrapper path = response.getBest(); 
			long dist = (long) (path.getDistance() +500L); // en metres et en long
			// pour modifier la fonction de coute il faut bien choisir ça distance (arrondi en mettre/en KM)
			routingResponse = new RoutingResponse(path.getDistance(), Math.round(path.getTime() / 1000), (dist *207) / 1000L + 440);
		} else {
			System.out.println("No response found - debug={} => " + response.getDebugInfo());
			System.out.println("No response found => " + response.getErrors().get(0));
		}
		return routingResponse;
	}

	private static void init() {
		graphHopper = new GraphHopperOSM().setStoreOnFlush(true).setCHEnabled(false).setDataReaderFile(osmFile)
				.setGraphHopperLocation(graphDir).setMinNetworkSize(200, 200)
				.setEncodingManager(new EncodingManager(new CustomFlagEncoderFactory(), vehicule, 4)).forDesktop();
		graphHopper.getCHFactoryDecorator().setPreparationThreads(3);
		graphHopper.importOrLoad();
	}
}
