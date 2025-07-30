package team2.pjt12.matchumoney.domain.personasaving.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonasavingResponseDTO;
import team2.pjt12.matchumoney.domain.personasaving.service.PersonasavingService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

@RestController
@RequestMapping("/api/savings/recommendations")
@RequiredArgsConstructor
@Api(tags = "Persona Saving Recommendations",
        description = "페르소나 기반 적금상품 추천 API")
public class PersonasavingController {

    private final PersonasavingService personasavingService;

    @ApiOperation(
            value = "페르소나 적금 추천 조회",
            notes = "personaId에 해당하는 적금 상품 중 무작위 3개를 추천합니다."
    )
    @GetMapping("/by-persona/{personaId}")
    public ResponseEntity<SuccessResponse<PersonasavingResponseDTO>> getPersonaSavingRecommendations(
            @ApiParam(value = "페르소나 ID", example = "1") @PathVariable Long personaId) {
        PersonasavingResponseDTO response = personasavingService.getRecommendedSaving(personaId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
