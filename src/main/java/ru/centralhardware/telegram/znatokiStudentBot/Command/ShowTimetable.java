package ru.centralhardware.telegram.znatokiStudentBot.Command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.centralhardware.telegram.znatokiStudentBot.Builder.InlineKeyboardBuilder;
import ru.centralhardware.telegram.znatokiStudentBot.Config;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.PaymentsSession;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PaymentSessionService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.StatisticService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.DateUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.LoggerUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.util.Arrays;
import java.util.Date;

/**
 * print link to timetable
 * param: no
 * output format: "url"
 * input format: no
 * access level: read
 */
@Slf4j
@Component
@Lazy
public class ShowTimetable extends BotCommand {

    private final TelegramService telegramService;
    private final PaymentSessionService paymentSessionService;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public ShowTimetable(TelegramService telegramService,
                         PaymentSessionService paymentSessionService,
                         StatisticService statisticService,
                         TelegramUtils telegramUtils) {
        super("/timetable_show",
                """
                        получить ссылку на расписание.
                        """);
        this.telegramService        = telegramService;
        this.paymentSessionService  = paymentSessionService;
        this.statisticService       = statisticService;
        this.telegramUtils          = telegramUtils;
    }

    @Override
    public void execute(AbsSender absSender, User user, Chat chat, String[] strings) {
        log.info(String.format(LoggerUtils.EXECUTING_COMMAND_BY_USER,
                GetTelephoneList.class,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        log.info(LoggerUtils.COMMAND_PARAM, Arrays.toString(strings));
        telegramService.update(chat.getId(), user);
        statisticService.save(Action.EXECUTE_COMMAND_SHOW_TIMETABLE, user.getId());
        if (telegramUtils.checkReadAccess(chat.getId(), user)) {
            PaymentsSession paymentsSession = paymentSessionService.create();
            InlineKeyboardBuilder inlineKeyboardBuilder = InlineKeyboardBuilder.
                    create().
                    setChatId(chat.getId()).
                    setText("Просмотр").
                    row().button("Открыть",
                    "sdf",
                    String.format("%s/timetable?sessionId=%s&date=%s",
                            Config.getBaseUrl(),
                            paymentsSession.getUuid(),
                            DateUtils.dateFormat.format(new Date()))).endRow();
            telegramUtils.sendMessage(inlineKeyboardBuilder.build());
        }
    }
}
