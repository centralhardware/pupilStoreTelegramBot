package ru.centralhardware.telegram.znatokiStudentBot.Service;

import org.springframework.stereotype.Service;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Pupil;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Session;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.TelegramUser;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.SessionRepository;

import java.util.Optional;

@Service
public class SessionService {

    private final SessionRepository sessionRepository;

    public SessionService(SessionRepository sessionRepository) {
        this.sessionRepository = sessionRepository;
    }

    public String create(Pupil pupil, TelegramUser telegramUser) {
        Session session = new Session(pupil, telegramUser);
        return sessionRepository.save(session).getUuid();
    }

    public Optional<Session> findByUuid(String uuid) {
        return sessionRepository.findByUuid(uuid);
    }

}
