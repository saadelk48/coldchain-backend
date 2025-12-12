package ehei.iot.coldChain.service;

import com.google.firebase.messaging.FirebaseMessagingException;

public interface PushService {

    void sendCriticalAlert(
            String fcmToken,
            String title,
            String body,
            Long incidentId
    ) throws FirebaseMessagingException;
}
