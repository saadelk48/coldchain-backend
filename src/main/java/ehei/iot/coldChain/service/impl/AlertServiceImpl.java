package ehei.iot.coldChain.service.impl;

//import com.google.firebase.messaging.*;
import ehei.iot.coldChain.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class AlertServiceImpl implements AlertService {

    private final JavaMailSender mailSender;

    private final String apiKey = "8064684";

    private final String telegramToken = "8544825366:AAFYS-dSIFcirgJ7WOiNYnzTk-7A0moZ8CQ";
    private final String telegramChatId = "1759851150";
    @Override
    public void sendEmailAlert(double temp) {
        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo("onizukasad@gmail.com");
            msg.setSubject("⚠️ Alerte Température Élevée");
            msg.setText("La température du capteur a atteint " + temp + " °C.");

            mailSender.send(msg);

            System.out.println("📧 Email sent successfully!");
        } catch (Exception e) {
            System.out.println("❌ Failed to send email: " + e.getMessage());
        }
    }

    @Override
    public void sendWhatsappAlert(String phone, String message) {
        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);

            String url = "https://api.callmebot.com/whatsapp.php?phone="
                    + phone
                    + "&text=" + encodedMessage
                    + "&apikey=" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class);

            System.out.println("📲 WhatsApp Alert sent to " + phone);

        } catch (Exception e) {
            System.out.println("❌ Failed to send WhatsApp alert: " + e.getMessage());
        }
    }



    @Override
    public void sendTelegramAlert(String message) {
        try {
            String url = "https://api.telegram.org/bot" + telegramToken + "/sendMessage";

            RestTemplate restTemplate = new RestTemplate();

            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", telegramChatId);
            body.put("text", message);

            restTemplate.postForObject(url, body, String.class);

            System.out.println("📨 Telegram Alert sent");

        } catch (Exception e) {
            System.out.println("❌ Failed to send Telegram alert: " + e.getMessage());
        }
    }


//    @Override
//    public void sendPushAlert(
//            String fcmToken,
//            String title,
//            String body,
//            Long incidentId
//    ) throws FirebaseMessagingException {
//
//        if (fcmToken == null || fcmToken.isBlank()) {
//            System.out.println("⚠️ No FCM token, skipping push alert");
//            return;
//        }
//
//        Message message = Message.builder()
//                .setToken(fcmToken)
//
//                // ✅ DATA ONLY — VERY IMPORTANT
//                .putData("type", "CRITICAL")
//                .putData("title", title)
//                .putData("body", body)
//                .putData("incidentId", String.valueOf(incidentId))
//
//                .setAndroidConfig(
//                        AndroidConfig.builder()
//                                .setPriority(AndroidConfig.Priority.HIGH)
//                                .build()
//                )
//                .build();
//
//        FirebaseMessaging.getInstance().send(message);
//
//        System.out.println("📲 Push ALARM sent to device");
//    }




}
