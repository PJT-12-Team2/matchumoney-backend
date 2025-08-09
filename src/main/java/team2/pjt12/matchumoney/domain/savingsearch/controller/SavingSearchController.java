package team2.pjt12.matchumoney.domain.savingsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.service.SavingSearchService;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

import java.util.List;

@RestController
@RequestMapping("/api/saving")
@RequiredArgsConstructor
public class SavingSearchController {

    private final SavingSearchService savingSearchService;

    @PostMapping("/search")
    public List<SavingSearchResponseDTO> search(
            @RequestBody SavingSearchRequestDTO req,
            @AuthenticationPrincipal UserDetailsImpl user) {
        UserVO vo = user.getUser(); // 사용자 ID 조회
        Long userId = user != null ? vo.getUserId() : null; // 비로그인 허용 시 null
        return savingSearchService.searchSavingProducts(userId, req);
    }
}