package ru.centralhardware.telegram.znatokiStudentBot.Command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Role;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.TelegramUser;
import ru.centralhardware.telegram.znatokiStudentBot.Service.StatisticService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.LoggerUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.StringUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * grant access to specific uer
 * param: chat id -
 * output format: "message with result"
 * input format "/command chat-id"
 * access level: admin
 * note: giving id must be register in database
 */
@Slf4j
@Component
@Lazy
public class GrandAccessCommand extends BotCommand {

    private final ResourceBundle resourceBundle;
    private final TelegramService telegramService;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public GrandAccessCommand(ResourceBundle resourceBundle,
                              TelegramService telegramService,
                              StatisticService statisticService,
                              TelegramUtils telegramUtils) {
        super("/grant_access",
                """
                        изменить права доступа пользователя бота. Аргументы: --id пользователя-- --r|rw|admin--. Пример:\s

                        <code> /grant_access 522104700 rw </code>

                        Уровни доступа:\s
                        r - чтение
                        rw - чтение и запись
                        admin - возможность менять права другим пользователям
                        """);
        this.resourceBundle     = resourceBundle;
        this.telegramService    = telegramService;
        this.statisticService   = statisticService;
        this.telegramUtils      = telegramUtils;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(String.format(LoggerUtils.EXECUTING_COMMAND_BY_USER,
                GrandAccessCommand.class,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        log.info(LoggerUtils.COMMAND_PARAM, Arrays.toString(arguments));
        telegramService.update(chat.getId(), user);
        statisticService.save(Action.EXECUTE_COMMAND_GRAND_ACCESS, user.getId());
        if (telegramUtils.checkAdminAccess(chat.getId(), user)) {
            if (!StringUtils.isNumeric(arguments[0])) {
                telegramUtils.sendMessage(resourceBundle.getString("INVALID_ARGUMENT_GRANT_ACCESS"), chat.getId());
                return;
            }
            Optional<TelegramUser> telegramUserOptional1 = telegramService.findById(Long.parseLong(arguments[0]));
            if (telegramUserOptional1.isPresent()) {
                TelegramUser telegramUser1 = telegramUserOptional1.get();
                switch (arguments[1]) {
                    case "r" -> telegramUser1.setRole(Role.READ);
                    case "rw" -> telegramUser1.setRole(Role.READ_WRITE);
                    case "admin" -> telegramUser1.setRole(Role.ADMIN);
                    case "no" -> telegramUser1.setRole(Role.UNAUTHORIZED);
                    default -> {
                        telegramUtils.sendMessage(resourceBundle.getString("INVALID_RIGHT"), chat.getId());
                        return;
                    }
                }
                telegramService.save(telegramUser1);
                telegramUtils.sendMessage(resourceBundle.getString("RIGH_SUCCESS_UPDATE"), chat.getId());
            } else {
                telegramUtils.sendMessage(resourceBundle.getString("USER_NOT_FOUND"), chat.getId());
            }
        }
    }
}