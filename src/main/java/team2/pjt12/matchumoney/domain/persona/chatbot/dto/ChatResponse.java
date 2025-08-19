package team2.pjt12.matchumoney.domain.persona.chatbot.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
@ApiModel(description = "챗봇 응답 DTO - GPT가 반환하는 답변 메시지")
public class ChatResponse {

    @ApiModelProperty(
            value = "GPT가 반환한 답변",
            example = "안녕하세요! 저는 재무 상담가 챗봇입니다. 어떤 상품을 찾고 계신가요?"
    )
    private final String reply;
}