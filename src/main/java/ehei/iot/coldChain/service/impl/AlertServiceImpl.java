package ehei.iot.coldChain.service.impl;

import ehei.iot.coldChain.config.props.AlertProperties;
import ehei.iot.coldChain.service.AlertService;
import jakarta.annotation.PostConstruct;
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

    /* -------------------------------------------------------
       DEBUG: verify env injection at startup
       ------------------------------------------------------- */
    @PostConstruct
    public void debugConfig() {
        log.info("Alert config loaded:");
        log.info("  Email to      = {}", alertProperties.getEmail().getTo());
        log.info("  WhatsApp phone= {}", alertProperties.getWhatsapp().getPhone());
        log.info("  Telegram chat = {}", alertProperties.getTelegram().getChatId());
    }

    /* -------------------------------------------------------
       EMAIL
       ------------------------------------------------------- */
    @Override
    public void sendEmailAlert(double temp) {
        String emailTo = alertProperties.getEmail().getTo();
        if (emailTo == null || emailTo.isBlank()) {
            log.warn("Email alert skipped (no recipient configured)");
            return;
        }

        try {
            SimpleMailMessage msg = new SimpleMailMessage();
            msg.setTo(emailTo);
            msg.setSubject("⚠️ ColdChain – High temperature alert");
            msg.setText("Sensor temperature reached " + temp + " °C.");

            mailSender.send(msg);
            log.info("📧 Email alert sent to {}", emailTo);
        } catch (Exception e) {
            log.error("❌ Failed to send email alert", e);
        }
    }

    /* -------------------------------------------------------
       TELEGRAM
       ------------------------------------------------------- */
    @Override
    public void sendTelegramAlert(String message) {
        String token = alertProperties.getTelegram().getToken();
        String chatId = alertProperties.getTelegram().getChatId();

        if (token == null || token.isBlank()
                || chatId == null || chatId.isBlank()) {
            log.warn("Telegram alert skipped (missing token or chatId)");
            return;
        }

        try {
            String url = "https://api.telegram.org/bot" + token + "/sendMessage";

            Map<String, Object> body = new HashMap<>();
            body.put("chat_id", chatId);
            body.put("text", message);

            new RestTemplate().postForObject(url, body, String.class);
            log.info("📨 Telegram alert sent");
        } catch (Exception e) {
            log.error("❌ Failed to send Telegram alert", e);
        }
    }

    /* -------------------------------------------------------
       WHATSAPP (CallMeBot – fixed phone)
       ------------------------------------------------------- */
    @Override
    public void sendWhatsappAlert(String message) {
        String apiKey = alertProperties.getWhatsapp().getApikey();
        String phone = alertProperties.getWhatsapp().getPhone();

        if (apiKey == null || apiKey.isBlank()
                || phone == null || phone.isBlank()) {
            log.warn("WhatsApp alert skipped (missing phone or apiKey)");
            return;
        }

        try {
            String encodedMessage = URLEncoder.encode(message, StandardCharsets.UTF_8);
            String url = "https://api.callmebot.com/whatsapp.php?phone="
                    + phone
                    + "&text=" + encodedMessage
                    + "&apikey=" + apiKey;

            new RestTemplate().getForObject(url, String.class);
            log.info("📲 WhatsApp alert sent to {}", phone);
        } catch (Exception e) {
            log.error("❌ Failed to send WhatsApp alert", e);
        }
    }
}
