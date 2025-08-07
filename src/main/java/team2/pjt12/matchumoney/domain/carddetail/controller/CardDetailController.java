package team2.pjt12.matchumoney.domain.carddetail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;
import team2.pjt12.matchumoney.domain.carddetail.service.CardDetailService;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;
import org.springframework.security.core.annotation.AuthenticationPrincipal;

@RestController
@RequestMapping("/api/card-products")
@RequiredArgsConstructor
public class CardDetailController {

    private final CardDetailService cardDetailService;

    @GetMapping("/{id}")
    public CardDetailResponseDTO getCardProduct(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl user) {
        UserVO vo = user.getUser();       // 사용자 ID 조회
        return cardDetailService.getCardDetailById(vo.getUserId(),id);
    }

    @PostMapping("/{id}/likes")
    public LikeStatusResponseDTO likeCard(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return cardDetailService.isUserLikedCard(userId, id);
    }

    @DeleteMapping("/{id}/likes")
    public LikeStatusResponseDTO unlikeCard(@PathVariable int id, @AuthenticationPrincipal UserDetailsImpl user) {
        long userId = user.getUser().getUserId();
        return cardDetailService.isUserLikedCard(userId, id);
    }
}