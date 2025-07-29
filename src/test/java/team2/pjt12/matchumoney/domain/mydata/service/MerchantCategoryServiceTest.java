package team2.pjt12.matchumoney.domain.mydata.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.util.List;
import java.util.Map;
import java.util.Set;

import static org.assertj.core.api.Assertions.*;

@DisplayName("MerchantCategoryService 소비 패턴 분류 테스트")
class MerchantCategoryServiceTest {

    private MerchantCategoryService merchantCategoryService;

    @BeforeEach
    void setUp() {
        merchantCategoryService = new MerchantCategoryService();
    }

    @ParameterizedTest
    @CsvSource({
        "'스타벅스 강남점', '카페'",
        "'맥도날드 홍대점', '음식점'",
        "'이마트 용산점', '마트'",
        "'GS25 역삼점', '편의점'",
        "'배달의민족', '배달앱'",
        "'KFC 신촌점', '음식점'",
        "'투썸플레이스', '카페'",
        "'7-ELEVEN', '편의점'",
        "'요기요', '배달앱'",
        "'롯데마트', '마트'"
    })
    @DisplayName("가맹점명 기반 소비 분야 분류 - 일반적인 케이스")
    void categorizeByMerchantName_CommonCases(String merchantName, String expectedCategory) {
        // when
        String result = merchantCategoryService.categorizeByMerchantName(merchantName);

        // then
        assertThat(result).isEqualTo(expectedCategory);
    }

    @Test
    @DisplayName("배달앱 우선순위 테스트 - 배달의민족이 카페보다 우선")
    void categorizeByMerchantName_DeliveryAppPriority() {
        // given
        String merchantName = "배달의민족 스타벅스";

        // when
        String result = merchantCategoryService.categorizeByMerchantName(merchantName);

        // then
        assertThat(result).isEqualTo("배달앱"); // 카페가 아니라 배달앱으로 분류되어야 함
    }

    @Test
    @DisplayName("정규화 처리 테스트 - 공백, 특수문자, 대소문자")
    void categorizeByMerchantName_NormalizationTest() {
        // given & when & then
        assertThat(merchantCategoryService.categorizeByMerchantName("스타 벅스")).isEqualTo("카페");
        assertThat(merchantCategoryService.categorizeByMerchantName("STARBUCKS")).isEqualTo("카페");
        assertThat(merchantCategoryService.categorizeByMerchantName("스타벅스!!!")).isEqualTo("카페");
        assertThat(merchantCategoryService.categorizeByMerchantName("G S 2 5")).isEqualTo("편의점");
        assertThat(merchantCategoryService.categorizeByMerchantName("mcdonald's")).isEqualTo("음식점");
    }

    @Test
    @DisplayName("매칭되지 않는 가맹점명 - 기타로 분류")
    void categorizeByMerchantName_UnknownMerchant() {
        // given
        String unknownMerchant = "알 수 없는 가맹점";

        // when
        String result = merchantCategoryService.categorizeByMerchantName(unknownMerchant);

        // then
        assertThat(result).isEqualTo("기타");
    }

    @Test
    @DisplayName("빈 문자열 및 null 처리")
    void categorizeByMerchantName_EdgeCases() {
        // when & then
        assertThat(merchantCategoryService.categorizeByMerchantName("")).isEqualTo("기타");
        assertThat(merchantCategoryService.categorizeByMerchantName("   ")).isEqualTo("기타");
        assertThat(merchantCategoryService.categorizeByMerchantName(null)).isEqualTo("기타");
    }

    @Test
    @DisplayName("지원되는 카테고리 목록 조회")
    void getSupportedCategories() {
        // when
        Set<String> categories = merchantCategoryService.getSupportedCategories();

        // then
        assertThat(categories).isNotNull();
        assertThat(categories).isNotEmpty();
        assertThat(categories).contains("카페", "편의점", "마트", "음식점", "배달앱", "기타");
        assertThat(categories.size()).isGreaterThan(5);
    }

    @Test
    @DisplayName("특정 카테고리의 키워드 목록 조회")
    void getCategoryKeywords() {
        // when
        List<String> cafeKeywords = merchantCategoryService.getCategoryKeywords("카페");
        List<String> convenienceKeywords = merchantCategoryService.getCategoryKeywords("편의점");

        // then
        assertThat(cafeKeywords).isNotNull();
        assertThat(cafeKeywords).contains("스타벅스", "투썸플레이스", "카페");
        
        assertThat(convenienceKeywords).isNotNull();
        assertThat(convenienceKeywords).contains("GS25", "CU", "세븐일레븐");
    }

    @Test
    @DisplayName("존재하지 않는 카테고리의 키워드 조회")
    void getCategoryKeywords_NonExistentCategory() {
        // when
        List<String> result = merchantCategoryService.getCategoryKeywords("존재하지않는카테고리");

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("상세 분류 정보 반환 테스트")
    void categorizeWithDetails() {
        // given
        String merchantName = "스타벅스 강남점";

        // when
        Map<String, Object> result = merchantCategoryService.categorizeWithDetails(merchantName);

        // then
        assertThat(result).isNotNull();
        assertThat(result.get("originalName")).isEqualTo("스타벅스 강남점");
        assertThat(result.get("normalizedName")).isEqualTo("스타벅스강남점");
        assertThat(result.get("category")).isEqualTo("카페");
        assertThat(result.get("supportedCategories")).isNotNull();
    }

    @Test
    @DisplayName("실제 거래 내역 시나리오 테스트")
    void realWorldScenarioTest() {
        // 실제 거래 내역에서 나올 수 있는 가맹점명들
        String[] realMerchants = {
            "스타벅스코리아 강남",
            "이마트 24 역삼점",
            "배달의민족*맥도날드",
            "GS25(편의점)",
            "요기요 주문",
            "KFC 신촌",
            "세븐일레븐 강남점",
            "투썸플레이스강남",
            "COUPANG EATS 배달"
        };

        String[] expectedCategories = {
            "카페", "편의점", "배달앱", "편의점", "배달앱", 
            "음식점", "편의점", "카페", "배달앱"
        };

        // when & then
        for (int i = 0; i < realMerchants.length; i++) {
            String category = merchantCategoryService.categorizeByMerchantName(realMerchants[i]);
            assertThat(category)
                .as("가맹점 '%s'는 '%s'로 분류되어야 합니다", realMerchants[i], expectedCategories[i])
                .isEqualTo(expectedCategories[i]);
        }
    }

    @Test
    @DisplayName("분류 성능 테스트 - 대량 데이터 처리")
    void performanceTest() {
        // given
        String[] testMerchants = {"스타벅스", "GS25", "맥도날드", "이마트", "배달의민족"};
        
        // when
        long startTime = System.currentTimeMillis();
        for (int i = 0; i < 1000; i++) {
            for (String merchant : testMerchants) {
                merchantCategoryService.categorizeByMerchantName(merchant + i);
            }
        }
        long endTime = System.currentTimeMillis();

        // then
        long executionTime = endTime - startTime;
        assertThat(executionTime).isLessThan(1000); // 1초 이내에 5000개 분류 완료
        System.out.println("5000개 가맹점 분류 소요 시간: " + executionTime + "ms");
    }
}