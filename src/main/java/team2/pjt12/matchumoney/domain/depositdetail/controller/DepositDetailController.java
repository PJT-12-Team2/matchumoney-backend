package team2.pjt12.matchumoney.domain.depositdetail.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.service.DepositDetailService;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

import java.security.Principal;

@Slf4j
@RestController
@RequestMapping("/api/deposit-products")
@RequiredArgsConstructor
@Api(tags = "Deposit Detail API", description = "예금 상품 상세 및 좋아요 API")
public class DepositDetailController {

    private final DepositDetailService depositDetailService;

    @ApiOperation(
            value = "예금 상세 조회",
            notes = "예금 상품 ID로 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public DepositDetailResponseDTO getDepositProduct(@ApiParam(value = "예금 상품 ID", example = "10", required = true)
                                                      @PathVariable Long id,
                                                      @AuthenticationPrincipal UserDetailsImpl user) {
        UserVO vo = user.getUser();       // 사용자 ID 조회
        log.info("로그인 사용자: {} / {}", vo.getUserId(), vo.getEmail());
        return depositDetailService.getDepositDetailById(vo.getUserId(), id);
    }

    @ApiOperation(
            value = "예금 좋아요 등록",
            notes = "해당 예금 상품에 좋아요를 표시합니다."
    )
    @PostMapping("/{id}/likes")
    public LikeStatusResponseDTO likeDeposit(@ApiParam(value = "예금 상품 ID", example = "10", required = true)
                                             @PathVariable Long id,
                                             @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return depositDetailService.isUserLikedDeposit(userId, id);
    }

    @ApiOperation(
            value = "예금 좋아요 해제",
            notes = "해당 예금 상품의 좋아요를 해제합니다."
    )
    @DeleteMapping("/{id}/likes")
    public LikeStatusResponseDTO unlikeDeposit(@ApiParam(value = "예금 상품 ID", example = "10", required = true)
                                               @PathVariable Long id,
                                               @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return depositDetailService.isUserLikedDeposit(userId, id);
    }
}