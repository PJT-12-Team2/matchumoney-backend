package team2.pjt12.matchumoney.domain.personadeposit.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.service.PersonaDepositService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;
import team2.pjt12.matchumoney.global.util.SecurityUtils;
import team2.pjt12.matchumoney.global.jwt.JwtService;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/deposits/recommendations")
@RequiredArgsConstructor
@Api(tags = "Persona Deposit Recommendations",
        description = "페르소나 기반 예금상품 추천 API")
public class PersonaDepositController {
    private final JwtService jwtService;
    private final PersonaDepositService personaDepositService;

    @ApiOperation(
            value = "페르소나 예금 추천 조회",
            notes = "personaId에 해당하는 예금 상품 중 무작위 3개를 추천합니다."
    )
    @GetMapping("/by-persona/{personaId}")
    public ResponseEntity<SuccessResponse<PersonaDepositResponseDTO>> getPersonaDepositRecommendations(
            @ApiParam(value = "페르소나 ID", example = "1") @PathVariable String personaId) {

        if (personaId == null || personaId.equalsIgnoreCase("null")) {
            throw new IllegalArgumentException("personaId는 null이 될 수 없습니다.");
        }

        Long parsedPersonaId;
        try {
            parsedPersonaId = Long.parseLong(personaId);
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("유효한 숫자 형태의 personaId가 필요합니다.");
        }

        PersonaDepositResponseDTO response = personaDepositService.getRecommendedDeposit(parsedPersonaId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @GetMapping("/user/persona-id")
    public ResponseEntity<Map<String, Object>> getPersonaId() {
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Long personaId = personaDepositService.getPersonaIdByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("personaId", personaId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/user/recommendation")
    public ResponseEntity<SuccessResponse<PersonaDepositResponseDTO>> getUserPersonaRecommendation(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Long personaId = personaDepositService.getPersonaIdByUserId(userId);
        PersonaDepositResponseDTO response = personaDepositService.getRecommendedDeposit(personaId);

        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
