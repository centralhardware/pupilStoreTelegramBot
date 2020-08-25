package ru.centralhardware.telegram.znatokiStudentBot.Command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PupilService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.StatisticService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.LoggerUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * show list of emails
 * param: no
 * output format: "email - fio"
 * input format: no
 * access level: read
 */
@Slf4j
@Component
@Lazy
public class GetEmailList extends BotCommand {

    private final ResourceBundle resourceBundle;
    private final PupilService pupilService;
    private final TelegramService telegramService;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public GetEmailList(ResourceBundle resourceBundle,
                        PupilService pupilService,
                        TelegramService telegramService,
                        StatisticService statisticService,
                        TelegramUtils telegramUtils) {
        super("/show_email_list",
                """
                        получить список email-ов.
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
                GetEmailList.class,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        log.info(LoggerUtils.COMMAND_PARAM, Arrays.toString(arguments));
        telegramService.update(chat.getId(), user);
        statisticService.save(Action.EXECUTE_COMMAND_GET_EMAIL_LIST, user.getId());
        if (telegramUtils.checkReadAccess(chat.getId(), user)) {
            if (pupilService.getEmail().size() == 0) {
                telegramUtils.sendMessage(resourceBundle.getString("DATABASE_NOT_CONTAIN_EMAILS"), chat.getId());
                return;
            }
            StringBuilder stringBuilder = new StringBuilder();
            pupilService.getEmail().forEach((email, fio) -> stringBuilder.append(email).append(" - ").append(fio).append("\n"));
            telegramUtils.sendMessage(stringBuilder.toString(), chat.getId());
        }
    }
}
