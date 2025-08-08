package team2.pjt12.matchumoney.domain.saving.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@AllArgsConstructor
@NoArgsConstructor
@ApiModel(description = "추천 상품 목록 정보 DTO")
public class SavingListItemResponseDTO {
    @ApiModelProperty(value = "적금 상품 번호", example = "1")
    Long id;
    @ApiModelProperty(value = "적금 상품 발행 은행", example = "수협은행")
    String company;
    @ApiModelProperty(value = "적금 상품 제목", example = "Sh적금")
    String title;
    @ApiModelProperty(value = "최대 적용 금리 (연 %)", example = "3.000")
    String maxRate;
    @ApiModelProperty(value = "기본 적용 금리 (연 %)", example = "2.500")
    String baseRate;
    @ApiModelProperty(value = "적금 기간", example = "12")
    String period;
    @ApiModelProperty(value = "한 달 최대 적금 가능 금액", example = "100000")
    String amount;
    @ApiModelProperty(value = "회사 로고 이미지 url", example = "/src/assets/bank-Logos/BK_KB_Profile.png")
    String companyImage;
    @ApiModelProperty(value = "즐겨찾기 여부", example = "false")
    Boolean isStarred;
    @ApiModelProperty(value = "기존보다 높은 금리 만큼", example = "0.24")
    Double betterThan;
    @ApiModelProperty(value = "좋아요 개수", example = "5")
    Long likeCount;
    @ApiModelProperty(value = "내 좋아요 여부", example = "true")
    Boolean isLiked;
}