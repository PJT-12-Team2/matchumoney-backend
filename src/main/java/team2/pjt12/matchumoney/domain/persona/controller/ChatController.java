package team2.pjt12.matchumoney.domain.persona.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.persona.chatbot.dto.ChatRequest;
import team2.pjt12.matchumoney.domain.persona.chatbot.dto.ChatResponse;
import team2.pjt12.matchumoney.domain.persona.chatbot.service.ChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
public class ChatController {

    private final ChatService chatService;

    @PostMapping
    public ResponseEntity<ChatResponse> chat(@RequestBody ChatRequest request) {
        ChatResponse response = chatService.askGPT(request.getMessage());
        return ResponseEntity.ok(response);
    }
}