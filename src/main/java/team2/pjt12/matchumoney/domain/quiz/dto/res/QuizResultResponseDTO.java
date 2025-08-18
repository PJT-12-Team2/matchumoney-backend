package team2.pjt12.matchumoney.domain.quiz.dto.res;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Getter;

@Getter
@Builder
@ApiModel(value = "QuizResultResponse", description = "퀴즈 정답 제출 결과 응답 DTO")
public class QuizResultResponseDTO {

    @ApiModelProperty(value = "사용자 정답 여부", example = "true")
    private Boolean isCorrect;

    @ApiModelProperty(value = "문제의 정답", example = "true")
    private Boolean correctAnswer;

    @ApiModelProperty(value = "문제 해설", example = "기준금리가 상승하면 일반 금리도 함께 오릅니다.")
    private String explanation;

    @ApiModelProperty(value = "획득한 경험치", example = "10")
    private Integer earnedXP;
}