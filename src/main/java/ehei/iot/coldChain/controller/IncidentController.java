package ehei.iot.coldChain.controller;

import ehei.iot.coldChain.dto.CommentRequest;
import ehei.iot.coldChain.dto.IncidentDetailsResponse;
import ehei.iot.coldChain.entity.Incident;
import ehei.iot.coldChain.service.IncidentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/incidents")
@RequiredArgsConstructor
public class IncidentController {

    private final IncidentService incidentService;

    @GetMapping("/active")
    public ResponseEntity<?> getActiveIncident() {
        Incident incident = incidentService.getActiveIncident();
        return ResponseEntity.ok(incident); // can be null -> frontend must handle it
    }

    // GET DETAILS
    @GetMapping("/{incidentId}")
    public ResponseEntity<?> getIncidentDetails(@PathVariable Long incidentId) {
        IncidentDetailsResponse response = incidentService.getIncidentDetails(incidentId);
        if (response == null) return ResponseEntity.notFound().build();
        return ResponseEntity.ok(response);
    }

    // ACK
    @PostMapping("/{operatorId}/ack")
    public ResponseEntity<?> acknowledge(@PathVariable Long operatorId) {
        incidentService.acknowledgeIncident(operatorId);
        return ResponseEntity.ok("ACK saved");
    }

    // COMMENT
    @PostMapping("/{operatorId}/comment")
    public ResponseEntity<?> addComment(
            @PathVariable Long operatorId,
            @RequestBody CommentRequest request
    ) {
        incidentService.addComment(operatorId, request.getMessage());
        return ResponseEntity.ok("Comment saved");
    }
}
