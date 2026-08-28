package mx.com.insigniadigital.contact.web;

import java.util.LinkedHashMap;
import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.mail.MailException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import mx.com.insigniadigital.contact.model.ContactResponse;

@RestControllerAdvice
public class ApiExceptionHandler {

    @ExceptionHandler(MethodArgumentNotValidException.class)
    ResponseEntity<ContactResponse> invalid(MethodArgumentNotValidException exception) {
        Map<String, String> errors = new LinkedHashMap<>();
        exception.getBindingResult().getFieldErrors()
                .forEach(error -> errors.putIfAbsent(error.getField(), "invalid"));
        return ResponseEntity.badRequest().body(new ContactResponse("invalid", errors));
    }

    @ExceptionHandler(MailException.class)
    ResponseEntity<ContactResponse> mailUnavailable() {
        return ResponseEntity.status(502).body(ContactResponse.status("unavailable"));
    }
}
