package ru.centralhardware.telegram.znatokiStudentBot.Service;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.User;
import ru.centralhardware.telegram.znatokiStudentBot.Config;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Role;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.TelegramUser;
import ru.centralhardware.telegram.znatokiStudentBot.Repository.TelegramUserRepository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Service
public class TelegramService {

    private final TelegramUserRepository repository;

    public TelegramService(TelegramUserRepository repository) {
        this.repository = repository;
    }

    public void update(Long id, User user) {
        if (!repository.existsById(id)) {
            TelegramUser telegramUser;
            telegramUser = new TelegramUser(id, user.getUserName(), user.getFirstName(), user.getLastName(), Role.UNAUTHORIZED);
            if (id.equals(Config.getAdminId())) {
                telegramUser = new TelegramUser(id, user.getUserName(), user.getFirstName(), user.getLastName(), Role.ADMIN);
            }
            repository.save(telegramUser);
        } else {
            TelegramUser telegramUser = repository.findById(id).get();
            telegramUser.setFirstName(user.getFirstName());
            telegramUser.setLastName(user.getLastName());
            telegramUser.setUsername(user.getUserName());
            if (id.equals(Config.getAdminId())) {
                telegramUser.setRole(Role.ADMIN);
            }
            repository.save(telegramUser);
        }
    }

    public Optional<TelegramUser> findById(Long id) {
        return repository.findById(id);
    }

    public List<TelegramUser> getAll() {
        return repository.findAll();
    }

    public List<TelegramUser> getReadRightUser() {
        return getAll().stream().filter(TelegramUser::hasReadRight).collect(Collectors.toList());
    }

    public void save(TelegramUser telegramUser) {
        repository.save(telegramUser);
    }

    public boolean hasWriteRight(long id) {
        return repository.findById(id).map(TelegramUser::hasWriteRight).orElse(false);
    }

    public boolean hasReadRight(long id) {
        return repository.findById(id).map(TelegramUser::hasReadRight).orElse(false);
    }

    public boolean isAdmin(long id) {
        return repository.findById(id).filter(telegramUser -> telegramUser.getRole() == Role.ADMIN).isPresent();
    }

    public boolean isUnauthorized(long id) {
        return repository.findById(id).filter(telegramUser -> telegramUser.getRole() == Role.UNAUTHORIZED).isPresent();
    }

}