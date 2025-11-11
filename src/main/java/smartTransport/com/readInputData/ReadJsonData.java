package smartTransport.com.readInputData;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.List;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import smartTransport.com.data.Order;
import smartTransport.com.data.TimeSlotOrder;

public class ReadJsonData {

	public static void readInputDataOrders(String fichierDonnees, List<TimeSlotOrder> alltimeSlot)
			throws FileNotFoundException, IOException, ParseException {

		JSONParser parser = new JSONParser();
		Order commandeOrder;
		TimeSlotOrder timeSlotOrder;
		Object obj = parser.parse(new FileReader(fichierDonnees));
		//System.out.println(obj);
		
		JSONArray commandeTab, commandes, tranches = (JSONArray) obj;
		JSONObject commande, InCommande, OutCommande, trache;
		
		//System.out.println(tranches);
		
		// JSONArray tab = elements;
		for (int i = 0; i < tranches.size(); i++) {
			trache = (JSONObject) tranches.get(i);
			
			timeSlotOrder = new TimeSlotOrder((String) trache.get("maxResponseWantedAt"),
					(String) trache.get("maxResponseWantedAt"));

			commandes = (JSONArray) trache.get("orders"); // trache.get("orders")
			for (int h = 0; h < commandes.size(); h++) {
				commande = (JSONObject) commandes.get(h);
				//commandeTab = (JSONArray) commande.get("course");
				InCommande = (JSONObject) commande.get("pickUp");
				OutCommande = (JSONObject) commande.get("dropOff");
				

				commandeOrder = new Order((Double) InCommande.get("lat"), (Double) InCommande.get("lng"),
						(Double) OutCommande.get("lat"), (Double) OutCommande.get("lng"),
						Long.parseLong(commande.get("id").toString()),
						Integer.parseInt(commande.get("pax").toString()));
				timeSlotOrder.addOrder(commandeOrder);
			}
			alltimeSlot.add(timeSlotOrder);
		}
	}
}
