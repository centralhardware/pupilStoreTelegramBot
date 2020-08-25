package ru.centralhardware.telegram.znatokiStudentBot;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import ru.centralhardware.telegram.znatokiStudentBot.Builder.InlineKeyboardBuilder;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Enum.LessonTime;
import ru.centralhardware.telegram.znatokiStudentBot.Entity.Lesson;
import ru.centralhardware.telegram.znatokiStudentBot.Service.LessonService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PupilService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.TelegramService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.DateUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Util.TelegramUtils;

import java.text.SimpleDateFormat;
import java.util.Date;

@Component
public class ScheduledTasks {

    private static final Logger log = LoggerFactory.getLogger(ScheduledTasks.class);
    private final PupilService pupilService;
    private final MailSender mailSender;
    private final TelegramService telegramService;
    private final LessonService lessonService;
    private final TelegramUtils telegramUtils;

    public ScheduledTasks(PupilService pupilService,
                          MailSender mailSender,
                          TelegramService telegramService,
                          LessonService lessonService,
                          TelegramBot telegramBot, TelegramUtils telegramUtils) {
        this.pupilService = pupilService;
        this.mailSender = mailSender;
        this.telegramService = telegramService;
        this.lessonService = lessonService;
        this.telegramUtils = telegramUtils;
    }

    @Scheduled(cron = "0 0 0 31 5 *")
    public void updateClassNumber() {
        log.info("start increment classNumber");
        pupilService.getAll().forEach(pupil -> {
            if (pupil.getClassNumber() != 11 && pupil.getClassNumber() != -1) {
                pupil.incrementClassNumber();
                pupilService.save(pupil);
            }
        });
        log.info("finish increment classNumber");
    }

    @Scheduled(cron = "0 0 7 * * *")
    public void notifyBirthDay() {
        log.info("start checking birthday");
        pupilService.getAll().forEach(pupil -> {
            log.info("check {}, date of birth = {}", pupil.getId(), pupil.getDateOfBirth());
            if (DateUtils.isBirthday(pupil.getDateOfBirth())) {
                log.info("birthday user today");
                mailSender.send(Config.getBirthText(), "С днем рождения", pupil.getEmail());
                telegramService.getReadRightUser().forEach(telegramUser -> telegramUtils.sendMessage(SendMessage.builder().
                        chatId(telegramUser.getId().toString()).
                        text(String.format("День рождения у %s %s %s телефон: %s email: %s",
                                pupil.getSecondName(),
                                pupil.getName(),
                                pupil.getLastName(),
                                pupil.getTelephone(),
                                pupil.getEmail())).build()));
            }
        });
        log.info("finish checking birthday");
    }

    @Scheduled(cron = "0 0 * * * *")
    public void checkLessonProcessed() {
        LessonTime lessonTime = LessonTime.getConstByStartTime(new SimpleDateFormat("H:m").format(new Date()));
        var lessons = lessonService.findByLessonTimes(lessonTime);
        for (Lesson lesson : lessons) {
            InlineKeyboardBuilder inlineKeyboardBuilder = InlineKeyboardBuilder.
                    create().
                    setChatId(lesson.getCreatedBy().getId()).
                    setText(lesson + "\nЗанятие состоялось?").
                    row().button("Да", "/lesson_processed" + lesson.getId()).endRow();
            telegramUtils.sendMessage(inlineKeyboardBuilder.build());
        }
    }

}
