package team2.pjt12.matchumoney.domain.depositsearch.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import team2.pjt12.matchumoney.domain.depositsearch.service.DepositSearchService;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.security.UserDetailsImpl;

import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@RestController
@RequestMapping("/api/deposit")
@RequiredArgsConstructor
@Api(tags = "Deposit Search API", description = "예금 필터링 검색 API")
public class DepositSearchController {

    private final DepositSearchService depositSearchService;

    @ApiOperation(
            value = "예금 필터링 검색",
            notes = "은행명/가입한도 등 조건으로 예금 상품을 검색합니다."
    )
    @PostMapping("/search")
    public List<DepositSearchResponseDTO> search(
            @ApiParam(value = "검색 필터", required = true)
            @RequestBody DepositSearchRequestDTO req
    ) {
        Long userId = getCurrentUser().getUserId();
        return depositSearchService.searchDepositProducts(userId, req);
    }
}