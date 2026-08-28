package mx.com.insigniadigital.contact.model;

import java.util.Map;

public record ContactResponse(String status, Map<String, String> errors) {

    public static ContactResponse status(String status) {
        return new ContactResponse(status, Map.of());
    }
}
