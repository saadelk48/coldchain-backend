package ehei.iot.coldChain.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "coldchain.alert")
@Data
public class AlertProperties {

    private Email email = new Email();
    private Whatsapp whatsapp = new Whatsapp();
    private Telegram telegram = new Telegram();

    @Data
    public static class Email {
        private String to;
    }

    @Data
    public static class Whatsapp {
        private String apikey;
        private String phone;
    }

    @Data
    public static class Telegram {
        private String token;
        private String chatId;
    }
}
