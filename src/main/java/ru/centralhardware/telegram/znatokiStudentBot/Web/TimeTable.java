package ru.centralhardware.telegram.znatokiStudentBot.Web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.centralhardware.telegram.znatokiStudentBot.Service.LessonService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PaymentSessionService;
import ru.centralhardware.telegram.znatokiStudentBot.Util.DateUtils;
import ru.centralhardware.telegram.znatokiStudentBot.Web.Dto.TimetableDto;

import java.text.ParseException;
import java.util.Date;
import java.util.ResourceBundle;

@Controller
public class TimeTable {

    public static final String ERROR_TITLE = "errorTitle";
    public static final String ERROR_MESSAGE = "errorMessage";
    private final ResourceBundle resourceBundle;
    private final PaymentSessionService paymentSessionService;
    private final LessonService lessonService;

    public TimeTable(ResourceBundle resourceBundle, PaymentSessionService paymentSessionService, LessonService lessonService) {
        this.resourceBundle = resourceBundle;
        this.paymentSessionService = paymentSessionService;
        this.lessonService = lessonService;
    }

    @GetMapping("/timetable")
    public String timetable(Model model, @RequestParam String sessionId, @RequestParam String date) {
        if (paymentSessionService.find(sessionId).isPresent()) {
            if (!paymentSessionService.find(sessionId).get().isExpire()) {
                Date dt;
                try {
                    dt = DateUtils.dateFormat.parse(date);
                } catch (ParseException e) {
                    model.addAttribute(ERROR_TITLE, "ошибка даты");
                    model.addAttribute(ERROR_MESSAGE, "Ошибка формата даты пожалуйста получите корректную ссылку в боте");
                    return "error";
                }
                model.addAttribute("timetables", TimetableDto.toDto(lessonService.findByDate(dt)));
                model.addAttribute("name", String.format("Расписание на %s", DateUtils.dateFormat.format(dt)));
                model.addAttribute("nextUrl", String.format("/timetable?sessionId=%s&date=%s",
                        sessionId,
                        DateUtils.dateFormat.format(DateUtils.getNextDay(dt))));
                model.addAttribute("previousUrl", String.format("/timetable?sessionId=%s&date=%s",
                        sessionId,
                        DateUtils.dateFormat.format(DateUtils.getPreviousDay(dt))));
                model.addAttribute("date", DateUtils.dateFormat.format(dt));
                model.addAttribute("sessionId", sessionId);
                return "timetable";
            } else {
                model.addAttribute(ERROR_TITLE, resourceBundle.getString("SESSION_EXPIRE"));
                model.addAttribute(ERROR_MESSAGE, "Сессия истекла. Получите новую ссылку в боте");
                return "error";
            }
        } else {
            model.addAttribute(ERROR_TITLE, "Сессия не найдена");
            model.addAttribute(ERROR_MESSAGE, "Сессия не найдена. Получите новую ссылку в боте");
            return "error";
        }
    }

    public String timetableWeek(Model model, String sessionId) {
        if (paymentSessionService.find(sessionId).isPresent()) {
            if (!paymentSessionService.find(sessionId).get().isExpire()) {
                return "timetable_week";
            } else {
                model.addAttribute(ERROR_TITLE, resourceBundle.getString("SESSION_EXPIRE"));
                model.addAttribute(ERROR_MESSAGE, "Сессия истекла. Получите новую ссылку в боте");
                return "error";
            }
        } else {
            model.addAttribute(ERROR_TITLE, "Сессия не найдена");
            model.addAttribute(ERROR_MESSAGE, "Сессия не найдена. Получите новую ссылку в боте");
            return "error";
        }
    }

}