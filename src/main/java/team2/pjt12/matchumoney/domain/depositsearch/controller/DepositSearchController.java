package team2.pjt12.matchumoney.domain.depositsearch.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import team2.pjt12.matchumoney.domain.depositsearch.service.DepositSearchService;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

import java.util.List;

@RestController
@RequestMapping("/api/deposit")
@RequiredArgsConstructor
public class DepositSearchController {

    private final DepositSearchService depositSearchService;

    @PostMapping("/search")
    public List<DepositSearchResponseDTO> search(
            @RequestBody DepositSearchRequestDTO req,
            @AuthenticationPrincipal UserDetailsImpl user) {
        UserVO vo = user.getUser(); // 사용자 ID 조회
        Long userId = user != null ? vo.getUserId() : null; // 비로그인 허용 시 null
        return depositSearchService.searchDepositProducts(userId, req);
    }
}