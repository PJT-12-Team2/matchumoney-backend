package team2.pjt12.matchumoney.domain.personasaving.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingResponseDTO;
import team2.pjt12.matchumoney.domain.personasaving.service.PersonaSavingService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

@RestController
@RequestMapping("/api/savings/recommendations")
@RequiredArgsConstructor
@Api(tags = "Persona Saving Recommendations",
        description = "페르소나 기반 적금상품 추천 API")
public class PersonaSavingController {

    private final PersonaSavingService personaSavingService;

    @ApiOperation(
            value = "페르소나 적금 추천 조회",
            notes = "personaId에 해당하는 적금 상품 중 무작위 3개를 추천합니다."
    )
    @GetMapping("/by-persona/{personaId}")
    public ResponseEntity<SuccessResponse<PersonaSavingResponseDTO>> getPersonaSavingRecommendations(
            @ApiParam(value = "페르소나 ID", example = "1") @PathVariable Long personaId) {
        PersonaSavingResponseDTO response = personaSavingService.getRecommendedSaving(personaId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
