package ehei.iot.coldChain.repository;

import ehei.iot.coldChain.entity.Sensor;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AuditLogRepository extends JpaRepository<Sensor, Long> {
}
