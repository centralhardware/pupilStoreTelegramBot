package ru.centralhardware.telegram.znatokiStudentBot.Service;

import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Email;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.EmailRepository;

import java.util.Optional;

@Service
public class EmailService {

    private final EmailRepository emailRepository;

    public EmailService(EmailRepository emailRepository) {
        this.emailRepository = emailRepository;
    }

    public Email save(Email email) {
        return emailRepository.save(email);
    }

    public boolean checkByUuid(String uuid) {
        return emailRepository.existsById(uuid);
    }

    public Optional<Email> findByUuid(String uuid) {
        return emailRepository.findById(uuid);
    }

}
