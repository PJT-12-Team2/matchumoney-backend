package team2.pjt12.matchumoney.domain.savingdetail.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.savingdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.service.SavingDetailService;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

@Slf4j
@RestController
@RequestMapping("/api/saving-products")
@RequiredArgsConstructor
public class SavingDetailController {

    private final SavingDetailService savingDetailService;

    @GetMapping("/{id}")
    public SavingDetailResponseDTO getSavingProduct(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        Long userId = null;
        if (user != null) {
            UserVO vo = user.getUser(); // 사용자 ID 조회
            userId = vo.getUserId();
            log.info("로그인 사용자: {} / {}", vo.getUserId(), vo.getEmail());
        } else {
            log.info("비로그인 사용자 접근");
        }
        return savingDetailService.getSavingDetailById(userId, id);
    }
    @PostMapping("/{id}/likes")
    public LikeStatusResponseDTO likeSaving(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return savingDetailService.isUserLikedSaving(userId, id);
    }

    @DeleteMapping("/{id}/likes")
    public LikeStatusResponseDTO unlikeSaving(@PathVariable Long id, @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return savingDetailService.isUserLikedSaving(userId, id);
    }
}