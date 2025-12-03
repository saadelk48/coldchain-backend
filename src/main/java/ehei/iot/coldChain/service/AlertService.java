package ehei.iot.coldChain.service;

import java.time.LocalDateTime;

public interface AlertService {
    void sendEmailAlert(double temp);
    void sendTelegramAlert(double temp);
    void sendWhatsappAlert(double temp);
}
