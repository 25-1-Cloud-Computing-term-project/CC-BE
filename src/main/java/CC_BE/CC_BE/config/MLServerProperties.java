package CC_BE.CC_BE.config;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "ml.server")
public class MLServerProperties {
    private String url;
    private String apiKey;
} 