package com.backend.apiserver.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

@Data
@Component
@PropertySource("classpath:commonProperties.properties")
@ConfigurationProperties(prefix = "information")
public class CommonProperties {
    private String secret;
    private Long expiration;
    private String apikey;
    private String domain;
    private String backendURL;
    private String frontendURL;
}
