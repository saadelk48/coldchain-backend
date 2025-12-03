package ehei.iot.coldChain.repository;

import ehei.iot.coldChain.entity.Measurement;
import org.springframework.data.jpa.repository.JpaRepository;

public interface MeasurementRepository extends JpaRepository<Measurement, Long> {
}
