package team2.pjt12.matchumoney.domain.persona.chatbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.persona.chatbot.dto.ChatResponse;
import team2.pjt12.matchumoney.domain.persona.chatbot.util.OpenAIClient;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenAIClient openAIClient;

    public ChatResponse askGPT(String message) {
        String reply = openAIClient.callChatGPT(message);
        return new ChatResponse(reply);
    }
}