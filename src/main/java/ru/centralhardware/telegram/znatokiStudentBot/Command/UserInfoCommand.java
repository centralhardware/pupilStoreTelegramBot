package ru.centralhardware.telegram.znatokiStudentBot.Command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Pupil;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PupilService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.StatisticService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.LoggerUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.util.Arrays;
import java.util.Optional;
import java.util.ResourceBundle;

/**
 * get user info by id
 * param: id of pupil
 * output format: "pupil toString"
 * input format: "/command pupil-id"
 * access level: read
 */
@Slf4j
@Component
@Lazy
public class UserInfoCommand extends BotCommand {

    private final ResourceBundle resourceBundle;
    private final PupilService pupilService;
    private final TelegramService telegramService;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public UserInfoCommand(ResourceBundle resourceBundle,
                           PupilService pupilService,
                           TelegramService telegramService,
                           StatisticService statisticService,
                           TelegramUtils telegramUtils) {
        super("/i",
                """
                        Вывести данные ученика по его ID. Пример:

                        <code> /i 1</code>
                        """);
        this.resourceBundle     = resourceBundle;
        this.pupilService       = pupilService;
        this.telegramService    = telegramService;
        this.statisticService   = statisticService;
        this.telegramUtils      = telegramUtils;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(String.format(LoggerUtils.EXECUTING_COMMAND_BY_USER,
                UserInfoCommand.class,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        log.info(LoggerUtils.COMMAND_PARAM, Arrays.toString(arguments));
        telegramService.update(chat.getId(), user);
        statisticService.save(Action.EXECUTE_COMMAND_USER_INFO, user.getId());
        if (telegramUtils.checkReadAccess(chat.getId(), user)) {
            Optional<Pupil> pupilOptional = pupilService.findById(Integer.valueOf(arguments[0]));
            if (pupilOptional.isPresent()) {
                Pupil pupil = pupilOptional.get();
                telegramUtils.sendMessageWithMarkdown(pupil.toString(), chat.getId());
            } else {
                telegramUtils.sendMessage(resourceBundle.getString("PUPIL_NOT_FOUND"), chat.getId());
            }
        }
    }
}
