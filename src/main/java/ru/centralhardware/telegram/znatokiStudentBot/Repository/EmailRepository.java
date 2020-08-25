package ru.centralhardware.telegram.znatokiStudentBot.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Email;

@Repository
public interface EmailRepository extends CrudRepository<Email, String> {
}
