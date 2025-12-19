package ehei.iot.coldChain.service.impl;

import ehei.iot.coldChain.config.props.AlertProperties;
import ehei.iot.coldChain.service.AlertService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

    private static final Logger log = LoggerFactory.getLogger(AlertServiceImpl.class);

    private final JavaMailSender mailSender;
    private final AlertProperties alertProperties;

    @Override
    public void sendEmailAlert(double temp) {
        String emailTo = alertProperties.getEmail().getTo();
        if (emailTo == null || emailTo.isBlank()) {
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(emailTo);
            msg.setSubject("ColdChain - High temperature alert");
            msg.setText("Sensor temperature reached " + temp + " °C.");
            mailSender.send(msg);
            log.info("Email alert sent to {}", emailTo);
        } catch (Exception e) {
            log.warn("Failed to send email alert: {}", e.getMessage());
        }
    }

    @Override
    public void sendTelegramAlert(String message) {
        String token = alertProperties.getTelegram().getToken();
        String chatId = alertProperties.getTelegram().getChatId();
        if (token == null || token.isBlank() || chatId == null || chatId.isBlank()) {
            return;
        }

        try {
            String url = "https://api.telegram.org/bot" + token + "/sendMessage";

            RestTemplate restTemplate = new RestTemplate();
            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", message);

            restTemplate.postForObject(url, body, String.class);
            log.info("Telegram alert sent");
        } catch (Exception e) {
            log.warn("Failed to send Telegram alert: {}", e.getMessage());
        }
    }

    @Override
    public void sendWhatsappAlert(String phone, String message) {
        String apiKey = alertProperties.getWhatsapp().getApikey();
        if (apiKey == null || apiKey.isBlank()) {
            return;
        }

        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String url = "https://api.callmebot.com/whatsapp.php?phone="
                    + phone
                    + "&text=" + encodedMessage
                    + "&apikey=" + apiKey;

            RestTemplate restTemplate = new RestTemplate();
            restTemplate.getForObject(url, String.class);
            log.info("WhatsApp alert sent to {}", phone);
        } catch (Exception e) {
            log.warn("Failed to send WhatsApp alert: {}", e.getMessage());
        }
    }
}
