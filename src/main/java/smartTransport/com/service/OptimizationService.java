package smartTransport.com.service;

import org.json.JSONArray;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import smartTransport.com.algo.MultiThreadPool;
import smartTransport.com.data.TimeSlotOrder;
import smartTransport.com.readInputData.ReadJsonData;

import java.io.File;
import java.io.FileWriter;
import java.util.ArrayList;
import java.util.List;

/**
 * Service d'optimisation : lit le fichier d'input, exécute l'optimisation (MultiThreadPool.run)
 * puis écrit le JSON de sortie dans le fichier configuré par app.output-json-path.
 */
@Service
public class OptimizationService {

    /**
     * Valeur récupérée depuis application.properties.
     * Exemple recommandé dans application.properties :
     * app.output-json-path=${java.io.tmpdir}/output.json
     *
     * Nous récupérons la valeur via @Value mais nous faisons aussi une correction
     * côté code si Spring n'a pas résolu ${java.io.tmpdir}.
     */
    @Value("${app.output-json-path:}")
    private String outputJsonPath;

    /**
     * Exécute l'optimisation à partir d'un fichier input (upload temporaire).
     * @param inputFile fichier JSON uploadé contenant les commandes
     * @return JSON (String) représentant le résultat de l'optimisation (indenté)
     * @throws Exception en cas d'erreur
     */
    public String optimize(File inputFile) throws Exception {
        // --- Résolution robuste du chemin de sortie ---
        if (outputJsonPath == null || outputJsonPath.isEmpty()) {
            outputJsonPath = System.getProperty("java.io.tmpdir") + File.separator + "output.json";
        } else if (outputJsonPath.contains("${java.io.tmpdir}")) {
            outputJsonPath = outputJsonPath.replace("${java.io.tmpdir}", System.getProperty("java.io.tmpdir"));
        }
        // normalize possible forward/back slashes on Windows
        outputJsonPath = outputJsonPath.replace("/", File.separator).replace("\\", File.separator);

        System.out.println("DEBUG: output path resolved to " + outputJsonPath);

        // --- Lire le fichier d'entrée vers la structure attendue ---
        List<TimeSlotOrder> allTimeSlots = new ArrayList<>();
        // ReadJsonData.readInputDataOrders attend (String inputPath, List<TimeSlotOrder> target)
        ReadJsonData.readInputDataOrders(inputFile.getAbsolutePath(), allTimeSlots);
        System.out.println("DEBUG: read " + allTimeSlots.size() + " timeslots from " + inputFile.getAbsolutePath());

        // --- Exécution de l'algorithme (synchronisé par sécurité si GraphHopper non thread-safe) ---
        JSONArray resultArray;
        synchronized (OptimizationService.class) {
            resultArray = MultiThreadPool.run(allTimeSlots);
        }

        System.out.println("DEBUG: resultArray length = " + (resultArray == null ? 0 : resultArray.length()));

        // --- Préparer dossier de sortie et écrire le fichier ---
        File outputFile = new File(outputJsonPath);
        File parentDir = outputFile.getParentFile();
        if (parentDir != null && !parentDir.exists()) {
            boolean created = parentDir.mkdirs();
            System.out.println("DEBUG: created output folder: " + created + " -> " + parentDir.getAbsolutePath());
        }

        try (FileWriter fw = new FileWriter(outputFile)) {
            fw.write(resultArray == null ? "[]" : resultArray.toString(2));
            fw.flush();
        } catch (Exception e) {
            System.err.println("Erreur écriture output JSON: " + outputJsonPath + " -> " + e.getMessage());
            throw e;
        }

        System.out.println("DEBUG: output JSON written to " + outputFile.getAbsolutePath());

        // --- Retourner le JSON sous forme de chaîne formatée ---
        return resultArray == null ? "[]" : resultArray.toString(2);
    }
}
