package team2.pjt12.matchumoney.domain.education.controller;

import team2.pjt12.matchumoney.domain.education.dto.WebtoonResponseDTO;
import team2.pjt12.matchumoney.domain.education.service.WebtoonService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/webtoon")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
@Slf4j
public class WebtoonController {

    private final WebtoonService webtoonService;

    /**
     * 전체 웹툰 목록 조회
     * GET /api/webtoon
     */
    @GetMapping
    public ResponseEntity<List<WebtoonResponseDTO>> getAllWebtoons() {
        try {
            log.info("웹툰 목록 조회 요청");
            List<WebtoonResponseDTO> webtoons = webtoonService.getAllWebtoons();
            log.info("웹툰 목록 조회 성공. 조회된 웹툰 수: {}", webtoons.size());
            return ResponseEntity.ok(webtoons);
        } catch (Exception e) {
            log.error("웹툰 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }

    /**
     * 전체 웹툰의 상위 4개 조회
     * GET /api/webtoon/top4
     */
    @GetMapping("/top4")
    public ResponseEntity<List<WebtoonResponseDTO>> getTop4Webtoons() {
        try {
            log.info("웹툰 목록 조회 ");
            List<WebtoonResponseDTO> webtoons = webtoonService.getTop4Webtoons();
            return ResponseEntity.ok(webtoons);
        } catch (Exception e) {
            log.error("웹툰 목록 조회 실패", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).build();
        }
    }
}