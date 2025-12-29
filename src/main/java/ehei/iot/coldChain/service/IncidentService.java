package ehei.iot.coldChain.service;

import ehei.iot.coldChain.dto.ArchivedIncidentSummaryResponse;
import ehei.iot.coldChain.dto.IncidentDetailsResponse;
import ehei.iot.coldChain.entity.AppUser;
import ehei.iot.coldChain.entity.Incident;

import java.util.List;

public interface IncidentService {

    void processTemperature(double temp); // MAIN LOGIC

    void acknowledgeIncident(Long operatorId);

    public IncidentDetailsResponse getIncidentDetails(Long id);

    void addComment(Long operatorId, String comment);

    Incident getActiveIncident();

    List<ArchivedIncidentSummaryResponse> getArchivedIncidents();

    IncidentDetailsResponse getArchivedIncidentDetails(Long incidentId);
}
