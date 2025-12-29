package ehei.iot.coldChain.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "coldchain.security.jwt")
public class JwtProperties {
    private String secret;
    private String issuer;
    private long ttlSeconds;
}
