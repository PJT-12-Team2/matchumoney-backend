package team2.pjt12.matchumoney.domain.persona.chatbot.dto;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

@Getter
public class ChatRequest {
    private final String message;

    @JsonCreator
    public ChatRequest(@JsonProperty("message") String message) {
        this.message = message;
    }
}