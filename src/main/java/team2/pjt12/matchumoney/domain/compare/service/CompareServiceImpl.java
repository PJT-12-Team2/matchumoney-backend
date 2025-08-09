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

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class CompareServiceImpl implements CompareService {

    private final CompareProductMapper compareProductMapper;


    @Override
    public CompareProductsResponseDTO getProducts(String type, List<Long> ids) {
        Long userId = getCurrentUser().getUserId();
        
        if (type.equalsIgnoreCase("saving")) {
            List<CompareSavingResponseDTO> products = compareProductMapper.selectSavingProductsByIds(ids, userId);
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
            List<CompareDepositResponseDTO> products = compareProductMapper.selectDepositProductsByIds(ids, userId);
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

        } else if (type.equalsIgnoreCase("card")) {
            // 1. 카드 정보 조회
            List<CompareCardResponseDTO> cards = compareProductMapper.selectCardProductsByIds(ids, userId);

            // 2. 카드 혜택(옵션) 조회
            List<CardOptionDTO> cardOptions = compareProductMapper.selectCardOptionsByCardIds(ids);

            // 3. 카드 ID로 그룹핑
            Map<Long, List<CardOptionDTO>> optionMap = cardOptions.stream()
                    .collect(Collectors.groupingBy(CardOptionDTO::getCardId2));

            // 4. 각 카드에 혜택 세팅
            for (CompareCardResponseDTO card : cards) {
                List<CardOptionDTO> benefits = optionMap.getOrDefault(card.getId(), new ArrayList<>());
                card.setBenefits(benefits);  // DTO에 benefits 필드 필요
            }
            return CompareProductsResponseDTO.builder()
                    .cards(cards)
                    .build();
        }
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
