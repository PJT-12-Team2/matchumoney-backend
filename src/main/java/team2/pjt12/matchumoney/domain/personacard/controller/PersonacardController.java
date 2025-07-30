package team2.pjt12.matchumoney.domain.personacard.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardRecommendationResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.service.PersonacardService;

@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
public class PersonacardController {

    private final PersonacardService personacardService;

    @GetMapping("/{personaId}/recommendations")
    public ResponseEntity<PersonaCardRecommendationResponseDTO> getRecommendations(
            @PathVariable Long personaId) {

        PersonaCardRecommendationResponseDTO response = personacardService.getRecommendedCards(personaId);
        return ResponseEntity.ok(response);
    }
}
