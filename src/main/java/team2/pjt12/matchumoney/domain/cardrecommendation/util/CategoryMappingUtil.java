package team2.pjt12.matchumoney.domain.cardrecommendation.util;

import java.util.HashMap;
import java.util.Map;
import java.util.LinkedHashSet;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 카드 혜택의 카테고리와 거래내역의 카테고리를 매핑하는 유틸리티
 * 새로운 23개 표준 카테고리 체계로 업데이트됨
 */
public class CategoryMappingUtil {
    
    // 카드 혜택 카테고리 -> 거래내역 카테고리 매핑 (23개 표준 카테고리)
    private static final Map<String, String> BENEFIT_TO_TRANSACTION_CATEGORY = new HashMap<>();
    
    static {
        // 교통 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("교통", "교통");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("대중교통", "교통");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("고속버스", "교통");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("기차", "교통");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("택시", "교통");
        
        // 주유 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("주유", "주유");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("주유소", "주유");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("충전소", "주유");
        
        // 통신 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("통신", "통신");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("SKT", "통신");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("KT", "통신");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("LGU+", "통신");
        
        // 마트/편의점 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("마트/편의점", "마트/편의점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("마트", "마트/편의점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("편의점", "마트/편의점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("대형마트", "마트/편의점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("SSM", "마트/편의점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("전통시장", "마트/편의점");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("생활", "마트/편의점");
        
        // 쇼핑 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("쇼핑", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("백화점", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("면세점", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("아울렛", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("온라인쇼핑", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("홈쇼핑", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("소셜커머스", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("인테리어", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("SPA브랜드", "쇼핑");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("패션", "쇼핑");
        
        // 푸드 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("푸드", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("일반음식점", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("음식점", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("외식", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("패밀리레스토랑", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("패스트푸드", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("점심", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("저녁", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("배달앱", "푸드");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("주점", "푸드");
        
        // 카페/디저트 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("카페/디저트", "카페/디저트");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("카페", "카페/디저트");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("베이커리", "카페/디저트");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("아이스크림", "카페/디저트");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("디저트", "카페/디저트");
        
        // 뷰티/피트니스 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("뷰티/피트니스", "뷰티/피트니스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("드럭스토어", "뷰티/피트니스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("피트니스", "뷰티/피트니스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("헤어", "뷰티/피트니스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("화장품", "뷰티/피트니스");
        
        // 무실적 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("무실적", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("할인", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("적립", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("캐시백", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("선택형", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("무이자할부", "무실적");
        
        // 공과금/렌탈 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("공과금/렌탈", "공과금/렌탈");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("공과금", "공과금/렌탈");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("렌탈", "공과금/렌탈");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("생활비/공과금", "공과금/렌탈");
        
        // 병원/약국 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("병원/약국", "병원/약국");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("병원", "병원/약국");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("약국", "병원/약국");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("의료/건강", "병원/약국");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("의료", "병원/약국");
        
        // 애완동물 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("애완동물", "애완동물");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("동물병원", "애완동물");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("펫샵", "애완동물");
        
        // 교육/육아 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("교육/육아", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("교육", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("학원", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("문화센터", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("국민행복", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("아이행복", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("어린이집", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("유치원", "교육/육아");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("학습지", "교육/육아");
        
        // 자동차/하이패스 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("자동차/하이패스", "자동차/하이패스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("자동차", "자동차/하이패스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("정비", "자동차/하이패스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("차/중고차", "자동차/하이패스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("하이패스", "자동차/하이패스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("렌터카", "자동차/하이패스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("보험", "자동차/하이패스");
        
        // 레저/스포츠 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("레저/스포츠", "레저/스포츠");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("경기관람", "레저/스포츠");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("골프", "레저/스포츠");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("테마파크", "레저/스포츠");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("게임", "레저/스포츠");
        
        // OTT/영화/문화 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("OTT/영화/문화", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("영화", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("영화관", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("공연/전시", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("도서", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("음원사이트", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("디지털구독", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("문화/여가", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("문화", "OTT/영화/문화");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("구독서비스", "OTT/영화/문화");
        
        // 간편결제 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("간편결제", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("카카오페이", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("삼성페이", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("네이버페이", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("APP", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("멤버십포인트", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("해피포인트", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("OK캐쉬백", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("CJ ONE", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("BC TOP", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("금융", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("은행사", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("카드사", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("보험사", "간편결제");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("증권사", "간편결제");
        
        // 항공마일리지 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("항공마일리지", "항공마일리지");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("대한항공", "항공마일리지");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("아시아나항공", "항공마일리지");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("제주항공", "항공마일리지");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("진에어", "항공마일리지");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("저가항공", "항공마일리지");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("항공권", "항공마일리지");
        
        // 공항라운지/PP 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("공항라운지/PP", "공항라운지/PP");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("공항라운지", "공항라운지/PP");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("PP", "공항라운지/PP");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("라운지키", "공항라운지/PP");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("공항", "공항라운지/PP");
        
        // 프리미엄 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("프리미엄", "프리미엄");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("프리미엄 서비스", "프리미엄");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("바우처", "프리미엄");
        
        // 여행/숙박 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("여행/숙박", "여행/숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("여행사", "여행/숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("호텔", "여행/숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("리조트", "여행/숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("온라인 여행사", "여행/숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("해외/여행", "여행/숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("여행", "여행/숙박");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("숙박", "여행/숙박");
        
        // 해외 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("해외", "해외");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("해외이용", "해외");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("해외직구", "해외");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("수수료우대", "해외");
        
        // 비즈니스 관련
        BENEFIT_TO_TRANSACTION_CATEGORY.put("비즈니스", "비즈니스");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("직장인", "비즈니스");
        
        // 기타 및 특수 유형들
        BENEFIT_TO_TRANSACTION_CATEGORY.put("하이브리드", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("제휴/PLCC", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("연회비지원", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("혜택 프로모션", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("혜택5", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("혜택2", "무실적");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("유의사항", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("지역", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("국내외가맹점", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("모든가맹점", "기타");
        BENEFIT_TO_TRANSACTION_CATEGORY.put("기타", "기타");
    }
    
    /**
     * 카드 혜택 카테고리를 거래내역 카테고리로 매핑합니다.
     * @param benefitCategory 카드 혜택 카테고리
     * @return 매핑된 거래내역 카테고리 (23개 표준 카테고리 중 하나)
     */
    public static String mapBenefitToTransactionCategory(String benefitCategory) {
        if (benefitCategory == null || benefitCategory.trim().isEmpty()) {
            return "기타";
        }
        
        String trimmedCategory = benefitCategory.trim();
        
        // 정확한 매칭 시도
        String mappedCategory = BENEFIT_TO_TRANSACTION_CATEGORY.get(trimmedCategory);
        if (mappedCategory != null) {
            return mappedCategory;
        }
        
        // 부분 매칭 시도 (카테고리명이 포함된 경우)
        for (Map.Entry<String, String> entry : BENEFIT_TO_TRANSACTION_CATEGORY.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            
            if (trimmedCategory.contains(key) || key.contains(trimmedCategory)) {
                return value;
            }
        }
        
        return "기타";
    }
    
    /**
     * 두 카테고리가 매치되는지 확인합니다.
     * @param benefitCategory 카드 혜택 카테고리
     * @param transactionCategory 거래내역 카테고리 (MerchantCategoryService에서 분류된)
     * @return 매치 여부
     */
    public static boolean isCategoryMatch(String benefitCategory, String transactionCategory) {
        if (benefitCategory == null || transactionCategory == null) {
            return false;
        }
        
        String mappedCategory = mapBenefitToTransactionCategory(benefitCategory);
        String trimmedTransactionCategory = transactionCategory.trim();
        
        // 정확한 매칭
        if (mappedCategory.equals(trimmedTransactionCategory)) {
            return true;
        }
        
        // 고도화된 매칭 로직 - 유사 카테고리 매칭
        return isAdvancedCategoryMatch(benefitCategory, trimmedTransactionCategory);
    }
    
    /**
     * 고도화된 카테고리 매칭 로직
     * 더 세밀한 카테고리 매칭을 위한 추가 규칙을 적용합니다.
     */
    private static boolean isAdvancedCategoryMatch(String benefitCategory, String transactionCategory) {
        // 1. 모든가맹점 혜택은 모든 카테고리와 매칭
        if ("모든가맹점".equals(benefitCategory) || "국내외가맹점".equals(benefitCategory)) {
            return true;
        }
        
        // 2. 선택형 혜택은 주요 카테고리와 매칭
        if ("선택형".equals(benefitCategory)) {
            return isSelectiveCompatibleCategory(transactionCategory);
        }
        
        // 3. 세부 카테고리 매칭 규칙
        return isDetailedCategoryMatch(benefitCategory, transactionCategory);
    }
    
    /**
     * 선택형 혜택과 호환되는 카테고리인지 확인합니다.
     */
    private static boolean isSelectiveCompatibleCategory(String transactionCategory) {
        // 선택형 혜택은 주로 일상 소비 카테고리에 적용
        Set<String> selectiveCategories = Set.of(
            "푸드", "카페/디저트", "마트/편의점", "쇼핑", "교통", "주유", 
            "통신", "공과금/렌탈", "뷰티/피트니스", "OTT/영화/문화"
        );
        return selectiveCategories.contains(transactionCategory);
    }
    
    /**
     * 세부 카테고리 매칭 규칙을 적용합니다.
     */
    private static boolean isDetailedCategoryMatch(String benefitCategory, String transactionCategory) {
        // 유사한 의미의 카테고리들을 매칭
        Map<String, Set<String>> similarCategories = Map.of(
            "교통", Set.of("교통", "대중교통", "지하철", "버스"),
            "푸드", Set.of("푸드", "외식", "음식점", "일반음식점", "점심", "저녁"),
            "쇼핑", Set.of("쇼핑", "온라인쇼핑", "백화점", "아울렛", "패션"),
            "마트/편의점", Set.of("마트", "편의점", "대형마트", "SSM", "생활"),
            "카페/디저트", Set.of("카페", "베이커리", "디저트", "아이스크림"),
            "OTT/영화/문화", Set.of("영화", "공연", "도서", "음원", "문화", "구독서비스")
        );
        
        for (Map.Entry<String, Set<String>> entry : similarCategories.entrySet()) {
            String standardCategory = entry.getKey();
            Set<String> aliases = entry.getValue();
            
            if (transactionCategory.equals(standardCategory)) {
                return aliases.contains(benefitCategory) || 
                       aliases.stream().anyMatch(alias -> benefitCategory.contains(alias));
            }
        }
        
        // 부분 매칭 - 혜택 카테고리가 거래 카테고리를 포함하거나 그 반대
        return benefitCategory.contains(transactionCategory) || 
               transactionCategory.contains(benefitCategory);
    }
    
    /**
     * 지원되는 모든 표준 카테고리 목록을 반환합니다.
     * @return 23개 표준 카테고리 목록
     */
    public static java.util.Set<String> getSupportedCategories() {
        java.util.Set<String> categories = new java.util.LinkedHashSet<>();
        categories.addAll(BENEFIT_TO_TRANSACTION_CATEGORY.values());
        categories.add("기타");
        return categories;
    }
    
    /**
     * 특정 거래내역 카테고리에 매핑되는 모든 혜택 카테고리들을 반환합니다.
     * @param transactionCategory 거래내역 카테고리
     * @return 해당 카테고리에 매핑되는 혜택 카테고리 목록
     */
    public static java.util.List<String> getBenefitCategoriesForTransaction(String transactionCategory) {
        java.util.List<String> benefitCategories = new java.util.ArrayList<>();
        
        for (Map.Entry<String, String> entry : BENEFIT_TO_TRANSACTION_CATEGORY.entrySet()) {
            if (transactionCategory.equals(entry.getValue())) {
                benefitCategories.add(entry.getKey());
            }
        }
        
        return benefitCategories;
    }
}