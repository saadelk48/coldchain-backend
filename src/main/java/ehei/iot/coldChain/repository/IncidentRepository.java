package ehei.iot.coldChain.repository;


import ehei.iot.coldChain.entity.Incident;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface IncidentRepository extends JpaRepository<Incident, Long> {

    Optional<Incident> findByActiveTrue();
}
