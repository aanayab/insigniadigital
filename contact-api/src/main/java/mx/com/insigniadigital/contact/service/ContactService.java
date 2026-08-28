package mx.com.insigniadigital.contact.service;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import mx.com.insigniadigital.contact.config.ContactProperties;
import mx.com.insigniadigital.contact.model.ContactRequest;

@Service
public class ContactService {

    private final JavaMailSender mailSender;
    private final ContactProperties properties;

    public ContactService(JavaMailSender mailSender, ContactProperties properties) {
        this.mailSender = mailSender;
        this.properties = properties;
    }

    public void send(ContactRequest request) {
        String firstName = normalize(request.firstName());
        String lastName = normalize(request.lastName());
        String email = normalize(request.email());
        String message = normalizeMessage(request.message());

        SimpleMailMessage mail = new SimpleMailMessage();
        mail.setFrom(properties.sender());
        mail.setTo(properties.recipient());
        mail.setReplyTo(email);
        mail.setSubject("Nuevo contacto desde insigniadigital.com.mx — " + firstName + " " + lastName);
        mail.setText("Nombre: " + firstName + " " + lastName + "\n"
                + "Correo: " + email + "\n\n"
                + "Mensaje:\n" + message + "\n");
        mailSender.send(mail);
    }

    private String normalize(String value) {
        return value == null ? "" : value.trim().replaceAll("[\\r\\n]+", " ");
    }

    private String normalizeMessage(String value) {
        return value == null ? "" : value.trim().replace("\r\n", "\n").replace('\r', '\n');
    }
}
