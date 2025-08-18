package team2.pjt12.matchumoney.domain.persona.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.log4j.Log4j2;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@Log4j2
@Api(tags = "Test API", description = "서버 상태 확인용 API")
public class HomeController {
    @ApiOperation(
            value = "API 서버 상태 확인",
            notes = "서버가 정상적으로 동작 중인지 확인할 수 있는 엔드포인트입니다."
    )
    
    @GetMapping("/")
    public String home() {
        log.info("================> HomController /");
        return "Matchumoney API 서버 정상 작동 중!";
    }
}
