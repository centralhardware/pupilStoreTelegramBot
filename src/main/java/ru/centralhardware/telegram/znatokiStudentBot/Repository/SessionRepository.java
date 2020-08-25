package ru.centralhardware.telegram.znatokiStudentBot.Repository;

import org.springframework.data.repository.CrudRepository;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Session;

import java.util.Optional;

public interface SessionRepository extends CrudRepository<Session, String> {

    Optional<Session> findByUuid(String uuid);

}
