package team2.pjt12.matchumoney.domain.carddetail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;
import team2.pjt12.matchumoney.domain.carddetail.service.CardDetailService;

@RestController
@RequestMapping("/api/card-products")
@RequiredArgsConstructor
public class CardDetailController {

    private final CardDetailService cardDetailService;

    @GetMapping("/{id}")
    public ResponseEntity<CardDetailResponseDTO> getCardDetail(@PathVariable int id) {
        return ResponseEntity.ok(cardDetailService.getCardDetailById(id));
    }
}