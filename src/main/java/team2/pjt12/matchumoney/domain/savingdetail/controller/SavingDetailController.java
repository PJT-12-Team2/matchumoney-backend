package team2.pjt12.matchumoney.domain.savingdetail.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.savingdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.service.SavingDetailService;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Slf4j
@RestController
@RequestMapping("/api/saving-products")
@RequiredArgsConstructor
@Api(tags = "Saving Detail API", description = "적금 상품 상세 조회 및 좋아요 관리")
public class SavingDetailController {

    private final SavingDetailService savingDetailService;

    @GetMapping("/{id}")
    @ApiOperation(value = "적금 상품 상세 조회", notes = "상품 ID로 적금 상품의 상세 정보를 조회한다.")
    public SavingDetailResponseDTO getSavingProduct(
            @ApiParam(value = "적금 상품 ID", example = "1", required = true)
            @PathVariable Long id) {

        Long userId = getCurrentUser().getUserId();
        return savingDetailService.getSavingDetailById(userId, id);
    }

    @PostMapping("/{id}/likes")
    @ApiOperation(value = "적금 상품 좋아요 추가", notes = "해당 적금 상품에 좋아요를 설정한다.")

    public LikeStatusResponseDTO likeSaving(
            @ApiParam(value = "적금 상품 ID", example = "1", required = true)
            @PathVariable Long id) {

        Long userId = getCurrentUser().getUserId();
        return savingDetailService.isUserLikedSaving(userId, id);
    }

    @DeleteMapping("/{id}/likes")
    @ApiOperation(value = "적금 상품 좋아요 제거", notes = "해당 적금 상품의 좋아요를 해제한다.")
    public LikeStatusResponseDTO unlikeSaving(
            @ApiParam(value = "적금 상품 ID", example = "1", required = true)
            @PathVariable Long id) {

        Long userId = getCurrentUser().getUserId();
        return savingDetailService.isUserLikedSaving(userId, id);
    }
}
