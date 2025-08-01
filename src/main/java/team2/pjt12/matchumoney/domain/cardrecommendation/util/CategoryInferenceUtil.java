package team2.pjt12.matchumoney.domain.cardrecommendation.util;

import team2.pjt12.matchumoney.domain.mydata.service.MerchantCategoryService;
import org.springframework.stereotype.Component;
import lombok.RequiredArgsConstructor;

@Component
@RequiredArgsConstructor
public class CategoryInferenceUtil {
    
    private final MerchantCategoryService merchantCategoryService;
    
    /**
     * 가맹점 이름을 기반으로 카테고리를 추론합니다.
     * @param merchantName 가맹점 이름
     * @return 추론된 카테고리 (기본값: "기타")
     */
    public String inferCategory(String merchantName) {
        return merchantCategoryService.categorizeByMerchantName(merchantName);
    }
    
    /**
     * 온라인 거래 여부를 판단합니다.
     * @param merchantName 가맹점 이름
     * @return 온라인 거래 여부
     */
    public boolean isOnlineTransaction(String merchantName) {
        if (merchantName == null || merchantName.trim().isEmpty()) {
            return false;
        }
        
        String category = merchantCategoryService.categorizeByMerchantName(merchantName);
        return "온라인쇼핑".equals(category) || "온라인서비스".equals(category);
    }
    
    /**
     * 카테고리별 온라인 거래 여부를 판단합니다.
     * @param category 카테고리명
     * @return 온라인 거래 여부
     */
    public boolean isOnlineCategory(String category) {
        return "온라인쇼핑".equals(category) || "온라인서비스".equals(category);
    }
}