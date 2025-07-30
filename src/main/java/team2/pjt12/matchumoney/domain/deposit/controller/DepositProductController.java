package team2.pjt12.matchumoney.domain.deposit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.service.DepositProductService;

import java.util.List;

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
     * 은행별 예금 상품 조회
     * @param bankName 은행명
     * @return 해당 은행의 예금 상품 목록
     */
    @GetMapping("/products/bank/{bankName}")
    public ResponseEntity<List<DepositProductResponseDTO>> getDepositProductsByBank(
            @PathVariable String bankName) {
        List<DepositProductResponseDTO> products = depositProductService.getDepositProductsByBank(bankName);
        return ResponseEntity.ok(products);
    }

    /**
     * 사용자 보유 금액과 상품 최소 가입 금액을 비교하여 가입 가능한 상품만 조회
     * 실제 가입 가능한 상품들만 보여줌
     *
     * @param userId 조회할 사용자 ID
     * @return 사용자가 가입 가능한 예금 상품 목록 (보유 금액 >= 최소 가입 금액)
     */
//    @GetMapping("/recommendations/history/{userId}")
//    public ResponseEntity<List<DepositProductResponseDTO>> getAffordableProducts(
//            @PathVariable("userId") String userId) {
//
////        log.info("사용자 가입 가능한 상품 조회 요청: userId={}", userId);
//
//        try {
//            // 서비스에서 사용자가 가입 가능한 상품 목록 조회
//            List<DepositProductResponseDTO> products =
//                    depositProductService.getAffordableProducts(userId);
//
////            log.info("사용자 가입 가능한 상품 조회 성공: userId={}, 상품수={}",
////                    userId, products.size());
//
//            // 성공 응답 (200 OK)
//            return ResponseEntity.ok(products);
//
//        } catch (RuntimeException e) {
////            log.error("사용자 가입 가능한 상품 조회 실패: userId={}, error={}",
////                    userId, e.getMessage());
//
//            // 비즈니스 로직 오류 응답 (400 Bad Request)
//            return ResponseEntity.badRequest().build();
//
//        } catch (Exception e) {
////            log.error("사용자 가입 가능한 상품 조회 중 시스템 오류: userId={}", userId, e);
//
//            // 시스템 오류 응답 (500 Internal Server Error)
//            return ResponseEntity.internalServerError().build();
//        }
//    }
}