package team2.pjt12.matchumoney.domain.persona.controller;

import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.GetMapping;

@RestController
@Log4j2
public class HomeController {

    @GetMapping("/")
    public String home() {
        log.info("================> HomController /");
        return "Matchumoney API 서버 정상 작동 중!";
    }
}
