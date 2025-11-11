package smartTransport.com.configurationJsprit;

import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import smartTransport.com.data.Order;
import smartTransport.com.graphOpperImpl.GraphHopperImpl;
import smartTransport.com.graphOpperImpl.RoutingResponse;

public class JspritConfig {

	private static int sizeDimension = 0;

	public static Shipment.Builder creatShipment(Order order) {
		return Shipment.Builder.newInstance(String.valueOf(order.getId()))
				.addSizeDimension(sizeDimension, order.getPax())
				.setPickupLocation(Location.newInstance(("D" + order.getId())))
				.setDeliveryLocation(Location.newInstance(("A" + order.getId())));
	}

	public static RoutingResponse setCostMatrixShipment(Order order,
			VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder, String printWriterCostMatrix) {
		RoutingResponse reponse = GraphHopperImpl.computeRouteBetween(order.getArrive(), order.getDepart());
		costMatrixBuilder.addTransportDistance(("A" + order.getId()), ("D" + order.getId()), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("A" + order.getId()), ("D" + order.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "A" + order.getId() + "\t" + "D" + order.getId() + "\t" + reponse.getDistance()
				+ "\t" + reponse.getTemps());
		reponse = GraphHopperImpl.computeRouteBetween(order.getDepart(), order.getArrive());
		costMatrixBuilder.addTransportDistance(("D" + order.getId()), ("A" + order.getId()), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("D" + order.getId()), ("A" + order.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "D" + order.getId() + "\t" + "A" + order.getId() + "\t" + reponse.getDistance()
				+ "\t" + reponse.getTemps());
		
		return reponse;
	}

	public static RoutingResponse setCostMatrixDtoD(Order orderD, Order orderToD,
			VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder, String printWriterCostMatrix) {
		RoutingResponse reponse = GraphHopperImpl.computeRouteBetween(orderD.getDepart(), orderToD.getDepart());
		costMatrixBuilder.addTransportDistance(("D" + orderD.getId()), "D" + orderToD.getId(), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("D" + orderD.getId()), ("D" + orderToD.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "D" + orderD.getId() + "\t" + "D" + orderToD.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());
		reponse = GraphHopperImpl.computeRouteBetween(orderToD.getDepart(), orderD.getDepart());
		costMatrixBuilder.addTransportDistance(("D" + orderToD.getId()), "D" + orderD.getId(), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("D" + orderToD.getId()), ("D" + orderD.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "D" + orderToD.getId() + "\t" + "D" + orderD.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());

		return reponse;
	}

	public static RoutingResponse setCostMatrixDtoA(Order orderD, Order orderToA,
			VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder, String printWriterCostMatrix) {
		RoutingResponse reponse = GraphHopperImpl.computeRouteBetween(orderD.getDepart(), orderToA.getArrive());
		costMatrixBuilder.addTransportDistance(("D" + orderD.getId()), ("A" + orderToA.getId()), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("D" + orderD.getId()), ("A" + orderToA.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "D" + orderD.getId() + "\t" + "A" + orderToA.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());
		reponse = GraphHopperImpl.computeRouteBetween(orderToA.getArrive(), orderD.getDepart());
		costMatrixBuilder.addTransportDistance(("A" + orderToA.getId()), ("D" + orderD.getId()), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("A" + orderToA.getId()), ("D" + orderD.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "A" + orderToA.getId() + "\t" + "D" + orderD.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());

		return reponse;
	}

	public static RoutingResponse setCostMatrixAtoD(Order orderA, Order orderToD,
			VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder, String printWriterCostMatrix) {
		RoutingResponse reponse = GraphHopperImpl.computeRouteBetween(orderA.getArrive(), orderToD.getDepart());
		costMatrixBuilder.addTransportDistance(("A" + orderA.getId()), "D" + orderToD.getId(), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("A" + orderA.getId()), ("D" + orderToD.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "A" + orderA.getId() + "\t" + "D" + orderToD.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());
		reponse = GraphHopperImpl.computeRouteBetween(orderToD.getDepart(), orderA.getArrive());
		costMatrixBuilder.addTransportDistance(("D" + orderToD.getId()), "A" + orderA.getId(), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("D" + orderToD.getId()), ("A" + orderA.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "D" + orderToD.getId() + "\t" + "A" + orderA.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());

		return reponse;
	}

	public static RoutingResponse setCostMatrixAtoA(Order orderA, Order orderToA,
			VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder, String printWriterCostMatrix) {
		RoutingResponse reponse = GraphHopperImpl.computeRouteBetween(orderA.getArrive(), orderToA.getArrive());
		costMatrixBuilder.addTransportDistance(("A" + orderA.getId()), "A" + orderToA.getId(), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("A" + orderA.getId()), ("A" + orderToA.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "A" + orderA.getId() + "\t" + "A" + orderToA.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());
		reponse = GraphHopperImpl.computeRouteBetween(orderToA.getArrive(), orderA.getArrive());
		costMatrixBuilder.addTransportDistance(("A" + orderToA.getId()), "A" + orderA.getId(), reponse.getDistance());
		costMatrixBuilder.addTransportTime(("A" + orderToA.getId()), ("A" + orderA.getId()), reponse.getTemps());
		printWriterCostMatrix += ("\n" + "A" + orderToA.getId() + "\t" + "A" + orderA.getId() + "\t"
				+ reponse.getDistance() + "\t" + reponse.getTemps());

		return reponse;
	}

}
