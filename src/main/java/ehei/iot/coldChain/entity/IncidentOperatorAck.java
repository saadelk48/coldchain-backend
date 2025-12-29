package ehei.iot.coldChain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class IncidentOperatorAck {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "incident_id", nullable = false)
    @JsonIgnore
    private Incident incident;

    @ManyToOne(optional = false, fetch = FetchType.LAZY)
    @JoinColumn(name = "operator_id", nullable = false)
    private AppUser operator;

    private boolean acknowledged;

    private LocalDateTime ackTime;
}

