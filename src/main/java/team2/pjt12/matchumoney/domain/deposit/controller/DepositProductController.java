package team2.pjt12.matchumoney.domain.deposit.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.service.DepositProductService;
import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/deposits")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DepositProductController {

    private final DepositProductService depositProductService;

    /**
     * 모든 예금 상품 조회 (프론트엔드에서 호출하는 엔드포인트)
     * @return 예금 상품 목록 (minAmount 포함)
     */
    @GetMapping("/recommendations/allProducts")
    public ResponseEntity<List<DepositProductResponseDTO>> getAllDepositProducts() {
        List<DepositProductResponseDTO> products = depositProductService.getAllDepositProducts();
        return ResponseEntity.ok(products);
    }

    /**
     * KB국민은행 예금 상품만 조회 (계좌가 없는 사용자용)
     * @return KB국민은행 예금 상품 목록
     */
    @GetMapping("/recommendations/kb-products")
    public ResponseEntity<List<DepositProductResponseDTO>> getKBDepositProducts() {
        List<DepositProductResponseDTO> products = depositProductService.getDepositProductsByBank("국민은행");
        return ResponseEntity.ok(products);
    }

    @PostMapping("/recommendations/byBalance")
    public ResponseEntity<List<DepositProductResponseDTO>> getProductsByBalance(@RequestBody BalanceRequestDTO request) {
        log.info("잔액 기반 상품 추천 API 호출: userId={}, balance={}, accountNumber={}",
                request.getUserId(), request.getBalance(), request.getAccountNumber());

        try {
            // 🔍 요청 데이터 상세 로깅
            log.debug("요청 DTO 전체: {}", request);

            List<DepositProductResponseDTO> products = depositProductService.getProductsByBalance(request);
            log.info("추천 상품 {}개 조회 완료", products.size());
            return ResponseEntity.ok(products);
        } catch (IllegalArgumentException e) {
            log.error("잘못된 요청 파라미터: {}", e.getMessage());
            return ResponseEntity.badRequest().build();
        } catch (Exception e) {
            log.error("잔액 기반 상품 추천 API 오류: userId={}, balance={}, 상세오류:",
                    request.getUserId(), request.getBalance(), e);
            return ResponseEntity.internalServerError().build();
        }
    }
}