package ehei.iot.coldChain.service;


import ehei.iot.coldChain.entity.Measurement;

import java.util.List;

public interface MeasurementService {

    Measurement save(Measurement measurement);

    List<Measurement> getAll();

    Measurement getById(Long id);
}