package ru.centralhardware.telegram.znatokiStudentBot.Util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.api.objects.replykeyboard.ReplyKeyboardRemove;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import ru.centralhardware.telegram.znatokiStudentBot.Command.GetEmailList;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Role;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.TelegramBot;

import java.util.ResourceBundle;

@Slf4j
@Component
public class TelegramUtils {

    public static final String UNABLE_TO_SEND_MESSAGE = "unable to send message";
    public static final String SEND_MESSAGE_FOR_CHAT = "send message %s for chat %s";
    private static final String PARSE_MODE_MARKDOWN = "markdown";
    private static final String BOLD_MAKER = "*";
    @Autowired
    private ResourceBundle resourceBundle;
    @Autowired
    private TelegramService telegramService;
    @Autowired
    private TelegramBot telegramBot;
    @Autowired
    private ReplyKeyboardRemove replyKeyboardRemove;

    /**
     * @param text message to make bold
     * @return text wrapped in markdown bold symbol
     */
    public static String makeBold(String text) {
        return BOLD_MAKER + text + BOLD_MAKER + "\n";
    }

    /**
     * @param text number to make bold
     * @return text wrapped in markdown bold symbol
     */
    public static String makeBold(int text) {
        return BOLD_MAKER + text + BOLD_MAKER;
    }

    public void sendMessage(SendMessage sendMessage) {
        try {
            log.info(String.format(SEND_MESSAGE_FOR_CHAT, sendMessage.getText(), sendMessage.getChatId()));
            telegramBot.execute(sendMessage);
        } catch (TelegramApiException e) {
            log.warn(UNABLE_TO_SEND_MESSAGE, e);
        }
    }

    public void sendMessage(String text, long chatId) {
        try {
            log.info(String.format(SEND_MESSAGE_FOR_CHAT, text, chatId));
            telegramBot.execute(SendMessage.
                    builder().
                    chatId(String.valueOf(chatId)).
                    text(text).
                    build()).getMessageId();
        } catch (TelegramApiException e) {
            log.warn(UNABLE_TO_SEND_MESSAGE, e);
        }
    }

    public void sendMessageAndRemoveKeyboard(SendMessage sendMessage) {
        sendMessage.setReplyMarkup(replyKeyboardRemove);
        sendMessage(sendMessage);
    }

    public void sendMessageAndRemoveKeyboard(String text, long chatId) {
        sendMessage(SendMessage.
                builder().
                chatId(String.valueOf(chatId)).
                text(text).
                replyMarkup(replyKeyboardRemove).
                build());
    }

    public void sendMessageWithMarkdown(String text, long chatId) {
        sendMessage(SendMessage.
                builder().
                chatId(String.valueOf(chatId)).
                text(text).
                parseMode(PARSE_MODE_MARKDOWN).
                build());
    }

    public void sendMessageWithMarkdownAndRemoveKeyboard(String text, long chatId) {
        sendMessage(SendMessage.
                builder().
                chatId(String.valueOf(chatId)).
                text(text).
                parseMode(PARSE_MODE_MARKDOWN).
                replyMarkup(replyKeyboardRemove).
                build());
    }

    private boolean checkAccess(long chatId, User user, String right) {
        boolean authorized = false;
        var telegramUserOptional = telegramService.findById(chatId);
        if (telegramUserOptional.isPresent()) {
            var telegramUser = telegramUserOptional.get();
            switch (right) {
                case "read" -> {
                    if (telegramUser.hasReadRight()) authorized = true;
                }
                case "admin" -> {
                    if (telegramUser.getRole() == Role.ADMIN) authorized = true;
                }
                case "write" -> {
                    if (telegramUser.hasWriteRight()) authorized = true;
                }
            }
            if (!authorized) {
                sendMessage(resourceBundle.getString("ACCESS_DENIED"), chatId);
                log.warn(String.format(LoggerUtils.UNAUTHORIZED_ACCESS_USER_TRY_TO_EXECUTE,
                        user.getUserName(),
                        user.getFirstName(),
                        user.getLastName(),
                        chatId,
                        GetEmailList.class));
            }
        }
        return authorized;
    }

    public boolean checkReadAccess(long chatId, User user) {
        return checkAccess(chatId, user, "read");
    }

    public boolean checkAdminAccess(long chatId, User user) {
        return checkAccess(chatId, user, "admin");
    }

    public boolean checkAWriteAccess(long chatId, User user) {
        return checkAccess(chatId, user, "write");
    }

}
