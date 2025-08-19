package team2.pjt12.matchumoney.domain.deposit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.DepositProductMapper;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.math.BigDecimal;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepositProductServiceImplTest {

    @Mock private DepositProductMapper depositProductMapper;
    @InjectMocks private DepositProductServiceImpl depositProductService;

    private DepositProductResponseDTO createMockProduct(String bankName, String productName, String etcNote) {
        return DepositProductResponseDTO.builder()
                .id(1L)
                .bankName(bankName)
                .productName(productName)
                .etcNote(etcNote)
                .maxSaveTrm(36)
                .maxIntrRate(new BigDecimal("2.5"))
                .maxIntrRate2(new BigDecimal("2.9"))
                .depositProductId(1L)
                .isFavorite(false)
                .liked(false)
                .likeCount(0)
                .build();
    }

    @Test
    @DisplayName("잔액 조건을 만족하는 상품만 추천한다")
    void getProductsByBalance_returnsEligibleProductsOnly() {
        // Given
        BalanceRequestDTO request = new BalanceRequestDTO("user123", 1000000L);

        List<DepositProductResponseDTO> allProducts = Arrays.asList(
                createMockProduct("국민은행", "고액예금", "최소 500만원 이상"), // 가입 불가능
                createMockProduct("신한은행", "일반예금", "최소 50만원 이상"),  // 가입 가능
                createMockProduct("우리은행", "자유예금", "제한 없음")         // 가입 가능
        );

        try (MockedStatic<SecurityUtils> mockedSecurityUtils = mockStatic(SecurityUtils.class)) {
            mockedSecurityUtils.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
            when(depositProductMapper.findAllDepositProducts(1L)).thenReturn(allProducts);

            // When
            List<DepositProductResponseDTO> result = depositProductService.getProductsByBalance(request);

            // Then
            assertThat(result).hasSize(2); // 가입 가능한 상품만 반환
            assertThat(result).noneMatch(product -> product.getProductName().equals("고액예금"));
            verify(depositProductMapper).findAllDepositProducts(1L);
        }
    }
}
