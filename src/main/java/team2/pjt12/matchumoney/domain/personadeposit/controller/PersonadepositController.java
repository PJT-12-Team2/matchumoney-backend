package team2.pjt12.matchumoney.domain.personadeposit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.service.PersonadepositService;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.success.SuccessResponse;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

@RestController
@RequestMapping("/api/deposits")
@RequiredArgsConstructor
public class PersonadepositController {

    private final PersonadepositService personadepositService;

    @GetMapping("/recommendations/by-persona")
    public ResponseEntity<SuccessResponse<PersonaDepositResponseDTO>> getPersonaDepositRecommendations() {

        UserVO user = SecurityUtils.getCurrentUser();
        Long userId = user.getUserId();

        PersonaDepositResponseDTO response = personadepositService.getRecommendedDeposit(userId);

        return ResponseEntity.ok(new SuccessResponse<>(response));

    }
}
