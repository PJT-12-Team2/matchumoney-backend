package team2.pjt12.matchumoney.domain.compare.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.compare.dto.*;
import team2.pjt12.matchumoney.domain.compare.mapper.CompareProductMapper;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CompareServiceImpl implements CompareService {

    private final CompareProductMapper compareProductMapper;

    @Override
    public CompareProductsResponseDTO getProducts(String type, List<Long> ids) {
        if (type.equalsIgnoreCase("saving")) {
            List<CompareSavingResponseDTO> products = compareProductMapper.selectSavingProductsByIds(ids);
            List<RateDTO> rates = compareProductMapper.selectRatesBySavingProductIds(ids);

            // 상품 ID → RateDTO 리스트로 매핑
            Map<Long, List<RateDTO>> rateMap = rates.stream()
                    .collect(Collectors.groupingBy(RateDTO::getId)); // 필드 추가 필요

            // rates를 상품에 주입
            for (CompareSavingResponseDTO product : products) {
                product.setRates(rateMap.getOrDefault(product.getId(), new ArrayList<>()));
            }

            return CompareProductsResponseDTO.builder()
                    .savings(products)
                    .build();

        } else if (type.equalsIgnoreCase("deposit")) {
            List<CompareDepositResponseDTO> products = compareProductMapper.selectDepositProductsByIds(ids);
            List<RateDTO> rates = compareProductMapper.selectRatesByDepositProductIds(ids);

            // 상품 ID → RateDTO 리스트로 매핑
            Map<Long, List<RateDTO>> rateMap = rates.stream()
                    .collect(Collectors.groupingBy(RateDTO::getId)); // 필드 추가 필요

            // rates를 상품에 주입
            for (CompareDepositResponseDTO product : products) {
                product.setRates(rateMap.getOrDefault(product.getId(), new ArrayList<>()));
            }

            return CompareProductsResponseDTO.builder()
                    .deposits(products)
                    .build();

        }
//        else if (type.equalsIgnoreCase("card")) {
//            List<CompareCardResponseDTO> cards = compareProductMapper.selectCardProductsByIds(ids);
//            return CompareProductsResponseDTO.builder()
//                    .cards(cards)
//                    .build();
//        }

        throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
    }

    @Override
    public List<SearchProductResponseDTO> getProductsAll(String type) {
        if (type.equalsIgnoreCase("saving")) {
            return compareProductMapper.selectAllSavingProducts();
        } else if (type.equalsIgnoreCase("deposit")) {
            return compareProductMapper.selectAllDepositProducts();
        } else if (type.equalsIgnoreCase("card")) {
            return compareProductMapper.selectAllCardProducts();
        }
        throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
    }
}
