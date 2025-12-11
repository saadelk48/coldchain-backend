package ehei.iot.coldChain.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "incident_comment")
@Builder
@Data
@NoArgsConstructor
@AllArgsConstructor
public class IncidentComment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "message", nullable = false)
    private String message;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt;

    @ManyToOne
    @JsonIgnore
    @JoinColumn(name = "incident_id")
    private Incident incident;

    @ManyToOne
    @JoinColumn(name = "operator_id")
    private AppUser operator;
}


