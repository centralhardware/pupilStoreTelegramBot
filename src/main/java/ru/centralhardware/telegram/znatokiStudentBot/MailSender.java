package ru.centralhardware.telegram.znatokiStudentBot;

import org.simplejavamail.api.email.Email;
import org.simplejavamail.api.mailer.Mailer;
import org.simplejavamail.email.EmailBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PupilService;

@Service
public class MailSender {

    private final PupilService pupilService;
    private final Mailer mailer;

    public MailSender(PupilService pupilService, Mailer mailer) {
        this.pupilService = pupilService;
        this.mailer = mailer;
    }

    public void sendAll(String text, String subject) {
        if (!Config.getEmailEnabled()) return;
        new Thread(() -> pupilService.getEmail().forEach((email, fio) -> {
            Logger log = LoggerFactory.getLogger(MailSender.class);
            Email email1 = EmailBuilder.
                    startingBlank().
                    from(Config.getEmailFrom(), Config.getSmtpUser()).
                    to(fio, email).
                    withSubject(subject).
                    withPlainText(text).buildEmail();
            mailer.sendMail(email1);
            log.info("send message to {}", email);
            try {
                Thread.sleep(1000L);
            } catch (InterruptedException ignored) {
            }
        })).start();
    }

    public void send(String text, String subject, String email) {
        if (!Config.getEmailEnabled()) return;
        Email email1 = EmailBuilder.
                startingBlank().
                from(Config.getEmailFrom(), Config.getSmtpUser()).
                to(email).withSubject(subject).
                withPlainText(text).buildEmail();
        mailer.sendMail(email1);
    }

}
