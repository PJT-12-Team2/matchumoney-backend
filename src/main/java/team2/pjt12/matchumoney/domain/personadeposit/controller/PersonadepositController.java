package team2.pjt12.matchumoney.domain.personadeposit.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.service.PersonadepositService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

@RestController
@RequestMapping("/api/deposits/recommendations")
@RequiredArgsConstructor
@Api(tags = "Persona Deposit Recommendations",
        description = "페르소나 기반 예금상품 추천 API")
public class PersonadepositController {

    private final PersonadepositService personadepositService;

    @ApiOperation(
            value = "페르소나 예금 추천 조회",
            notes = "personaId에 해당하는 예금 상품 중 무작위 3개를 추천합니다."
    )
    @GetMapping("/by-persona/{personaId}")
    public ResponseEntity<SuccessResponse<PersonaDepositResponseDTO>> getPersonaDepositRecommendations(@ApiParam(value = "페르소나 ID", example = "1") @PathVariable Long personaId) {
        PersonaDepositResponseDTO response = personadepositService.getRecommendedDeposit(personaId);
        return ResponseEntity.ok(new SuccessResponse<>(response));
    }
}
