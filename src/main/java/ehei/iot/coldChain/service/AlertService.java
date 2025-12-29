package ehei.iot.coldChain.service;

//import com.google.firebase.messaging.FirebaseMessagingException;

import java.time.LocalDateTime;

public interface AlertService {
    void sendEmailAlert(double temp);
    void sendTelegramAlert(String message);
    void sendWhatsappAlert(String message);
//    void sendPushAlert(String fcmToken, String title, String body, Long incidentId)
//            throws FirebaseMessagingException;
}
