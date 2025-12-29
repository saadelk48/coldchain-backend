package ehei.iot.coldChain.controller;

import ehei.iot.coldChain.dto.ArchivedIncidentSummaryResponse;
import ehei.iot.coldChain.dto.IncidentDetailsResponse;
import ehei.iot.coldChain.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/incidents/archive")
@RequiredArgsConstructor
public class IncidentArchiveController {

    private final IncidentService incidentService;

    // ✅ LIST ALL CLOSED INCIDENTS (SUMMARY)
    @GetMapping
    public ResponseEntity<List<ArchivedIncidentSummaryResponse>> getArchivedIncidents() {
        return ResponseEntity.ok(incidentService.getArchivedIncidents());
    }

    // ✅ DETAILS OF A CLOSED INCIDENT
    @GetMapping("/{incidentId}")
    public ResponseEntity<IncidentDetailsResponse> getArchivedIncidentDetails(
            @PathVariable Long incidentId
    ) {
        IncidentDetailsResponse response =
                incidentService.getArchivedIncidentDetails(incidentId);

        if (response == null) {
            return ResponseEntity.notFound().build();
        }

        return ResponseEntity.ok(response);
    }
}
