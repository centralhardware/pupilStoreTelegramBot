package ru.centralhardware.telegram.znatokiStudentBot.Web;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class Healthcheck {

    @GetMapping(value = "healthcheck")
    public ResponseEntity<?> check() {
        return ResponseEntity.ok("ok");
    }

}
