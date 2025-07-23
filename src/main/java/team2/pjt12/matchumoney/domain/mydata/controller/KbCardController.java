package team2.pjt12.matchumoney.domain.mydata.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.mydata.dto.KbCardApiRequestDTO;
import team2.pjt12.matchumoney.domain.mydata.service.KbCardService;
import team2.pjt12.matchumoney.domain.mydata.vo.CardInfoVO;

import java.util.List;

@RestController
@RequestMapping("/api/users/me")
@RequiredArgsConstructor
public class KbCardController {

    private final KbCardService kbCardService;

    @PostMapping("/cards")
    public ResponseEntity<List<CardInfoVO>> syncKbCardByBody(@RequestBody KbCardApiRequestDTO req) throws Exception {
        List<CardInfoVO> result = kbCardService.syncAndSaveCards(req.getUserId(), req.getCardId(), req.getCardPw());
        return ResponseEntity.ok(result);
    }

    @GetMapping("/{userId}")
    public ResponseEntity<List<CardInfoVO>> getMyCards(@PathVariable Long userId) {
        List<CardInfoVO> cards = kbCardService.getCards(userId);
        return ResponseEntity.ok(cards);
    }
}
