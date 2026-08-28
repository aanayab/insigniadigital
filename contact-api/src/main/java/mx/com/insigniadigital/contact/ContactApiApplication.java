package mx.com.insigniadigital.contact;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.ConfigurationPropertiesScan;
import org.springframework.scheduling.annotation.EnableScheduling;

@EnableScheduling
@SpringBootApplication
@ConfigurationPropertiesScan
public class ContactApiApplication {

    public static void main(String[] args) {
        SpringApplication.run(ContactApiApplication.class, args);
    }
}
