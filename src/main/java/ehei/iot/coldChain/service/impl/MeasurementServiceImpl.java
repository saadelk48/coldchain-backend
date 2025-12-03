package ehei.iot.coldChain.service.impl;



import ehei.iot.coldChain.entity.Measurement;
import ehei.iot.coldChain.service.AlertService;
import ehei.iot.coldChain.service.MeasurementService;
import ehei.iot.coldChain.repository.MeasurementRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class MeasurementServiceImpl implements MeasurementService {

    private final MeasurementRepository repo;
    private final AlertService alertService;

    @Override
    public Measurement save(Measurement measurement) {
        Measurement saved = repo.save(measurement);

        if (saved.getTemperature() > 25) {
//            alertService.sendEmailAlert(saved.getTemperature());
//            alertService.sendWhatsappAlert(saved.getTemperature());
            alertService.sendTelegramAlert(saved.getTemperature());
        }
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