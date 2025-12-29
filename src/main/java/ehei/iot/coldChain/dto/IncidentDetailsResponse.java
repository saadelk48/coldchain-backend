package ehei.iot.coldChain.dto;

import lombok.Data;
import java.time.LocalDateTime;
import java.util.List;

@Data
public class IncidentDetailsResponse {

    private Long id;
    private boolean active;
    private double maxTemperature;
    private LocalDateTime startTime;
    private LocalDateTime endTime;

    private List<CommentDTO> comments;
    private List<AckDTO> acknowledgments;

    @Data
    public static class CommentDTO {
        private OperatorDto operator;
        private String message;
        private LocalDateTime createdAt;
    }

    @Data
    public static class AckDTO {
        private OperatorDto operator;
        private boolean acknowledged;
        private LocalDateTime ackTime;
    }
}
