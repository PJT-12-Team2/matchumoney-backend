package team2.pjt12.matchumoney.domain.push.controller;

import io.swagger.annotations.*;
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
@Api(tags = "Alarm API", description = "푸시 토큰 및 알림 API")
public class PushController {
    private final PushService pushService;

    @PostMapping("/tokens")
    @ApiOperation(value = "푸시 토큰 저장", notes = "FCM 등의 푸시 토큰을 저장합니다.")
    public ResponseEntity<?> saveToken(@ApiParam(value = "토큰 정보", required = true) @RequestBody TokenReq req,
                                       @RequestHeader(value = "User-Agent", required = false) String ua) {
        if (req == null || req.token == null || req.token.isBlank()) {
            return ResponseEntity.badRequest().body("token required");
        }

        Long userId = getCurrentUser().getUserId();
        log.info("userId: {}, token: {}, userAgent: {}", userId, req.token, ua);
        pushService.upsert(userId, req.token, ua);
        return ResponseEntity.status(201).build();
    }

    @ApiOperation(value = "푸시 토큰 삭제", notes = "사용자 디바이스에서 더 이상 사용하지 않는 푸시 토큰을 삭제합니다.")
    @DeleteMapping("/tokens")
    @ApiParam(value = "토큰 정보 DTO", required = true)
    public ResponseEntity<?> deleteToken(@RequestBody TokenReq req) {
        if (req != null && req.token != null) pushService.delete(req.token);
        return ResponseEntity.noContent().build();
    }

    // 단건 발송 테스트(선택)
    @PostMapping("/test")
    @ApiOperation(value = "푸시 테스트 발송", notes = "테스트용으로 특정 토큰에 알림을 전송합니다.")
    public ResponseEntity<?> test(@RequestBody TokenReq req,
                                  @RequestParam(defaultValue = "테스트") String title,
                                  @RequestParam(defaultValue = "웹 푸시") String body,
                                  @RequestParam(defaultValue = "http://localhost:5173/") String link) throws Exception {
        String id = pushService.sendTest(req.token, title, body, link);
        return ResponseEntity.ok(id);
    }

    @ApiModel(description = "FCM 등 푸시 토큰 요청 DTO")
    @Data
    static class TokenReq {
        @ApiModelProperty(value = "디바이스 푸시 토큰", example = "fcm_token_abc123", required = true)
        String token;
    }
}