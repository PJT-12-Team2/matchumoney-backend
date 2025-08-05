package team2.pjt12.matchumoney.domain.compare.service;

import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.compare.dto.CompareProductsResponseDTO;
import team2.pjt12.matchumoney.domain.compare.dto.SearchProductResponseDTO;

import java.util.List;


@Service
public interface CompareService {
    //비교 상품 조회
    CompareProductsResponseDTO getProducts(String type, List<Long> ids);

    //해당 타입 모든 상품 조회
    List<SearchProductResponseDTO> getProductsAll(String type);

}
