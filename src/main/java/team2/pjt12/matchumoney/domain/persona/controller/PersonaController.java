package team2.pjt12.matchumoney.domain.persona.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.persona.dto.PersonaResponseDTO;
import team2.pjt12.matchumoney.domain.persona.service.PersonaService;

@RestController
@RequestMapping("/api/persona")
@RequiredArgsConstructor
public class PersonaController {

    private final PersonaService personaService;

    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponseDTO> getPersonaDetail(@PathVariable("id") String personaId) {
        PersonaResponseDTO response = personaService.getPersonaDetail(personaId);
        return ResponseEntity.ok(response);
    }
    @GetMapping("/api/persona/{personaId}")
    public ResponseEntity<PersonaResponseDTO> getPersonaById(@PathVariable Long personaId) {
        PersonaResponseDTO response = personaService.getPersonaDetailById(personaId);
        return ResponseEntity.ok(response);
    }
    }
