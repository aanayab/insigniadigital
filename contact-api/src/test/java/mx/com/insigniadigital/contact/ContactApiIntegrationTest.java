package mx.com.insigniadigital.contact;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.reset;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.mail.MailSendException;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

@SpringBootTest
@AutoConfigureMockMvc
class ContactApiIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private JavaMailSender mailSender;

    @AfterEach
    void resetMailSender() {
        reset(mailSender);
    }

    @Test
    void sendsAValidContactWithSafeEnvelope() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .header("X-Contact-Client-IP", "198.51.100.10")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sent"));

        ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
        verify(mailSender).send(captor.capture());
        SimpleMailMessage message = captor.getValue();
        org.assertj.core.api.Assertions.assertThat(message.getTo())
                .containsExactly("atencion@insigniadigital.com.mx");
        org.assertj.core.api.Assertions.assertThat(message.getReplyTo()).isEqualTo("ana@example.com");
        org.assertj.core.api.Assertions.assertThat(message.getSubject()).doesNotContain("\r", "\n");
        org.assertj.core.api.Assertions.assertThat(message.getText()).contains("Necesito información");
    }

    @Test
    void rejectsInvalidInputWithoutSendingMail() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .header("X-Contact-Client-IP", "198.51.100.11")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content("{\"firstName\":\"\",\"lastName\":\"Pérez\",\"email\":\"mal\",\"message\":\"\"}"))
                .andExpect(status().isBadRequest())
                .andExpect(jsonPath("$.status").value("invalid"))
                .andExpect(jsonPath("$.errors.firstName").value("invalid"))
                .andExpect(jsonPath("$.errors.email").value("invalid"))
                .andExpect(jsonPath("$.errors.message").value("invalid"));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void silentlyAcceptsTheHoneypotWithoutSendingMail() throws Exception {
        mockMvc.perform(post("/api/contact")
                        .header("X-Contact-Client-IP", "198.51.100.12")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("https://spam.invalid")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.status").value("sent"));

        verify(mailSender, never()).send(any(SimpleMailMessage.class));
    }

    @Test
    void limitsTheSixthAttemptFromTheSameAddress() throws Exception {
        for (int attempt = 0; attempt < 5; attempt++) {
            mockMvc.perform(post("/api/contact")
                            .header("X-Contact-Client-IP", "198.51.100.13")
                            .contentType(MediaType.APPLICATION_JSON)
                            .content(validBody("bot")))
                    .andExpect(status().isOk());
        }

        mockMvc.perform(post("/api/contact")
                        .header("X-Contact-Client-IP", "198.51.100.13")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("bot")))
                .andExpect(status().isTooManyRequests())
                .andExpect(jsonPath("$.status").value("rate_limited"));
    }

    @Test
    void reportsSmtpFailuresWithoutExposingDetails() throws Exception {
        doThrow(new MailSendException("SMTP detail that must not escape"))
                .when(mailSender).send(any(SimpleMailMessage.class));

        mockMvc.perform(post("/api/contact")
                        .header("X-Contact-Client-IP", "198.51.100.14")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(validBody("")))
                .andExpect(status().isBadGateway())
                .andExpect(jsonPath("$.status").value("unavailable"))
                .andExpect(jsonPath("$.errors").isEmpty());
    }

    private String validBody(String website) {
        return """
                {
                  "firstName": "Ana",
                  "lastName": "Pérez",
                  "email": "ana@example.com",
                  "message": "Necesito información sobre RCS",
                  "website": "%s"
                }
                """.formatted(website);
    }
}
