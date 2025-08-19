package team2.pjt12.matchumoney.domain.deposit.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "User Deposit API", description = "사용자 예금 계좌 조회 API")
public class UserDepositController {
    private final UserDepositService userDepositService;

    @ApiOperation(
            value = "사용자 예금 계좌 조회",
            notes = "특정 사용자 ID의 예금 계좌 목록을 조회합니다."
    )
    @GetMapping("/accounts/{userId}")
    public ResponseEntity<List<UserDepositResponseDTO>> getUserDeposits(@ApiParam(value = "사용자 고유 ID", example = "1", required = true)
                                                                        @PathVariable String userId) {
//        log.info("계좌 조회 API 호출: userId={}", userId);

        try {
            List<UserDepositResponseDTO> deposits = userDepositService.getAccountsByUserId(userId);
            return ResponseEntity.ok(deposits);
        } catch (Exception e) {
//            log.error("계좌 조회 API 오류: userId={}", userId, e);
            return ResponseEntity.internalServerError().build();
        }
    }
}