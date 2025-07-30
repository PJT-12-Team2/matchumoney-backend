package team2.pjt12.matchumoney.domain.personasaving.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.personasaving.dto.SavingProductDTO;
import team2.pjt12.matchumoney.domain.personasaving.service.PersonaSavingService;

import java.util.List;

@RestController
@RequestMapping("/api/persona-saving")
@RequiredArgsConstructor
public class PersonaSavingController {

    private final PersonaSavingService personaSavingService;

    @GetMapping("/recommendation")
    public ResponseEntity<List<SavingProductDTO>> getRecommended(@RequestParam Long personaId) {
        return ResponseEntity.ok(personaSavingService.getRecommendedByPersona(personaId));
    }
}
