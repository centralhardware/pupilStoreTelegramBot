package ru.centralhardware.telegram.znatokiStudentBot.Web;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PaymentService;
import ru.centralhardware.telegram.znatokiStudentBot.Service.PaymentSessionService;

import java.util.ResourceBundle;

@Controller
public class Payment {

    public static final String ERROR_TITLE = "errorTitle";
    public static final String ERROR_MESSAGE = "errorMessage";
    private final ResourceBundle resourceBundle;
    private final PaymentSessionService paymentSessionService;
    private final PaymentService paymentService;


    public Payment(ResourceBundle resourceBundle, PaymentSessionService paymentSessionService, PaymentService paymentService) {
        this.resourceBundle = resourceBundle;
        this.paymentSessionService = paymentSessionService;
        this.paymentService = paymentService;
    }

    @GetMapping("/payments")
    public String payments(Model model, @RequestParam String sessionId) {
        if (paymentSessionService.find(sessionId).isPresent()) {
            if (!paymentSessionService.find(sessionId).get().isExpire()) {
                model.addAttribute("payments", paymentService.getLastMonth());
                return "payment";
            }
        }
        model.addAttribute(ERROR_TITLE, resourceBundle.getString("SESSION_EXPIRE"));
        model.addAttribute(ERROR_MESSAGE, "Сессия истекла. Получите новую ссылку в боте");
        return "error";
    }

}
