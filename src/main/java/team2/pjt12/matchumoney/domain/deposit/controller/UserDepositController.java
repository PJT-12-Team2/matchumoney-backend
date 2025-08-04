package team2.pjt12.matchumoney.domain.deposit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;  // req로 변경
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.UserDepositResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.service.UserDepositService;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class UserDepositController {
    private final UserDepositService userDepositService;

    @GetMapping("/accounts/{userId}")
    public ResponseEntity<List<UserDepositResponseDTO>> getUserDeposits(@PathVariable String userId) {
        log.info("계좌 조회 API 호출: userId={}", userId);

        try {
            List<UserDepositResponseDTO> deposits = userDepositService.getAccountsByUserId(userId);
            return ResponseEntity.ok(deposits);
        }catch (Exception e) {
            log.error("계좌 조회 API 오류: userId={}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}