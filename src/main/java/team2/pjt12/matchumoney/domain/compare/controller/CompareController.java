package team2.pjt12.matchumoney.domain.compare.controller;


import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.compare.dto.CompareProductsResponseDTO;
import team2.pjt12.matchumoney.domain.compare.dto.SearchProductResponseDTO;
import team2.pjt12.matchumoney.domain.compare.service.CompareService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CompareController implements CompareApi {

    private final CompareService compareService;

    @Override
    public ResponseEntity<CompareProductsResponseDTO> getProductInfoList(@ApiParam(value = "상품 타입", allowableValues = "SAVING, DEPOSIT, CARD", required = true)
                                                                         @PathVariable("type") String type, @RequestParam(required = false) List<Long> ids) {
        if (ids == null || ids.isEmpty()) {
            return ResponseEntity.ok(null);
        }

        return ResponseEntity.ok(compareService.getProducts(type, ids));
    }

    @Override
    public ResponseEntity<List<SearchProductResponseDTO>> getProductList(@ApiParam(value = "상품 타입", allowableValues = "SAVING, DEPOSIT, CARD", required = true) @PathVariable("type") String type) {
        return ResponseEntity.ok(compareService.getProductsAll(type));
    }
}
