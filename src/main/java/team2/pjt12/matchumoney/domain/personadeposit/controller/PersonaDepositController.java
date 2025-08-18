package team2.pjt12.matchumoney.domain.personadeposit.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.service.PersonaDepositService;
import team2.pjt12.matchumoney.global.jwt.JwtService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/deposits/recommendations")
@RequiredArgsConstructor
@Api(tags = "Persona Deposit API",
        description = "페르소나 기반 예금상품 추천 API")
public class PersonaDepositController {
    private final JwtService jwtService;
    private final PersonaDepositService personadepositService;

    @ApiOperation(
            value = "페르소나 예금 추천 조회",
            notes = "personaId에 해당하는 예금 상품 중 무작위 3개를 추천합니다."
    )
    @GetMapping("/by-persona/{personaId}")
    public ResponseEntity<SuccessResponse<PersonaDepositResponseDTO>> getPersonaDepositRecommendations(@ApiParam(value = "페르소나 ID", example = "1") @PathVariable Long personaId) {
        PersonaDepositResponseDTO response = personadepositService.getRecommendedDeposit(personaId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/user/persona-id")
    public ResponseEntity<Map<String, Object>> getPersonaId(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromToken(request)
                .orElseThrow(() -> new RuntimeException("userId 추출 실패"));

        Long personaId = personadepositService.getPersonaIdByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("personaId", personaId);
        return ResponseEntity.ok(response);
    }

    @GetMapping("/user/recommendation")
    public ResponseEntity<SuccessResponse<PersonaDepositResponseDTO>> getUserPersonaRecommendation(HttpServletRequest request) {
        Long userId = jwtService.getUserIdFromToken(request)
                .orElseThrow(() -> new RuntimeException("userId 추출 실패"));

        Long personaId = personadepositService.getPersonaIdByUserId(userId);
        PersonaDepositResponseDTO response = personadepositService.getRecommendedDeposit(personaId);

        return ResponseEntity.ok(new SuccessResponse<>(response));

    }
}
