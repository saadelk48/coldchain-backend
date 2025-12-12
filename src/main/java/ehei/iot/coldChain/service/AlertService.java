package ehei.iot.coldChain.service;

import java.time.LocalDateTime;

public interface AlertService {
    void sendEmailAlert(double temp);
    void sendTelegramAlert(String message);
    void sendWhatsappAlert(String phone, String message);
}
