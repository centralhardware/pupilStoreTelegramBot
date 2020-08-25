package ru.centralhardware.telegram.znatokiStudentBot.Command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Service.StatisticService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.LoggerUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * show start message
 * param: no
 * output format: "start message"
 * input format: no
 * access level: unauthorized
 */
@Slf4j
@Component
@Lazy
public class StartCommand extends BotCommand {

    private final ResourceBundle resourceBundle;
    private final TelegramService service;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public StartCommand(ResourceBundle resourceBundle,
                        TelegramService service,
                        StatisticService statisticService,
                        TelegramUtils telegramUtils) {
        super("/start",
                """
                        Показать стартовый текст.
                        """);
        this.resourceBundle     = resourceBundle;
        this.service            = service;
        this.statisticService   = statisticService;
        this.telegramUtils      = telegramUtils;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(String.format(LoggerUtils.EXECUTING_COMMAND_BY_USER,
                StartCommand.class,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        log.info(LoggerUtils.COMMAND_PARAM, Arrays.toString(strings));
        service.update(chat.getId(), user);
        statisticService.save(Action.EXECUTE_COMMAND_START, user.getId());
        telegramUtils.sendMessage(resourceBundle.getString("START_MESSAGE"), chat.getId());
    }

}
