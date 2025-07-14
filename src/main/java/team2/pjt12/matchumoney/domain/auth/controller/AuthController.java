package team2.pjt12.matchumoney.domain.auth.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.auth.dto.LoginResDto;
import team2.pjt12.matchumoney.domain.auth.dto.SocialLoginReqDto;
import team2.pjt12.matchumoney.domain.auth.service.AuthService;

@RestController
@RequiredArgsConstructor
@RequestMapping("/oauth")
public class AuthController {

    static {
        System.out.println("🚨 AuthController Loaded 🚨");
    }
    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<LoginResDto> login(@RequestBody SocialLoginReqDto request) {
        System.out.println("🚀 Processing login request 🚀");
        System.out.println("Received login request with code: " + request.getCode());
        LoginResDto response = authService.loginOrSignUp(request);
        return ResponseEntity.ok(response);
    }
}