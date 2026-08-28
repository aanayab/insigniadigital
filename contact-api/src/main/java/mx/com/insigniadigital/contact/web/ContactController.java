package mx.com.insigniadigital.contact.web;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;

import org.springframework.http.ResponseEntity;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import mx.com.insigniadigital.contact.model.ContactRequest;
import mx.com.insigniadigital.contact.model.ContactResponse;
import mx.com.insigniadigital.contact.service.ContactRateLimiter;
import mx.com.insigniadigital.contact.service.ContactService;

@RestController
@RequestMapping("/api/contact")
public class ContactController {

    private final ContactService contactService;
    private final ContactRateLimiter rateLimiter;

    public ContactController(ContactService contactService, ContactRateLimiter rateLimiter) {
        this.contactService = contactService;
        this.rateLimiter = rateLimiter;
    }

    @PostMapping(consumes = MediaType.APPLICATION_JSON_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<ContactResponse> send(
            @RequestHeader(value = "X-Contact-Client-IP", required = false) String forwardedClientIp,
            HttpServletRequest servletRequest,
            @Valid @RequestBody ContactRequest request) {
        String clientKey = clientKey(forwardedClientIp, servletRequest.getRemoteAddr());
        if (!rateLimiter.allow(clientKey)) {
            return ResponseEntity.status(429).body(ContactResponse.status("rate_limited"));
        }
        if (request.website() != null && !request.website().isBlank()) {
            return ResponseEntity.ok(ContactResponse.status("sent"));
        }
        contactService.send(request);
        return ResponseEntity.ok(ContactResponse.status("sent"));
    }

    private String clientKey(String forwardedClientIp, String remoteAddress) {
        if (forwardedClientIp == null || forwardedClientIp.isBlank()) {
            return remoteAddress;
        }
        return forwardedClientIp.trim().substring(0, Math.min(forwardedClientIp.trim().length(), 64));
    }
}
