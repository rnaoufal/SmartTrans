package smartTransport.com.algo;

import java.io.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.*;

import org.json.JSONArray;
import org.json.simple.parser.ParseException;

import smartTransport.com.data.TimeSlotOrder;
import smartTransport.com.graphOpperImpl.GraphHopperImpl;
import smartTransport.com.readInputData.ReadJsonData;

public class MultiThreadPool {
	static Properties bundle = new Properties();

	static {
		try {
			bundle.load(PoolTwo.class.getResourceAsStream("/config.properties"));
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private static PrintWriter printWriterStatsComplet;
	private static PrintWriter printWriterCostMatrix;
	private static String fichierResultat = new SimpleDateFormat(bundle.getProperty("csv.fichierResultat")).format(new Date());

	private static String osmFile = bundle.getProperty("routing.graphopper.osmFile");
	private static String graphDir = bundle.getProperty("routing.graphopper.graphDir");
	private static String vehicule = bundle.getProperty("routing.graphopper.vehicule");
	private static String weighting = bundle.getProperty("routing.graphopper.weighting");

	private static String inputFile = bundle.getProperty("json.inputFile");
	private static String outputJson = new SimpleDateFormat(bundle.getProperty("json.outputJson")).format(new Date());
	private static String outputCostMatrix = bundle.getProperty("txt.outputCostMatrix");

	public static int tempsDetour = Integer.parseInt(bundle.getProperty("contrainte.tempsDetour"));
	public static int tempsPriseEncharge = Integer.parseInt(bundle.getProperty("contrainte.tempsPriseEncharge"));
	public static int fixedCost = Integer.parseInt(bundle.getProperty("contrainte.fixedCost"));
	public static int costPerDistance = Integer.parseInt(bundle.getProperty("contrainte.costPerDistance"));
	public static int threadCount = Integer.parseInt(bundle.getProperty("executionParametre.threadCount"));

	// ✅ CORRECT : méthode en dehors du bloc static
	public static JSONArray run(List<TimeSlotOrder> alltimeSlot) throws Exception {
		// Extract OSM resource to temp file
		InputStream in = MultiThreadPool.class.getClassLoader().getResourceAsStream(osmFile);
		File tempOsm = File.createTempFile("graphhopper-osm-", ".pbf");
		tempOsm.deleteOnExit();
		try (FileOutputStream out = new FileOutputStream(tempOsm)) {
			byte[] buffer = new byte[1024];
			int len;
			while ((len = in.read(buffer)) != -1) {
				out.write(buffer, 0, len);
			}
		}

		new GraphHopperImpl(tempOsm.getAbsolutePath(), graphDir, vehicule, weighting);

		JSONArray timeSlotOrderGroupedJson = new JSONArray();
		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<Callable<PoolTwo>> tasks = new ArrayList<>();

		for (final TimeSlotOrder tso : alltimeSlot) {
			tasks.add(() -> {
				PoolTwo pool = new PoolTwo(tso);
				pool.poolProcess();
				return pool;
			});
		}

		List<Future<PoolTwo>> results = executor.invokeAll(tasks);
		for (Future<PoolTwo> future : results) {
			PoolTwo pool = future.get();
			timeSlotOrderGroupedJson.put(pool.getTimeSlotRidesObject());
		}

		executor.shutdown();
		return timeSlotOrderGroupedJson;
	}

	public static void main(String[] args) throws FileNotFoundException, IOException, ParseException {
		JSONArray timeSlotOrderGroupedJson = new JSONArray();
		new GraphHopperImpl(osmFile, graphDir, vehicule, weighting);
		List<TimeSlotOrder> alltimeSlot = new ArrayList<>();
		ReadJsonData.readInputDataOrders(inputFile, alltimeSlot);

		ExecutorService executor = Executors.newFixedThreadPool(threadCount);
		List<Callable<PoolTwo>> listOfCallable = new ArrayList<>();

		for (final TimeSlotOrder tso : alltimeSlot) {
			listOfCallable.add(() -> {
				PoolTwo poolProcess = new PoolTwo(tso);
				poolProcess.poolProcess();
				return poolProcess;
			});
		}

		try {
			List<Future<PoolTwo>> futures = executor.invokeAll(listOfCallable);

			printWriterStatsComplet = new PrintWriter(new FileWriter(fichierResultat));
			printWriterStatsComplet.println("Tranche Horaire;" + "Total Commandes;" + "Nbr Courses;" + "Prix Brut;" + "Prix Groupe;" + "SommePax;" + "temps Execution (S);" + "jobNonAssigne");

			printWriterCostMatrix = new PrintWriter(new FileWriter(outputCostMatrix));
			FileWriter file = new FileWriter(outputJson);

			for (Future<PoolTwo> future : futures) {
				PoolTwo pool = future.get();
				printWriterStatsComplet.println(pool.getPrintWriterStatsComplet());
				printWriterCostMatrix.println(pool.getPrintWriterCostMatrix());
				timeSlotOrderGroupedJson.put(pool.getTimeSlotRidesObject());
			}

			printWriterStatsComplet.close();
			printWriterCostMatrix.close();
			file.write(timeSlotOrderGroupedJson.toString());
			file.close();

		} catch (InterruptedException | ExecutionException e) {
			e.printStackTrace();
		} finally {
			executor.shutdown();
		}
	}
}
