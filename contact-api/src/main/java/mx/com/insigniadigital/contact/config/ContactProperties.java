package mx.com.insigniadigital.contact.config;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties("contact")
public record ContactProperties(
        String recipient,
        String sender,
        int rateLimitAttempts,
        Duration rateLimitWindow) {
}
