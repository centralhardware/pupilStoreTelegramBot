package ru.centralhardware.telegram.znatokiStudentBot.Repository;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.TelegramUser;

import java.util.List;

@Repository
public interface TelegramUserRepository extends CrudRepository<TelegramUser, Long> {

    List<TelegramUser> findAll();

}
