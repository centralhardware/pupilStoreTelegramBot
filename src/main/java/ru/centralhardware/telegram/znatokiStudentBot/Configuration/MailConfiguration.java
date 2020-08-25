package ru.centralhardware.telegram.znatokiStudentBot.Configuration;

import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.api.mailer.config.TransportStrategy;
import org.simplejavamail.mailer.MailerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import ru.centralhardware.telegram.znatokiStudentBot.Config;

@Configuration
public class MailConfiguration {

    @Bean
    public Mailer mailer() {
        return MailerBuilder.withSMTPServer(
                Config.getSmtpHost(),
                Config.getSmtpPort(),
                Config.getSmtpUser(),
                Config.getSmtpPassword()).
                withTransportStrategy(TransportStrategy.SMTP_TLS).
                withDebugLogging(false).
                buildMailer();
    }

}
