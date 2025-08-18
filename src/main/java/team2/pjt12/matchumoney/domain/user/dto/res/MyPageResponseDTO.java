package team2.pjt12.matchumoney.domain.user.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.persona.dto.PersonaSimpleResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.List;

@Getter
@ApiModel(value = "MyPageResponse", description = "마이페이지 사용자 정보 및 관심 상품(즐겨찾기) 목록 응답 DTO")
public final class MyPageResponseDTO {

    @ApiModelProperty(value = "사용자 닉네임", example = "홍길동")
    private final String nickname;

    @ApiModelProperty(value = "페르소나 정보")
    private final PersonaSimpleResponseDTO persona;

    @ApiModelProperty(value = "누적 EXP", example = "40")
    private final Integer exp;

    @ApiModelProperty(value = "이메일", example = "test@example.com")
    private final String email;

    @ApiModelProperty(value = "프로필 이미지 URL", example = "https://example.com/profile.jpg")
    private final String profileImageUrl;

    @ApiModelProperty(value = "소셜 로그인 여부", example = "true")
    private final Boolean socialLogin;      // maps is_social_login


    private final Long favoriteId;

    @ApiModelProperty(value = "성별", example = "MALE")
    private final String gender;            // "MALE" | "FEMALE"

    @ApiModelProperty(value = "생년월일", example = "1990-01-01")
    private final LocalDate birthDate;

    @ApiModelProperty(value = "가입 일시", example = "2024-01-01T10:00:00")
    private final LocalDateTime createdTime;

    @ApiModelProperty(value = "최종 수정 일시", example = "2024-08-01T14:22:00")
    private final LocalDateTime lastModifiedTime;

    @ApiModelProperty(value = "관심 예금 상품 목록")
    private final List<DepositProductResponseDTO> favoriteDeposits;

    @ApiModelProperty(value = "관심 적금 상품 목록")
    private final List<SavingListItemResponseDTO> favoriteSavings;

    @ApiModelProperty(value = "관심 카드 목록")
    private final List<CardSearchResponseDTO> favoriteCards;

    public MyPageResponseDTO(

            String nickname,


            PersonaSimpleResponseDTO persona,


            Integer exp,


            String email,


            String profileImageUrl,


            Boolean socialLogin,
            Long favoriteId,
            String gender,
            LocalDate birthDate,
            LocalDateTime createdTime,
            LocalDateTime lastModifiedTime,
            List<DepositProductResponseDTO> favoriteDeposits,
            List<SavingListItemResponseDTO> favoriteSavings,
            List<CardSearchResponseDTO> favoriteCards
    ) {
        this.nickname = nickname;
        this.persona = persona;
        this.exp = exp;
        this.email = email;
        this.profileImageUrl = profileImageUrl;
        this.socialLogin = socialLogin;
        this.favoriteId = favoriteId;
        this.gender = gender;
        this.birthDate = birthDate;
        this.createdTime = createdTime;
        this.lastModifiedTime = lastModifiedTime;
        this.favoriteDeposits = favoriteDeposits;
        this.favoriteSavings = favoriteSavings;
        this.favoriteCards = favoriteCards;
    }
}
