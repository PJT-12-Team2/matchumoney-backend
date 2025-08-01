package team2.pjt12.matchumoney.domain.personacard.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.service.PersonaCardService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/cards/recommendations")
@RequiredArgsConstructor
@Api(tags = "Persona Card Recommendations",
        description = "페르소나 기반 카드상품 추천 API")
public class PersonaCardController {
    private final PersonaCardService personaCardService;

    @ApiOperation(
            value = "페르소나 카드 추천 조회",
            notes = "personaId에 해당하는 카드 상품 중 무작위 3개를 추천합니다."
    )
    @GetMapping("/by-persona/{personaId}")
    public ResponseEntity<SuccessResponse<PersonaCardResponseDTO>> getPersonaCardRecommendations(
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

        PersonaCardResponseDTO response = personaCardService.getRecommendedCards(parsedPersonaId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
    @GetMapping("/user/persona-id")
    public ResponseEntity<Map<String, Object>> getPersonaId(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Long personaId = personaCardService.getPersonaIdByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("personaId", personaId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/user/recommendation")
    public ResponseEntity<SuccessResponse<PersonaCardResponseDTO>> getUserPersonaRecommendation(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Long personaId = personaCardService.getPersonaIdByUserId(userId);
        PersonaCardResponseDTO response = personaCardService.getRecommendedCards(personaId);

        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
