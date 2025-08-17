package team2.pjt12.matchumoney.domain.push.controller;

import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.push.service.PushService;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@RestController
@RequestMapping("/api/push")
@RequiredArgsConstructor
@Slf4j
public class PushController {
    private final PushService pushService;

    @PostMapping("/tokens")
    public ResponseEntity<?> saveToken(@RequestBody TokenReq req,
                                       @RequestHeader(value = "User-Agent", required = false) String ua) {
        if (req == null || req.token == null || req.token.isBlank()) {
            return ResponseEntity.badRequest().body("token required");
        }

        Long userId = getCurrentUser().getUserId();
        log.info("userId: {}, token: {}, userAgent: {}", userId, req.token, ua);
        pushService.upsert(userId, req.token, ua);
        return ResponseEntity.status(201).build();
    }

    @DeleteMapping("/tokens")
    public ResponseEntity<?> deleteToken(@RequestBody TokenReq req) {
        if (req != null && req.token != null) pushService.delete(req.token);
        return ResponseEntity.noContent().build();
    }

    // 단건 발송 테스트(선택)
    @PostMapping("/test")
    public ResponseEntity<?> test(@RequestBody TokenReq req,
                                  @RequestParam(defaultValue = "테스트") String title,
                                  @RequestParam(defaultValue = "웹 푸시") String body,
                                  @RequestParam(defaultValue = "http://localhost:5173/") String link) throws Exception {
        String id = pushService.sendTest(req.token, title, body, link);
        return ResponseEntity.ok(id);
    }

    @Data
    static class TokenReq { String token; }
}