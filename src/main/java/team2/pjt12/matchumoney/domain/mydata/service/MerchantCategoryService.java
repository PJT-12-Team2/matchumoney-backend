package team2.pjt12.matchumoney.domain.mydata.service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.*;

/**
 * 가맹점명을 기반으로 소비 분야를 분류하는 서비스
 * 
 * @author MatchuMoney Team
 * @since 1.0ㄴ
 */
@Slf4j
@Service
public class MerchantCategoryService {
    
    // 소비 분야별 키워드 맵 (우선순위 순으로 정렬)
    private static final Map<String, List<String>> CATEGORY_KEYWORDS = new LinkedHashMap<>();
    
    static {
        // 배달앱 (가장 우선적으로 분류)
        CATEGORY_KEYWORDS.put("배달앱", Arrays.asList(
            "배달의민족", "배민", "요기요", "쿠팡이츠", "COUPANG EATS", "배달", "딜리버리",
            "배달앱", "우버이츠", "UBER EATS", "배달주문", "푸드딜리버리", "우아한형제들"
        ));
        
        // 편의점 (구체적인 브랜드명들)
        CATEGORY_KEYWORDS.put("편의점", Arrays.asList(
            "GS25", "CU", "세븐일레븐", "7-ELEVEN", "이마트24", "미니스톱", "MINISTOP",
            "편의점", "CVS", "씨유", "지에스", "세븐", "24시", "24", "GS", "세븐일레븐S",
            "씨유(CU)", "지에스(GS)25", "이마트24"
        ));
        
        // 카페 (카페 브랜드 및 일반명)
        CATEGORY_KEYWORDS.put("카페", Arrays.asList(
            "스타벅스", "STARBUCKS", "투썸플레이스", "A TWOSOME", "이디야", "EDIYA",
            "커피빈", "COFFEE BEAN", "할리스", "HOLLYS", "탐앤탐스", "TOM N TOMS",
            "메가커피", "MEGA", "카페베네", "CAFFE BENE", "엔젤인어스", "ANGEL-IN-US",
            "드롭탑", "커피", "COFFEE", "카페", "CAFE", "BEAN", "원두", "라떼", "아메리카노",
            "빽다방", "컴포즈커피", "더벤티", "폴바셋", "파스쿠찌", "설빙"
        ));
        
        // 교통 (모빌리티, 대중교통)
        CATEGORY_KEYWORDS.put("교통", Arrays.asList(
            "지하철", "SUBWAY", "버스", "BUS", "택시", "TAXI", "주유소", "GS칼텍스",
            "SK에너지", "현대오일뱅크", "S-OIL", "알뜰주유소", "셀프주유소",
            "교통카드", "하이패스", "Hi-pass", "톨게이트", "주차장", "PARKING",
            "카카오 T", "카카오T", "우버", "UBER", "타다", "바이크", "킥보드", "모빌리티"
        ));
        
        // 온라인서비스 (구독, 디지털 서비스)
        CATEGORY_KEYWORDS.put("온라인서비스", Arrays.asList(
            "CLAUDE.AI", "CLAUDE", "OPENAI", "CHATGPT", "GPT", "유튜브프리미엄", "YOUTUBE",
            "넷플릭스", "NETFLIX", "구글", "GOOGLE", "애플", "APPLE", "마이크로소프트", "MICROSOFT",
            "네이버페이", "카카오페이", "페이팔", "PAYPAL", "구독", "SUBSCRIPTION", "프리미엄",
            "클라우드", "CLOUD", "스트리밍", "STREAMING"
        ));
        
        // 마트 (대형마트, 슈퍼마켓, 생활용품)
        CATEGORY_KEYWORDS.put("마트", Arrays.asList(
            "이마트", "E-MART", "롯데마트", "LOTTE MART", "홈플러스", "HOMEPLUS",
            "코스트코", "COSTCO", "하나로마트", "농협", "슈퍼마켓", "수퍼마켓", "마트",
            "MART", "하이마트", "HI-MART", "전자랜드", "마켓컬리", "쿠팡", "SSG",
            "다이소", "아성다이소", "DAISO", "생활용품", "잡화"
        ));
        
        // 음식점 (일반 음식점, 레스토랑)
        CATEGORY_KEYWORDS.put("음식점", Arrays.asList(
            "맥도날드", "MCDONALD", "버거킹", "BURGER KING", "KFC", "롯데리아", "LOTTERIA",
            "피자헛", "PIZZA HUT", "도미노피자", "DOMINO", "치킨", "피자", "PIZZA",
            "한식", "중식", "일식", "양식", "분식", "족발", "보쌈", "삼겹살", "갈비",
            "찜닭", "치킨", "hamburger", "레스토랑", "RESTAURANT", "푸드", "FOOD",
            "김밥", "떡볶이", "순대", "라면", "국수", "냉면", "비빔밥", "돈까스",
            "세종김밥떡볶이", "신사골감자탕", "천지샤브샤브", "혼다라멘", "엽기떡볶이",
            "칼국수", "고을칼국수", "감자탕", "샤브샤브", "라멘", "모모야",
                "써브웨이","더개미", "The개미", "채움", "알고"
        ));
        
        // 주점/술집
        CATEGORY_KEYWORDS.put("주점", Arrays.asList("호프", "맥주", "술집", "주점", "바", "BAR",
            "칵테일", "위스키", "소주", "맥주집", "치킨호프"
        ));
        
        // 디저트/아이스크림
        CATEGORY_KEYWORDS.put("디저트", Arrays.asList(
            "아이스크림", "빙수", "케이크", "디저트", "베이커리", "도넛", "마카롱",
            "30cm 아이스크림", "베스킨라빈스", "던킨도넛", "크리스피크림", "파리바게뜨"
        ));
        
        // 쇼핑/백화점
        CATEGORY_KEYWORDS.put("쇼핑", Arrays.asList(
            "백화점", "롯데백화점", "신세계백화점", "현대백화점", "갤러리아",
            "행복한백화점", "아울렛", "OUTLET", "쇼핑몰", "MALL", "빈티지", "1978빈티지", "빈프라임"
        ));
        
        // 결제서비스
        CATEGORY_KEYWORDS.put("결제서비스", Arrays.asList(
            "NICE 결제대행", "NICE", "결제대행", "PG", "결제", "페이", "PAY",
            "카카오_선물하기", "선물하기", "상품권", "기프트카드", "포인트"
        ));
        
        // 흡연용품
        CATEGORY_KEYWORDS.put("흡연용품", Arrays.asList(
            "전담", "GATE", "전자담배", "담배", "흡연", "시가", "라이터"
        ));
        
        // 영화관
        CATEGORY_KEYWORDS.put("영화관", Arrays.asList(
            "CGV", "롯데시네마", "LOTTE CINEMA", "메가박스", "MEGABOX", "영화관",
            "시네마", "CINEMA", "무비", "MOVIE", "상영관", "극장"
        ));
        
        // 문화 (서점, 문화시설)
        CATEGORY_KEYWORDS.put("문화", Arrays.asList(
            "교보문고", "KYOBO", "영풍문고", "알라딘", "YES24", "서점", "도서관",
            "박물관", "미술관", "전시관", "콘서트", "공연", "뮤지컬", "연극",
            "아트센터", "문화센터", "갤러리", "GALLERY", "북스", "BOOKS", "레전드"
        ));
        
        // 화장품 (뷰티, 화장품)
        CATEGORY_KEYWORDS.put("화장품", Arrays.asList(
            "올리브영", "OLIVE YOUNG", "롭스", "LOHBS", "부츠", "BOOTS", "세포라", "SEPHORA",
            "미샤", "MISSHA", "더페이스샵", "THE FACE SHOP", "이니스프리", "INNISFREE",
            "에뛰드하우스", "ETUDE HOUSE", "네이처리퍼블릭", "NATURE REPUBLIC",
            "화장품", "코스메틱", "COSMETIC", "뷰티", "BEAUTY", "향수", "PERFUME",
            "토니앤가이", "아모레퍼시픽", "LG생활건강", "헤라", "설화수", "라네즈", "씨제이올리브네트웍스"
        ));
        
        // 온라인쇼핑
        CATEGORY_KEYWORDS.put("온라인쇼핑", Arrays.asList(
            "11번가", "11ST", "G마켓", "GMARKET", "옥션", "AUCTION", "인터파크", "INTERPARK",
            "위메프", "WEMAKEPRICE", "티몬", "TMON", "하프클럽", "CJ온스타일", "GS SHOP",
            "롯데닷컴", "신세계몰", "현대HMall", "온라인", "ONLINE", "쇼핑몰", "MALL"
        ));
        
        // 패션 (의류, 신발, 가방)
        CATEGORY_KEYWORDS.put("패션", Arrays.asList(
            "유니클로", "UNIQLO", "ZARA", "H&M", "무신사", "29CM", "브랜디", "BRANDY",
            "스타일난다", "STYLENANDA", "에이블리", "ABLY", "의류", "옷", "신발",
            "SHOES", "가방", "BAG", "패션", "FASHION", "아울렛", "OUTLET"
        ));
        
        // 숙박 (호텔, 모텔, 펜션)
        CATEGORY_KEYWORDS.put("숙박", Arrays.asList(
            "호텔", "HOTEL", "모텔", "MOTEL", "펜션", "PENSION", "리조트", "RESORT",
            "게스트하우스", "GUEST HOUSE", "숙박", "숙소", "에어비앤비", "AIRBNB",
            "야놀자", "여기어때", "롯데호텔", "신라호텔", "조선호텔", "하얏트"
        ));
        
        // 통신 (휴대폰, 인터넷)
        CATEGORY_KEYWORDS.put("통신", Arrays.asList(
            "SK텔레콤", "SKT", "KT", "LG유플러스", "LG U+", "휴대폰", "핸드폰", "폰",
            "통신비", "인터넷", "INTERNET", "와이파이", "WIFI", "데이터", "통신사"
        ));
        
        // 의료 (병원, 약국)
        CATEGORY_KEYWORDS.put("의료", Arrays.asList(
            "병원", "HOSPITAL", "의원", "클리닉", "CLINIC", "약국", "PHARMACY",
            "한의원", "치과", "안과", "이비인후과", "피부과", "정형외과", "내과",
            "메디컬", "MEDICAL", "헬스케어", "HEALTHCARE", "약국", "드러그스토어"
        ));
        
        // 금융 (은행, 보험)
        CATEGORY_KEYWORDS.put("금융", Arrays.asList(
            "은행", "BANK", "농협", "신한", "우리", "하나", "KB국민", "기업", "씨티",
            "SC제일", "보험", "INSURANCE", "증권", "카드", "ATM", "현금인출기"
        ));
    }
    
    /**
     * 가맹점명을 분석하여 적절한 소비 분야를 반환합니다.
     * 
     * @param merchantName 가맹점명
     * @return 분류된 소비 분야 (기타 포함)
     */
    public String categorizeByMerchantName(String merchantName) {
        if (merchantName == null || merchantName.trim().isEmpty()) {
            log.debug("가맹점명이 null 또는 비어있음");
            return "기타";
        }
        
        String normalizedName = normalizeText(merchantName);
        log.debug("가맹점명 분류 시도: '{}' -> 정규화: '{}'", merchantName, normalizedName);
        
        // 우선순위에 따라 카테고리 매칭
        for (Map.Entry<String, List<String>> entry : CATEGORY_KEYWORDS.entrySet()) {
            String category = entry.getKey();
            List<String> keywords = entry.getValue();
            
            for (String keyword : keywords) {
                if (containsKeyword(normalizedName, keyword)) {
                    log.debug("매칭 성공: '{}' -> '{}' (키워드: '{}')", merchantName, category, keyword);
                    return category;
                }
            }
        }
        
        log.debug("매칭 실패: '{}' -> '기타'", merchantName);
        return "기타";
    }
    
    /**
     * 텍스트를 정규화합니다 (대소문자, 공백, 특수문자 처리)
     * 매칭의 확률을 올립니다.
     * 
     * @param text 원본 텍스트
     * @return 정규화된 텍스트
     */
    private String normalizeText(String text) {
        return text.toUpperCase()
                  .replaceAll("\\s+", "")      // 공백 제거
                  .replaceAll("[^A-Z0-9가-힣*_\\-\\.]", ""); // 특수문자 제거 (영문, 숫자, 한글, *, _, -, . 유지)
    }
    
    /**
     * 정규화된 가맹점명에 키워드가 포함되어 있는지 확인합니다.
     * 
     * @param normalizedMerchantName 정규화된 가맹점명
     * @param keyword 검색할 키워드
     * @return 포함 여부
     */
    private boolean containsKeyword(String normalizedMerchantName, String keyword) {
        String normalizedKeyword = normalizeText(keyword);
        return normalizedMerchantName.contains(normalizedKeyword);
    }
    
    /**
     * 모든 지원되는 카테고리 목록을 반환합니다.
     * 
     * @return 카테고리 목록
     */
    public Set<String> getSupportedCategories() {
        Set<String> categories = new LinkedHashSet<>(CATEGORY_KEYWORDS.keySet());
        categories.add("기타");
        return categories;
    }
    
    /**
     * 특정 카테고리의 키워드 목록을 반환합니다.
     * 
     * @param category 카테고리명
     * @return 키워드 목록
     */
    public List<String> getCategoryKeywords(String category) {
        return CATEGORY_KEYWORDS.getOrDefault(category, Collections.emptyList());
    }

    /**
     * 가맹점명 분류 결과를 상세히 반환합니다.
     *
     * @param merchantName 가맹점명
     * @return 분류 결과 정보
     */
    public Map<String, Object> categorizeWithDetails(String merchantName) {
        Map<String, Object> result = new HashMap<>();
        result.put("originalName", merchantName);
        result.put("normalizedName", normalizeText(merchantName));
        result.put("category", categorizeByMerchantName(merchantName));
        result.put("supportedCategories", getSupportedCategories());

        return result;
    }
}
