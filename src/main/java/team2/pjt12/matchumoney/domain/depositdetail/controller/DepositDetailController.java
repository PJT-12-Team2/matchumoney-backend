package team2.pjt12.matchumoney.domain.depositdetail.controller;

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
public class DepositDetailController {

    private final DepositDetailService depositDetailService;

    @GetMapping("/{id}")
    public DepositDetailResponseDTO getDepositProduct(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        UserVO vo = user.getUser();       // 사용자 ID 조회
        log.info("로그인 사용자: {} / {}", vo.getUserId(), vo.getEmail());
        return depositDetailService.getDepositDetailById(vo.getUserId(),id);
    }

    @GetMapping("/{id}/likes")
    public LikeStatusResponseDTO getLikeStatus(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        if (user == null) {
            // 로그인 안한 경우 좋아요 안누른 상태로 반환 + 현재 좋아요 개수
            int likeCount = depositDetailService.getDepositLikeCount(id);
            return new LikeStatusResponseDTO(false, likeCount);
        }

        long userId = user.getUser().getUserId();
        return depositDetailService.isUserLikedDeposit(userId, id);
    }

    @PostMapping("/{id}/likes")
    public LikeStatusResponseDTO likeDeposit(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return depositDetailService.isUserLikedDeposit(userId, id);
    }

    @DeleteMapping("/{id}/likes")
    public LikeStatusResponseDTO unlikeDeposit(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return depositDetailService.isUserLikedDeposit(userId, id);
    }
}