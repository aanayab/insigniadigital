package mx.com.insigniadigital.contact.service;

import java.time.Clock;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicInteger;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import mx.com.insigniadigital.contact.config.ContactProperties;

@Component
public class ContactRateLimiter {

    private final ContactProperties properties;
    private final Clock clock;
    private final Map<String, Window> windows = new ConcurrentHashMap<>();

    @Autowired
    public ContactRateLimiter(ContactProperties properties) {
        this(properties, Clock.systemUTC());
    }

    ContactRateLimiter(ContactProperties properties, Clock clock) {
        this.properties = properties;
        this.clock = clock;
    }

    public boolean allow(String clientKey) {
        Instant now = clock.instant();
        Window window = windows.compute(clientKey, (key, current) -> {
            if (current == null || current.expiresAt().isBefore(now) || current.expiresAt().equals(now)) {
                return new Window(now.plus(properties.rateLimitWindow()), new AtomicInteger(1));
            }
            current.attempts().incrementAndGet();
            return current;
        });
        return window.attempts().get() <= properties.rateLimitAttempts();
    }

    @Scheduled(fixedDelayString = "PT10M")
    void removeExpiredWindows() {
        Instant now = clock.instant();
        windows.entrySet().removeIf(entry -> !entry.getValue().expiresAt().isAfter(now));
    }

    private record Window(Instant expiresAt, AtomicInteger attempts) {
    }
}
