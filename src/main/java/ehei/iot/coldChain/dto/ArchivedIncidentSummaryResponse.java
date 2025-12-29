package ehei.iot.coldChain.dto;

import lombok.Builder;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@Builder
public class ArchivedIncidentSummaryResponse {

    private Long id;
    private LocalDateTime startTime;
    private LocalDateTime endTime;
    private int alertCount;
    private double maxTemperature;
}
