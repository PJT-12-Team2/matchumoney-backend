package team2.pjt12.matchumoney.domain.persona.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ChatRequest {
    private final String message;
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