package ehei.iot.coldChain.repository;


import ehei.iot.coldChain.entity.*;
import org.springframework.data.jpa.repository.JpaRepository;

public interface IncidentOperatorAckRepository extends JpaRepository<IncidentOperatorAck, Long> {
    IncidentOperatorAck findByIncidentIdAndOperatorId(Long incidentId, Long operatorId);
}

