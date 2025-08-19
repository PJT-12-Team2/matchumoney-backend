package team2.pjt12.matchumoney.domain.carddetail.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@RestController
@RequestMapping("/api/card-products")
@RequiredArgsConstructor
@Api(tags = "Card Detail API", description = "카드 상세 조회 및 좋아요 API")
public class CardDetailController {

    private final CardDetailService cardDetailService;

    @ApiOperation(
            value = "카드 상세 조회",
            notes = "카드 ID로 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public CardDetailResponseDTO getCardProduct(@ApiParam(value = "카드 상품 ID", example = "10", required = true)
                                                @PathVariable int id) {
        Long userId = getCurrentUser().getUserId();
        return cardDetailService.getCardDetailById(userId, id);
    }

    @ApiOperation(
            value = "카드 좋아요 등록",
            notes = "해당 카드에 좋아요를 표시합니다."
    )
    @PostMapping("/{id}/likes")
    public LikeStatusResponseDTO likeCard(@ApiParam(value = "카드 상품 ID", example = "10", required = true)
                                          @PathVariable int id) {
        Long userId = getCurrentUser().getUserId();
        return cardDetailService.isUserLikedCard(userId, id);
    }

    @ApiOperation(
            value = "카드 좋아요 해제",
            notes = "해당 카드의 좋아요를 해제합니다."
    )
    @DeleteMapping("/{id}/likes")
    public LikeStatusResponseDTO unlikeCard(@ApiParam(value = "카드 상품 ID", example = "10", required = true)
                                            @PathVariable int id) {
        Long userId = getCurrentUser().getUserId();
        return cardDetailService.isUserLikedCard(userId, id);
    }
}
