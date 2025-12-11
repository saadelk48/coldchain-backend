package ehei.iot.coldChain.service.impl;

import ehei.iot.coldChain.entity.Measurement;
import ehei.iot.coldChain.service.AlertService;
import ehei.iot.coldChain.service.IncidentService;
import ehei.iot.coldChain.service.MeasurementService;
import ehei.iot.coldChain.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {

    private final IncidentService incidentService;
    private final MeasurementRepository repo;

    @Override
    public Measurement save(Measurement measurement) {

        Measurement saved = repo.save(measurement);

        incidentService.processTemperature(saved.getTemperature());

        return saved;
    }

    @Override
    public List<Measurement> getAll() {
        return repo.findAll();
    }

    @Override
    public Measurement getById(Long id) {
        return repo.findById(id).orElseThrow();
    }
}
