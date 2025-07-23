package team2.pjt12.matchumoney.domain.deposit.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.service.DepositProductService;

import java.util.List;

@RestController
@RequestMapping("/api/deposit")
@CrossOrigin(origins = "*")
@RequiredArgsConstructor
public class DepositProductController {

    private final DepositProductService depositProductService;

    /**
     * 모든 예금 상품 조회
     * @return 예금 상품 목록
     */
    @GetMapping("/products")
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
     * 상품 코드로 예금 상품 조회
     * @param productCode 상품 코드
     * @return 예금 상품 정보
     */
    @GetMapping("/products/{productCode}")
    public ResponseEntity<DepositProductResponseDTO> getDepositProductByCode(
            @PathVariable String productCode) {
        DepositProductResponseDTO product = depositProductService.getDepositProductByCode(productCode);
        if (product != null) {
            return ResponseEntity.ok(product);
        } else {
            return ResponseEntity.notFound().build();
        }
    }
}