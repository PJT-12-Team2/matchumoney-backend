package team2.pjt12.matchumoney.domain.push.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.push.dto.NewProductResponseDTO;
import team2.pjt12.matchumoney.domain.push.mapper.ProductScanMapper;
import team2.pjt12.matchumoney.domain.push.mapper.WatermarkMapper;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class ProductChangeScanService {

    private final WatermarkMapper watermarkMapper;
    private final ProductScanMapper productScanMapper;
    private final PushService pushService; // 이미 구현한 sendToUsers(...) 사용


    // 매 시각 0분/10분/20분/30분/40분/50분에 실행 (초 분 시 월 요일)
    @Scheduled(cron = "0 0,10,20,30,40,50 * * * *")
    public void run() {
        scanAndNotify("card",    "CARD");
        scanAndNotify("deposit", "DEPOSIT");
        scanAndNotify("saving",  "SAVING");
    }

    private void scanAndNotify(String table, String type) {
        LocalDateTime since = watermarkMapper.getLastChecked(table);
        if (since == null) since = LocalDateTime.of(1970,1,1,0,0,0);

        List<NewProductResponseDTO> newly = switch (table) {
            case "card"    -> productScanMapper.findNewCards(since);
            case "deposit" -> productScanMapper.findNewDeposits(since);
            case "saving"  -> productScanMapper.findNewSavings(since);
            default -> List.of();
        };

        if (newly.isEmpty()) {
            log.info("[SCAN] {}: no new rows since {}", table, since);
            watermarkMapper.upsertLastChecked(table, LocalDateTime.now());
            return;
        }

        // 이번 배치에서 처리한 최대 생성시간 = 다음 스캔 시작점
        LocalDateTime maxTs = newly.stream()
                .map(NewProductResponseDTO::getCreatedTime)
                .filter(Objects::nonNull)
                .max(LocalDateTime::compareTo)
                .orElse(LocalDateTime.now());

        // personaId 별로 묶기 (null 제외)
        Map<Long, List<NewProductResponseDTO>> byPersona = newly.stream()
                .filter(x -> x.getPersonaId() != null)
                .collect(Collectors.groupingBy(NewProductResponseDTO::getPersonaId));

        for (Map.Entry<Long, List<NewProductResponseDTO>> e : byPersona.entrySet()) {
            Long personaId = e.getKey();
            List<NewProductResponseDTO> items = e.getValue();

            // 이 페르소나를 가진 사용자들
            List<Long> userIds = productScanMapper.findUserIdsByPersona(personaId);
            if (userIds.isEmpty()) continue;

            // 사용자별 신규 product_id 디듀프
            Map<Long, List<Long>> userNewProductIds = new HashMap<>();
            for (Long uid : userIds) {
                List<Long> newIds = items.stream()
                        .map(NewProductResponseDTO::getId)
                        .filter(pid -> productScanMapper.existsPushLog(uid, type, pid) == 0)
                        .toList();
                if (!newIds.isEmpty()) {
                    userNewProductIds.put(uid, newIds);
                }
            }
            if (userNewProductIds.isEmpty()) continue;

            // 메시지/링크
            String title = switch (type) {
                case "CARD"    -> "새 카드 추천이 업데이트됐어요";
                case "DEPOSIT" -> "새 예금 상품이 업데이트됐어요";
                case "SAVING"  -> "새 적금 상품이 업데이트됐어요";
                default -> "새 추천이 업데이트됐어요";
            };
            String body = "지금 확인해 보세요.";
            String link = switch (type) {
                case "CARD"    -> "http://localhost:5173/persona/cards";
                case "DEPOSIT" -> "http://localhost:5173/persona/deposits";
                case "SAVING"  -> "http://localhost:5173/persona/savings";
                default -> "http://localhost:5173/";
            };

            List<Long> targets = new ArrayList<>(userNewProductIds.keySet());
            int success = pushService.sendToUsers(
                    targets,
                    title,
                    body,
                    link,
                    Map.of("type", type.toLowerCase(), "personaId", String.valueOf(personaId))
            );
            log.info("[PUSH] {} persona={} targets={} success={}", type, personaId, targets.size(), success);

            // 디듀프 로그 저장 (성공/실패와 무관하게 기록하여 재중복 방지)
            for (Map.Entry<Long, List<Long>> en : userNewProductIds.entrySet()) {
                Long uid = en.getKey();
                for (Long pid : en.getValue()) {
                    try {
                        productScanMapper.insertPushLog(uid, type, pid);
                    } catch (Exception ignore) {}
                }
            }
        }

        // 워터마크 갱신
        watermarkMapper.upsertLastChecked(table, maxTs);
    }
}