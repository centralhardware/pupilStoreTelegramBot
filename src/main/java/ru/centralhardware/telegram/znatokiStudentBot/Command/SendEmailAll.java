package ru.centralhardware.telegram.znatokiStudentBot.Command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.centralhardware.telegram.znatokiStudentBot.Builder.InlineKeyboardBuilder;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Email;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Service.EmailService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.StatisticService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.TelegramBot;
import ru.centralhardware.telegram.znatokiStudentBot.Util.LoggerUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.util.Arrays;
import java.util.ResourceBundle;

/**
 * send email to all pupil that have email
 * param: subject, email text
 * output format: "confirmation message"
 * input format: "/command subject text"
 * access level: write
 */
@Slf4j
@Component
@Lazy
public class SendEmailAll extends BotCommand {

    private final ResourceBundle resourceBundle;
    private final TelegramService telegramService;
    private final EmailService emailService;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public SendEmailAll(ResourceBundle resourceBundle,
                        TelegramService telegramService,
                        EmailService emailService,
                        StatisticService statisticService,
                        TelegramUtils telegramUtils) {
        super("/send_email_all",
                """
                        выполнить рассылку по email-ам. Аргументы: --тема письма-- - --текст письма--. Пример:

                        <code> /send_email_all Первое сентября - Начните заниматься прямо сейчас</code>\s

                        Примечание: не настроено
                        """);
        this.resourceBundle     = resourceBundle;
        this.telegramService    = telegramService;
        this.emailService       = emailService;
        this.statisticService   = statisticService;
        this.telegramUtils      = telegramUtils;
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
        statisticService.save(Action.EXECUTE_COMMAND_SEND_EMAIL, user.getId());
        if (telegramUtils.checkAWriteAccess(chat.getId(), user)) {
            StringBuilder message = new StringBuilder();
            for (String str : strings) {
                message.append(" ").append(str);
            }
            String[] messageArr = message.toString().split("-");
            if (messageArr.length == 2) {
                String uuid = emailService.save(new Email(messageArr[0], messageArr[1])).getUuid();
                telegramUtils.sendMessage(String.format(resourceBundle.getString("EMAIL_DESCRIPTION"),
                        messageArr[0],
                        messageArr[1]),
                        chat.getId());
                InlineKeyboardBuilder inlineKeyboardBuilder = InlineKeyboardBuilder.
                        create().
                        setChatId(chat.getId()).
                        setText(resourceBundle.getString("EXECUTE_MAILING")).
                        row().button(resourceBundle.getString("YES"), TelegramBot.CALLBACK_EMAIL_PREFIX + uuid).endRow();
                telegramUtils.sendMessage(inlineKeyboardBuilder.build());
            } else {
                telegramUtils.sendMessage(resourceBundle.getString("ARGUMENT_ERROR_SEND_MAIL"), chat.getId());
            }
        }
    }
}
