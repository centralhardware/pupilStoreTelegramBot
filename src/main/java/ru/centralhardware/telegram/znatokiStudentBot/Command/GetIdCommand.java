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

/**
 * show chat id
 * param: no
 * output format: "id"
 * input format: no
 * access level: unauthorized
 */
@Slf4j
@Component
@Lazy
public class GetIdCommand extends BotCommand {

    private final TelegramService telegramService;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public GetIdCommand(TelegramService telegramService,
                        StatisticService statisticService,
                        TelegramUtils telegramUtils) {
        super("/get_id",
                """
                        получить ид пользователя.
                        """);
        this.telegramService    = telegramService;
        this.statisticService   = statisticService;
        this.telegramUtils      = telegramUtils;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] arguments) {
        log.info(String.format(LoggerUtils.EXECUTING_COMMAND_BY_USER,
                GetIdCommand.class,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        log.info(LoggerUtils.COMMAND_PARAM, Arrays.toString(arguments));
        telegramService.update(chat.getId(), user);
        statisticService.save(Action.EXECUTE_COMMAND_GET_ID, user.getId());
        telegramUtils.sendMessage(String.valueOf(chat.getId()), chat.getId());
    }
}
