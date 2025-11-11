package smartTransport.com.algo;

import java.time.Instant;
import java.time.OffsetDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONObject;

import com.graphhopper.jsprit.core.algorithm.VehicleRoutingAlgorithm;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit;
import com.graphhopper.jsprit.core.algorithm.box.Jsprit.Parameter;
import com.graphhopper.jsprit.core.problem.Location;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem;
import com.graphhopper.jsprit.core.problem.VehicleRoutingProblem.FleetSize;
import com.graphhopper.jsprit.core.problem.cost.VehicleRoutingTransportCosts;
import com.graphhopper.jsprit.core.problem.job.Shipment;
import com.graphhopper.jsprit.core.problem.solution.VehicleRoutingProblemSolution;
import com.graphhopper.jsprit.core.problem.solution.route.VehicleRoute;
import com.graphhopper.jsprit.core.problem.solution.route.activity.PickupShipment;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TimeWindow;
import com.graphhopper.jsprit.core.problem.solution.route.activity.TourActivity;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleImpl.Builder;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleType;
import com.graphhopper.jsprit.core.problem.vehicle.VehicleTypeImpl;
import com.graphhopper.jsprit.core.util.Solutions;
import com.graphhopper.jsprit.core.util.VehicleRoutingTransportCostsMatrix;

import smartTransport.com.configurationJsprit.JspritConfig;
import smartTransport.com.data.Order;
import smartTransport.com.data.TimeSlotOrder;
import smartTransport.com.graphOpperImpl.GraphHopperImpl;
import smartTransport.com.graphOpperImpl.RoutingResponse;
import smartTransport.com.model.Journey;
import smartTransport.com.model.Step;
import smartTransport.com.model.WorkSetResponse;
import smartTransport.com.positionGps.PointGps;

public class PoolTwo {
	public PoolTwo(TimeSlotOrder alltimeSlot) {
		super();
		this.alltimeSlot = alltimeSlot;
		this.timeSlotRidesObject = new JSONObject();
	}

	private String costMatrixString = "";
	private String statsCompletString = "";
	private TimeSlotOrder alltimeSlot;
	private JSONObject timeSlotRidesObject;

	public void poolProcess() {

		/*
		 * get a vehicle type-builder and build a type with the typeId "vehicleType" and
		 * a capacity of 2
		 */
		VehicleTypeImpl.Builder vehicleTypeBuilder = VehicleTypeImpl.Builder.newInstance("vehicleType")
				.addCapacityDimension(0, 4).setFixedCost(MultiThreadPool.fixedCost)
				.setCostPerDistance(MultiThreadPool.costPerDistance);
		VehicleType vehicleType = vehicleTypeBuilder.build();
		

		/*
		 * get a vehicle-builder and build a vehicle located at (10,10) with type
		 * "vehicleType"
		 */
		Builder vehicleBuilder = VehicleImpl.Builder.newInstance("vehicle");
		vehicleBuilder.setStartLocation(Location.newInstance("0")); // set this to the depot Location.newInstance(34.047702,-5.004801)
		vehicleBuilder.setReturnToDepot(false).setEarliestStart(0);

		vehicleBuilder.setType(vehicleType);
		VehicleImpl vehicle = vehicleBuilder.build();

		/*
		 * build shipments at the required locations, each with a capacity-demand of 1.
		 * 4 shipments 1: (5,7)->(6,9) 2: (5,13)->(6,11) 3: (15,7)->(14,9) 4:
		 * (15,13)->(14,11)
		 */
		VehicleRoutingProblem.Builder vrpBuilder;

		TimeSlotOrder timeSlot;
		com.graphhopper.jsprit.core.problem.job.Shipment.Builder shipment;
		Shipment course;
		ArrayList<Order> timeSlotOrder;
		RoutingResponse reponse;
		VehicleRoutingTransportCosts costMatrix;
		VehicleRoutingProblem problem;
		VehicleRoutingAlgorithm algorithm;
		Collection<VehicleRoutingProblemSolution> solutions;
		VehicleRoutingProblemSolution bestSolution;
		long startTime, endTime;
		long odresPrice = 0;
		

		// define a matrix-builder building a symmetric matrix

		VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder = VehicleRoutingTransportCostsMatrix.Builder
				.newInstance(false);
		vrpBuilder = VehicleRoutingProblem.Builder.newInstance();
		vrpBuilder.addVehicle(vehicle);
		// taille de la flotte
		vrpBuilder.setFleetSize(FleetSize.INFINITE);

		timeSlot = alltimeSlot;
		timeSlotOrder = timeSlot.getOrdresList();
		costMatrixString += "\tTranche Horaire\t" + timeSlot.getDay() + "\t" + timeSlot.getHoure();

		startTime = System.currentTimeMillis();
		


		for (int j = 0; j < timeSlotOrder.size(); j++) {
			
			//System.out.println(timeSlotOrder.get(j));
			
			

			shipment = JspritConfig.creatShipment(timeSlotOrder.get(j));
			reponse = JspritConfig.setCostMatrixShipment(timeSlotOrder.get(j), costMatrixBuilder, costMatrixString);
			odresPrice = reponse.getCout() + odresPrice;
			shipment.setDeliveryTimeWindow(
					TimeWindow.newInstance(0, (reponse.getTemps() + MultiThreadPool.tempsDetour)))
					.setPickupTimeWindow(TimeWindow.newInstance(0, (MultiThreadPool.tempsPriseEncharge)));
			//change time windows
			course = shipment.build();
			vrpBuilder.addJob(course);
		}

		populateCostMatrix(timeSlotOrder, costMatrixBuilder, costMatrixString);
		

		endTime = System.currentTimeMillis();
		costMatrixString += "\n + tempsCalculEnSec_Matrice_Cout\t" + (endTime - startTime);

		costMatrix = costMatrixBuilder.build();
		
		
		System.out.println("CostMatrix computing is OK");
		System.out.println(costMatrix);

		vrpBuilder.setRoutingCost(costMatrix);
		problem = vrpBuilder.build();
		/*
		 * get the algorithm out-of-the-box.
		 */
		// paramétrage des stratégies
		algorithm = Jsprit.Builder.newInstance(problem).setProperty(Parameter.THREADS, "2").buildAlgorithm();

		/*
		 * and search a solution
		 */

		solutions = algorithm.searchSolutions();

		/*
		 * get the best
		 */
		bestSolution = Solutions.bestOf(solutions);
		System.out.println(bestSolution);
		/*
		 * print nRoutes and totalCosts of bestSolution
		 */
		// SolutionPrinter.print(bestSolution);
		// SolutionPrinter.print(problem, bestSolution, SolutionPrinter.Print.VERBOSE);
		// System.out.println(bestSolution);
		endTime = System.currentTimeMillis();
		// Add extract best solution
		
		
		//
		long price = generateJsonOutputResult(timeSlotRidesObject, bestSolution, timeSlot, odresPrice);
		statsCompletString = timeSlot.getDay() + " " + timeSlot.getHoure() + ";" + timeSlotOrder.size() + ";"
				+ bestSolution.getRoutes().size() + ";" + odresPrice + "; " + price + ";" + timeSlot.getSumPax() + ";"
				+ +((endTime - startTime) / 1000) + ";" + bestSolution.getUnassignedJobs().size();
		odresPrice = 0;

	}

	private long getPriceSlotRidesArray(JSONArray oneTimeSlotRidesArray) {
		JSONObject course, demande;
		JSONArray demandes;
		ArrayList<PointGps> points = new ArrayList<PointGps>();
		long totalPrice = 0;

		for (int n = 0; n < oneTimeSlotRidesArray.length(); n++) {
			course = (JSONObject) oneTimeSlotRidesArray.get(n);
			demandes = (JSONArray) course.get("course");
			points.clear();
			for (int k = 0; k < demandes.length(); k++) {
				demande = (JSONObject) demandes.get(k);
				points.add(new PointGps((Double) demande.get("WGSLng"), (Double) demande.get("WGSLat")));
			}
			RoutingResponse response = GraphHopperImpl.computeRouteBetween(points.toArray(new PointGps[points.size()]));
			totalPrice += response.getCout();
		}
		return totalPrice;
	}

	private void populateCostMatrix(ArrayList<Order> timeSlotOrder,
			VehicleRoutingTransportCostsMatrix.Builder costMatrixBuilder, String printCostMatrix) {
		for (int j = 0; j < timeSlotOrder.size(); j++) {
			costMatrixBuilder.addTransportDistance("0", ("D" + timeSlotOrder.get(j).getId()), 0);
			costMatrixBuilder.addTransportDistance("0", ("A" + timeSlotOrder.get(j).getId()), 0);
			costMatrixBuilder.addTransportTime("0", ("D" + timeSlotOrder.get(j).getId()), 0);
			costMatrixBuilder.addTransportTime("0", ("A" + timeSlotOrder.get(j).getId()), 0);

			costMatrixBuilder.addTransportDistance(("D" + timeSlotOrder.get(j).getId()), "0", 0);
			costMatrixBuilder.addTransportDistance(("A" + timeSlotOrder.get(j).getId()), "0", 0);
			costMatrixBuilder.addTransportTime(("D" + timeSlotOrder.get(j).getId()), "0", 0);
			costMatrixBuilder.addTransportTime(("A" + timeSlotOrder.get(j).getId()), "0", 0);

			for (int n = j + 1; n < timeSlotOrder.size(); n++) {
				// ==================== D vs D =========================
				JspritConfig.setCostMatrixDtoD(timeSlotOrder.get(j), timeSlotOrder.get(n), costMatrixBuilder,
						printCostMatrix);

				// printWriterCostMatrix.println(("D" + timeSlotOrder.get(j).getId()) + "\t" +
				// "D" + timeSlotOrder.get(n).getId());
				// ==================== D vs A =========================
				JspritConfig.setCostMatrixDtoA(timeSlotOrder.get(j), timeSlotOrder.get(n), costMatrixBuilder,
						printCostMatrix);

				// printWriterCostMatrix.println(("D" + timeSlotOrder.get(j).getId()) + "\t" +
				// "A" + timeSlotOrder.get(n).getId());
				// ==================== A vs D =========================
				JspritConfig.setCostMatrixAtoD(timeSlotOrder.get(j), timeSlotOrder.get(n), costMatrixBuilder,
						printCostMatrix);

				// printWriterCostMatrix.println(("A" + timeSlotOrder.get(j).getId()) + "\t" +
				// "D" + timeSlotOrder.get(n).getId());
				// ==================== A vs A =========================
				JspritConfig.setCostMatrixAtoA(timeSlotOrder.get(j), timeSlotOrder.get(n), costMatrixBuilder,
						printCostMatrix);
				// printWriterCostMatrix.println(("A" + timeSlotOrder.get(j).getId()) + "\t" +
				// "A" + timeSlotOrder.get(n).getId());

			}
		}
	}
/*	
	private void extractBestSolution(VehicleRoutingProblemSolution bestSolution)
	{   
		WorkSetResponse response = new WorkSetResponse();
        Map<String, Step> stepsById = new HashMap<>();
        List<Journey> journeys = new ArrayList<>(128);
        
        
        response.setTotalCost(Math.round(bestSolution.getCost()));
        response.setJourneys(journeys);
        
        

		for (VehicleRoute vehicleRoute : bestSolution.getRoutes())
		{
			List<Step> ss = new ArrayList<>(vehicleRoute.getActivities().size());
			Journey j = new Journey();
			for (TourActivity tourActivity : vehicleRoute.getActivities())
			{
				OffsetDateTime arrivalTime;
				double arrival;
				Step s = stepsById.get(tourActivity.getLocation().getId());
				arrival = tourActivity.getEndTime();
				arrivalTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Math.round(arrival)), ZoneId.of("UTC"));
				//ss.add(new Step(s.getId(), s.getOrderId(), s.getWay(), s.getLoad(), s.getPosition(), arrivalTime, j));
			}
		}
			
	}
*/
/*	
    private WorkSetResponse extractBestSolution(WorkSet workSet, VehicleRoutingProblemSolution bestSolution)
    {
        WorkSetResponse response = new WorkSetResponse();
        Map<String, Step> stepsById = new HashMap<>();
        List<Journey> journeys = new ArrayList<>(128);

        workSet.getJourneys().forEach(j -> j.getSteps().forEach(s -> stepsById.put(s.getId(), s)));
        response.setTotalCost(Math.round(bestSolution.getCost()));
        response.setJourneys(journeys);
        for (VehicleRoute vehicleRoute : bestSolution.getRoutes())
        {
            List<Step> ss = new ArrayList<>(vehicleRoute.getActivities().size());
            Journey j = new Journey();
            boolean hasArrivalConstraint = vehicleRoute.getActivities().stream().map(TourActivity::getLocation).map(Location::getId).map(stepsById::get).map(
                    Step::getJourney).map(Journey::getConstraint).filter(Objects::nonNull).map(Constraint::getType).anyMatch(
                            Constraint.Type.DROPOFF_DT_MAX::equals);

            log.warn("hasArrivalConstraint={}", hasArrivalConstraint);
            for (TourActivity tourActivity : vehicleRoute.getActivities())
            {
                OffsetDateTime arrivalTime;
                double arrival;
                Step s = stepsById.get(tourActivity.getLocation().getId());

                if (tourActivity instanceof PickupShipment)
                {
                    PickupShipment pickupShipment = (PickupShipment) tourActivity;

                    arrival = hasArrivalConstraint ? pickupShipment.getTheoreticalLatestOperationStartTime() : tourActivity.getEndTime();
                }
                else
                {
                    DeliverShipment deliverShipment = (DeliverShipment) tourActivity;

                    arrival = hasArrivalConstraint ? deliverShipment.getTheoreticalLatestOperationStartTime() : tourActivity.getEndTime();
                }
                arrivalTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Math.round(arrival)), ZoneId.of("UTC"));
                ss.add(new Step(s.getId(), s.getOrderId(), s.getWay(), s.getLoad(), s.getPosition(), arrivalTime, j));
            }
            j.setSteps(ss);
            journeys.add(j);
        }
        return response;
    }
*/

	private long generateJsonOutputResult(JSONObject timeSlotRidesObject, VehicleRoutingProblemSolution bestSolution,
			TimeSlotOrder timeSlot, long odresPrice) {
		System.out.println(timeSlotRidesObject);
		// remplir timeSlotRidesObject
		
		
		//
		
		Iterator itJob, it = bestSolution.getRoutes().iterator();
		long idOrder = 0;
		ArrayList<Long> orderListGrouped = new ArrayList<Long>();
		JSONArray oneTimeSlotRidesArray = new JSONArray();
		
		System.out.println(oneTimeSlotRidesArray);

		while (it.hasNext()) {
			VehicleRoute r = (VehicleRoute) it.next();
			itJob = r.getActivities().iterator();
			JSONArray ordersGrouped = new JSONArray();
			JSONObject rideJson = new JSONObject();

			while (itJob.hasNext()) {
				OffsetDateTime arrivalTime;
				double arrival;
				TourActivity job = (TourActivity) itJob.next();
				idOrder = Long.parseLong((((TourActivity.JobActivity) job).getJob().getId()));
				arrival = job.getEndTime();
				//System.out.println("test"+Instant.ofEpochSecond(Math.round(arrival)));
				arrivalTime = OffsetDateTime.ofInstant(Instant.ofEpochSecond(Math.round(arrival)), ZoneId.of("UTC"));
				//System.out.println(arrivalTime);
				
				if (orderListGrouped.contains(idOrder)) {
					//timeSlot.getOrder(idOrder).getCmdToJsonOUT().append("time", arrivalTime);
					//System.out.println(timeSlot.getOrder(idOrder).getCmdToJsonOUT().put("time", arrivalTime));
					//System.exit(0);
					ordersGrouped.put(timeSlot.getOrder(idOrder).getCmdToJsonOUT().put("time", arrival));
				} else {
					ordersGrouped.put(timeSlot.getOrder(idOrder).getCmdToJsonIN().put("time", arrival));
					orderListGrouped.add(idOrder);
				}
			}
			rideJson.put("course", ordersGrouped);
			rideJson.put("ordersID", orderListGrouped.toString());
			oneTimeSlotRidesArray.put(rideJson);
			orderListGrouped.clear();
		}
		long slotPrice = getPriceSlotRidesArray(oneTimeSlotRidesArray);
		timeSlotRidesObject.put("prixAvecRegroupement", slotPrice);
		timeSlotRidesObject.put("prixSansRegroupement", odresPrice);
		timeSlotRidesObject.put("Courses", oneTimeSlotRidesArray);
		timeSlotRidesObject.put("heure", timeSlot.getHoure());
		timeSlotRidesObject.put("date", timeSlot.getDay());
		System.out.println("date: " + timeSlot.getDay() + " heure:" + timeSlot.getHoure());
		return slotPrice;
	}

	public String getPrintWriterCostMatrix() {
		return costMatrixString;
	}

	public String getPrintWriterStatsComplet() {
		return statsCompletString;
	}

	public JSONObject getTimeSlotRidesObject() {
		return timeSlotRidesObject;
	}
}