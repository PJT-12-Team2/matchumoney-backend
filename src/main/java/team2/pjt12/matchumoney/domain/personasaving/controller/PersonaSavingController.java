package team2.pjt12.matchumoney.domain.personasaving.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingResponseDTO;
import team2.pjt12.matchumoney.domain.personasaving.service.PersonaSavingService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import javax.servlet.http.HttpServletRequest;
import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/savings/recommendations")
@RequiredArgsConstructor
@Api(tags = "Persona Saving API",
        description = "페르소나 기반 적금상품 추천 API")
public class PersonaSavingController {
    private final PersonaSavingService personaSavingService;

    @ApiOperation(
            value = "페르소나 적금 추천 조회",
            notes = "personaId에 해당하는 적금 상품 중 무작위 3개를 추천합니다."
    )
    @GetMapping("/by-persona/{personaId}")
    public ResponseEntity<SuccessResponse<PersonaSavingResponseDTO>> getPersonaDepositRecommendations(
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

        PersonaSavingResponseDTO response = personaSavingService.getRecommendedSaving(parsedPersonaId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }

    @ApiOperation(
            value = "사용자의 페르소나 ID 조회",
            notes = "현재 로그인된 사용자의 페르소나 ID를 반환합니다."
    )
    @GetMapping("/user/persona-id")
    public ResponseEntity<Map<String, Object>> getPersonaId(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Long personaId = personaSavingService.getPersonaIdByUserId(userId);

        Map<String, Object> response = new HashMap<>();
        response.put("personaId", personaId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(
            value = "사용자 페르소나 기반 적금 추천",
            notes = "로그인된 사용자의 페르소나에 맞는 적금 상품 3개를 추천합니다."
    )
    @GetMapping("/user/recommendation")
    public ResponseEntity<SuccessResponse<PersonaSavingResponseDTO>> getUserPersonaRecommendation(HttpServletRequest request) {
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        Long personaId = personaSavingService.getPersonaIdByUserId(userId);
        PersonaSavingResponseDTO response = personaSavingService.getRecommendedSaving(personaId);

        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
