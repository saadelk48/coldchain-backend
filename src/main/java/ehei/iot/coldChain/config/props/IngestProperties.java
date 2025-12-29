package ehei.iot.coldChain.config.props;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

@Data
@ConfigurationProperties(prefix = "coldchain.ingest")
public class IngestProperties {
    private String apiKey;
}
