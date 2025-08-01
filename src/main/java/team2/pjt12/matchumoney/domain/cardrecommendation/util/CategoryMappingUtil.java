package team2.pjt12.matchumoney.domain.cardrecommendation.util;

import java.util.HashMap;
import java.util.Map;

/**
 * 카드 혜택의 카테고리와 거래내역의 카테고리를 매핑하는 유틸리티
 */
public class CategoryMappingUtil {
    
    // 카드 혜택 카테고리 -> 거래내역 카테고리 매핑
    private static final Map<String, String> BENEFIT_TO_TRANSACTION_CATEGORY = new HashMap<>();
    
    static {
        // 기본 매핑 (카드고릴라 카테고리 -> MerchantCategoryService 카테고리)
        BENEFIT_TO_TRANSACTION_CATEGORY.put("교통", "교통");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("카페/베이커리", "카페");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("카페", "카페");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("베이커리", "카페");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("외식", "음식점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("음식점", "음식점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("배달앱", "배달앱");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("온라인쇼핑", "온라인쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("온라인서비스", "온라인서비스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("통신", "통신");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("주유", "교통");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("마트/백화점", "마트");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("마트", "마트");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("백화점", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("문화/여가", "문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("문화", "문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("영화관", "영화관");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("의료/건강", "의료");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("의료", "의료");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("해외/여행", "숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("여행", "숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("숙박", "숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("교육", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("편의점", "편의점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("구독서비스", "온라인서비스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("생활비/공과금", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("공과금", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("보험", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("금융", "금융");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("패션", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("화장품", "화장품");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("디저트", "디저트");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("주점", "주점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("기타", "기타");
    }
    
    /**
     * 카드 혜택 카테고리를 거래내역 카테고리로 매핑합니다.
     * @param benefitCategory 카드 혜택 카테고리
     * @return 매핑된 거래내역 카테고리
     */
    public static String mapBenefitToTransactionCategory(String benefitCategory) {
        if (benefitCategory == null || benefitCategory.trim().isEmpty()) {
            return "기타";
        }
        
        return BENEFIT_TO_TRANSACTION_CATEGORY.getOrDefault(benefitCategory.trim(), "기타");
    }
    
    /**
     * 두 카테고리가 매치되는지 확인합니다.
     * @param benefitCategory 카드 혜택 카테고리
     * @param transactionCategory 거래내역 카테고리
     * @return 매치 여부
     */
    public static boolean isCategoryMatch(String benefitCategory, String transactionCategory) {
        if (benefitCategory == null || transactionCategory == null) {
            return false;
        }
        
        String mappedCategory = mapBenefitToTransactionCategory(benefitCategory);
        return mappedCategory.equals(transactionCategory.trim());
    }
}