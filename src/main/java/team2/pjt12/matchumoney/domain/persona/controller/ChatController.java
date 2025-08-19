package team2.pjt12.matchumoney.domain.persona.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.persona.chatbot.dto.ChatRequest;
import team2.pjt12.matchumoney.domain.persona.chatbot.dto.ChatResponse;
import team2.pjt12.matchumoney.domain.persona.chatbot.service.ChatService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/chatbot")
@Api(tags = "Chatbot API", description = "페르소나 챗봇 대화 API")
public class ChatController {

    private final ChatService chatService;

    @ApiOperation(value = "챗봇 대화 요청", notes = "사용자 메시지를 전달하면 챗봇의 응답을 반환합니다.")
    @PostMapping
    public ResponseEntity<ChatResponse> chat(@ApiParam(value = "대화 요청 body (사용자 메시지)", required = true) @RequestBody ChatRequest request) {
        ChatResponse response = chatService.askGPT(request.getMessage());
        return ResponseEntity.ok(response);
    }
}