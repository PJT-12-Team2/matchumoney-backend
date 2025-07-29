package team2.pjt12.matchumoney.domain.mydata.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;
import lombok.Setter;
import org.springframework.format.annotation.DateTimeFormat;

import java.time.LocalDate;

@Getter
@Setter
@ApiModel(description = "카드 거래 내역 입력 DTO")
public class KbCardTransactionRequestDTO {
    @ApiModelProperty(value = "로그인 한 사용자 고유 ID", example = "1", position = 1)
    private Long userId;

    @ApiModelProperty(value = "선택된 카드의 holdingId (CardInfoVO의 PK)", example = "1", position = 2)
    private Long holdingId;

    @ApiModelProperty(value = " 사용자 입력 카드 번호", example = "1234567890111213", position = 3)
    private String cardNo;

    @ApiModelProperty(value = "사용자 입력 카드 비밀번호 앞 2자리", example = "12", position = 4)
    private String cardPw2;

    @ApiModelProperty(value = "사용자 생년월일 (YYYYMMDD)", example = "YYYYMMDD", position = 5)
    private String birthDate;

    @ApiModelProperty(value = "조회 시작일", example = "yyyyMMdd", position = 6)
    @DateTimeFormat(pattern = "yyyyMMdd")
    private LocalDate startDate;

    @ApiModelProperty(value = "조회 종료일", example = "yyyyMMdd", position = 7)
    @DateTimeFormat(pattern = "yyyyMMdd")
    private LocalDate endDate;

    @ApiModelProperty(value = "카드 고유 ID", example = "1", position = 8)
    private Long cardId;
}