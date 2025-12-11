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

    @ManyToOne
    @JsonIgnore
    private Incident incident;

    @ManyToOne
    private AppUser operator;

    private boolean acknowledged;

    private LocalDateTime ackTime;
}
