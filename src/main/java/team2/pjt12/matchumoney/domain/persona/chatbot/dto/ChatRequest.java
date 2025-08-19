package team2.pjt12.matchumoney.domain.persona.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Getter;

@Getter
@ApiModel(description = "챗봇 요청 DTO - 사용자 메시지와 시스템 프롬프트를 포함")

public class ChatRequest {

    @ApiModelProperty(
            value = "사용자가 입력한 메시지",
            example = "예금이란 무엇인가요?",
            required = true
    )
    private final String message;

    @ApiModelProperty(
            value = "시스템 프롬프트 (선택 입력, 대화 톤/역할 설정용)",
            example = "서비스에 관련된 대화만이 가능합니다...",
            required = false
    )
    private final String systemPrompt; // 🔧 이 필드를 추가

    @JsonCreator
    public ChatRequest(
            @JsonProperty("message") String message,
            @JsonProperty("systemPrompt") String systemPrompt // 🔧 이 부분도 추가
    ) {
        this.message = message;
        this.systemPrompt = systemPrompt;
    }
}