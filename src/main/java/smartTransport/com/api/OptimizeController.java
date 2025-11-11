package smartTransport.com.api;

import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import smartTransport.com.service.OptimizationService;

import java.io.File;

@RestController
@RequestMapping("/api")
public class OptimizeController {

    private final OptimizationService optimizationService;

    @Autowired
    public OptimizeController(OptimizationService optimizationService) {
        this.optimizationService = optimizationService;
    }

    @PostMapping(value = "/optimize", consumes = "multipart/form-data", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> optimize(
            @Parameter(description = "Fichier JSON de commandes")
            @RequestPart("file") MultipartFile file) {
        try {
            // stocker le fichier uploadé dans un tempFile
            File tempFile = File.createTempFile("input-", ".json");
            file.transferTo(tempFile);
            System.out.println("DEBUG: uploaded temp file at: " + tempFile.getAbsolutePath());

            // appeler le service d'optimisation
            String resultJson = optimizationService.optimize(tempFile);

            // renvoyer le JSON
            return ResponseEntity.ok()
                    .contentType(MediaType.APPLICATION_JSON)
                    .body(resultJson);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.internalServerError().body("{\"error\":\"" + e.getMessage() + "\"}");
        }
    }
}
