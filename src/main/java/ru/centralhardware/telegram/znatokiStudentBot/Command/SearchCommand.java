package ru.centralhardware.telegram.znatokiStudentBot.Command;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.extensions.bots.commandbot.commands.BotCommand;
import org.telegram.telegrambots.meta.api.objects.Chat;
import org.telegram.telegrambots.meta.api.objects.User;
import org.telegram.telegrambots.meta.bots.AbsSender;
import ru.centralhardware.telegram.znatokiStudentBot.Builder.InlineKeyboardBuilder;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.Action;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Pupil;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PupilService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.StatisticService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.TelegramBot;
import ru.centralhardware.telegram.znatokiStudentBot.Util.LoggerUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.util.Arrays;
import java.util.List;
import java.util.ResourceBundle;

/**
 * show user by text filed using full text search
 * param: search query
 * output format:
 * "fio
 * age л. classNumber кл.
 * button(информация) button(редактирование)
 * button(удалить) button(добавить оплату)
 * button(добавить занятие)"
 * input format: "/command/ search-query"
 * access level: read
 */
@Slf4j
@Component
@Lazy
public class SearchCommand extends BotCommand {

    private final ResourceBundle resourceBundle;
    public static final String ADD_PAYMENT_CALLBACK_PREFIX = "/add_payment";
    private final PupilService pupilService;
    private final TelegramService telegramService;
    private final StatisticService statisticService;
    private final TelegramUtils telegramUtils;

    public SearchCommand(ResourceBundle resourceBundle,
                         PupilService pupilService,
                         TelegramService telegramService,
                         StatisticService statisticService,
                         TelegramUtils telegramUtils) {
        super("/s",
                """
                выполнить поиск ученика по текстовым полям. Аргументы: поисковый запрос. Пример:

                <code> /s Михаил </code>
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
                SearchCommand.class,
                user.getUserName(),
                user.getFirstName(),
                user.getLastName()));
        log.info(LoggerUtils.COMMAND_PARAM, Arrays.toString(arguments));
        telegramService.update(chat.getId(), user);
        statisticService.save(Action.EXECUTE_COMMAND_SEARCH, user.getId());
        if (telegramUtils.checkReadAccess(chat.getId(), user)){
            if (arguments.length == 0){
                telegramUtils.sendMessage("Вы не ввели текст запроса. Пример: /s Иванов", chat.getId());
                return;
            }
            String searchText = Arrays.toString(arguments);
            List<Pupil>  searchResult = null;
            try {
                searchResult = pupilService.search(searchText);
            } catch (InterruptedException e) {
                log.warn("", e);
            }
            if (searchResult != null && searchResult.size() > 0) {
                telegramUtils.sendMessage(resourceBundle.getString("SEARCH_RESULT"), chat.getId());
                int i = 1;
                for (Pupil pupil : searchResult) {
                    InlineKeyboardBuilder inlineKeyboardBuilder = InlineKeyboardBuilder.
                            create().
                            setChatId(chat.getId()).
                            setText(String.format("%s %s %s \n%s л. %s кл.",
                                    pupil.getName(),
                                    pupil.getSecondName(),
                                    pupil.getLastName(),
                                    pupil.getAge(),
                                    pupil.getClassNumber())).
                            row().
                            button("информация", TelegramBot.USER_INFO_COMMAND + pupil.getId()).
                            button("редактировать", TelegramBot.EDIT_USER_COMMAND + pupil.getId()).
                            endRow().
                            row().
                            button("удалить", TelegramBot.DELETE_USER_COMMAND + pupil.getId()).
                            button("Добавить оплату", ADD_PAYMENT_CALLBACK_PREFIX + pupil.getId()).
                            endRow().row().
                            button("Добавить занятие", "/add_lesson"+ pupil.getId()).endRow();
                    telegramUtils.sendMessage(inlineKeyboardBuilder.build());
                }
            } else {
                telegramUtils.sendMessage(resourceBundle.getString("NOTHING_FOUND"), chat.getId());
            }
        }
    }
}
