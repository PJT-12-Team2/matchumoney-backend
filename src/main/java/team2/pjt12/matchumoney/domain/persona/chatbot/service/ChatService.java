package team2.pjt12.matchumoney.domain.persona.chatbot.service;


import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.persona.chatbot.dto.ChatResponse;
import team2.pjt12.matchumoney.domain.persona.chatbot.util.OpenAIClient;

@Service
public class ChatService {

    private final OpenAIClient openAIClient;

    public ChatService(OpenAIClient openAIClient) {
        this.openAIClient = openAIClient;
    }

    public ChatResponse askGPT(String message) {
        String reply = openAIClient.callChatGPT(message);
        return new ChatResponse(reply);
    }
}

