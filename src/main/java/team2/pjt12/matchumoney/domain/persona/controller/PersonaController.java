package team2.pjt12.matchumoney.domain.persona.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
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
@Api(tags = "Persona API", description = "페르소나 관련 API")
public class PersonaController {

    private final PersonaService personaService;

    @ApiOperation(
            value = "페르소나 상세 조회 (페르소나 이름)",
            notes = "페르소나 이름을 사용하여 페르소나의 상세 정보를 조회합니다."
    )
    @GetMapping("/{id}")
    public ResponseEntity<PersonaResponseDTO> getPersonaDetail(@ApiParam(value = "페르소나 이름 (영문)", example = "turtle") @PathVariable("id") String personaId) {
        PersonaResponseDTO response = personaService.getPersonaDetail(personaId);
        return ResponseEntity.ok(response);
    }

    @ApiOperation(
            value = "페르소나 상세 조회 (페르소나 ID)",
            notes = "페르소나 ID를 사용하여 페르소나의 상세 정보를 조회합니다."
    )
    @GetMapping("/api/persona/{personaId}")
    public ResponseEntity<PersonaResponseDTO> getPersonaById(@ApiParam(value = "페르소나 ID(정수)", example = "1") @PathVariable Long personaId) {
        PersonaResponseDTO response = personaService.getPersonaDetailById(personaId);
        return ResponseEntity.ok(response);
    }
}
