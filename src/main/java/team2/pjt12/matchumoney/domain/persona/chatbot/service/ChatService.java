package team2.pjt12.matchumoney.domain.persona.chatbot.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.persona.chatbot.dto.ChatResponse;
import team2.pjt12.matchumoney.domain.persona.chatbot.util.OpenAIClient;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class ChatService {

    private final OpenAIClient openAIClient;

    // 사용자 메시지에 따른 프롬프트 매핑
    private static final Map<String, String> promptMap = Map.of(
            "서비스 이용방법", "이 플랫폼은 사용자의 금융 생활을 돕기 위해 다양한 기능을 제공합니다...",
            "카드 추천", "사용자의 소비 성향에 따라 적절한 카드를 추천해 드릴게요!",
            "예금 추천", "사용자의 금융 목표에 맞는 예금 상품을 안내해 드릴게요!"
            // 원하는 만큼 추가 가능
    );

    // 공통 시스템 프롬프트
    private static final String systemPrompt = """
        당신은 친절하고 전문적인 금융 상담사 챗봇입니다.
        사용자의 질문에 맞춤형 설명을 제공하고, 금융상품 비교/추천과 페르소나 안내를 도와주세요.
    """;

    public ChatResponse askGPT(String userMessage) {
        // 사용자 메시지에 따라 프롬프트 자동 매핑
        String prompt = promptMap.getOrDefault(userMessage, userMessage); // 없는 건 그냥 질문 그대로 사용

        // GPT 호출
        String reply = openAIClient.callChatGPT(prompt, systemPrompt);

        return new ChatResponse(reply);
    }
}