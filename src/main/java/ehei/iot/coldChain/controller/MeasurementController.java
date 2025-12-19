package ehei.iot.coldChain.controller;

import ehei.iot.coldChain.config.props.IngestProperties;
import ehei.iot.coldChain.entity.Measurement;
import ehei.iot.coldChain.service.MeasurementService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/measurements")
@RequiredArgsConstructor
public class MeasurementController {

    private final MeasurementService measurementService;
    private final IngestProperties ingestProperties;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestHeader("X-API-KEY") String apiKey,
            @RequestBody Measurement measurement) {

        if (!apiKey.equals(ingestProperties.getApiKey())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid API KEY");
        }

        Measurement saved = measurementService.save(measurement);
        return ResponseEntity.ok(saved);
    }



    @GetMapping
    public ResponseEntity<List<Measurement>> getAll() {
        return ResponseEntity.ok(measurementService.getAll());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Measurement> getById(@PathVariable Long id) {
        return ResponseEntity.ok(measurementService.getById(id));
    }
}
