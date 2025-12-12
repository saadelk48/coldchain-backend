package ehei.iot.coldChain.service.impl;

import com.google.firebase.messaging.AndroidConfig;
import com.google.firebase.messaging.FirebaseMessaging;
import com.google.firebase.messaging.FirebaseMessagingException;
import com.google.firebase.messaging.Message;
import com.google.firebase.messaging.Notification;
import ehei.iot.coldChain.service.PushService;
import org.springframework.stereotype.Service;

@Service
public class PushServiceImpl implements PushService {

    @Override
    public void sendCriticalAlert(
            String fcmToken,
            String title,
            String body,
            Long incidentId
    ) throws FirebaseMessagingException {

        Message message = Message.builder()
                .setToken(fcmToken)
                .setNotification(
                        Notification.builder()
                                .setTitle(title)
                                .setBody(body)
                                .build()
                )
                .putData("type", "CRITICAL")
                .putData("incidentId", String.valueOf(incidentId))
                .setAndroidConfig(
                        AndroidConfig.builder()
                                .setPriority(AndroidConfig.Priority.HIGH)
                                .build()
                )
                .build();

        FirebaseMessaging.getInstance().send(message);
    }
}
