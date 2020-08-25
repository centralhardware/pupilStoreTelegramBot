package ru.centralhardware.telegram.znatokiStudentBot.Web;

import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

import javax.servlet.http.HttpServletRequest;
import java.util.ResourceBundle;

@Controller
public class ZnatokiErrorController implements ErrorController {

    private final ResourceBundle resourceBundle;

    public ZnatokiErrorController(ResourceBundle resourceBundle) {
        this.resourceBundle = resourceBundle;
    }

    @SuppressWarnings("SameReturnValue")
    @RequestMapping(value = "error", method = RequestMethod.GET)
    public String handleError(HttpServletRequest httpRequest, Model model) {
        model.addAttribute(Payment.ERROR_TITLE, resourceBundle.getString("ERROR"));
        model.addAttribute(Payment.ERROR_MESSAGE, resourceBundle.getString("HAPPENED_UNKNOWN_ERROR"));
        return Edit.ERROR_PAGE_NAME;
    }

    @Override
    public String getErrorPath() {
        return "/error";
    }

}
