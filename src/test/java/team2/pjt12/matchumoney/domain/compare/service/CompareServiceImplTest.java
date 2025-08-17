package team2.pjt12.matchumoney.domain.compare.service;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import team2.pjt12.matchumoney.domain.compare.dto.CompareCardResponseDTO;
import team2.pjt12.matchumoney.domain.compare.dto.CompareDepositResponseDTO;
import team2.pjt12.matchumoney.domain.compare.dto.CompareProductsResponseDTO;
import team2.pjt12.matchumoney.domain.compare.dto.CompareSavingResponseDTO;
import team2.pjt12.matchumoney.domain.compare.mapper.CompareProductMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;


@ExtendWith(MockitoExtension.class)
class CompareServiceImplTest {

    @Mock
    private CompareProductMapper compareProductMapper;

    @InjectMocks
    private CompareServiceImpl compareService;

    @BeforeEach
    void setUp() {
        // ✅ 1. UserVO mock 객체 생성
        UserVO mockUser = UserVO.builder()
                .userId(1L)
                .nickname("테스트유저")
                .email("test@example.com")
                .socialLogin(false)
                .build();

        // ✅ 2. principal에 UserVO 삽입하여 인증 정보 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ 3. SecurityUtils의 static 메서드 mocking
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
        }
    }


    @Test
    void getProducts_withDepositType_returnsDeposits() {
        CompareDepositResponseDTO deposit = new CompareDepositResponseDTO();
        deposit.setFinPrdtName("예금상품");
        when(compareProductMapper.selectDepositProductsByIds(List.of(2L), 1L)).thenReturn(List.of(deposit));

        CompareProductsResponseDTO result = compareService.getProducts("DEPOSIT", List.of(2L));

        assertThat(result.getDeposits()).hasSize(1);
        assertThat(result.getDeposits().get(0).getFinPrdtName()).isEqualTo("예금상품");
    }

    @Test
    void getProducts_withSavingType_returnsSavings() {
        CompareSavingResponseDTO saving1 = new CompareSavingResponseDTO();
        saving1.setFinPrdtName("적금상품1");

        CompareSavingResponseDTO saving2 = new CompareSavingResponseDTO();
        saving2.setFinPrdtName("적금상품2");

        when(compareProductMapper.selectSavingProductsByIds(List.of(2L, 1L), 1L))
                .thenReturn(List.of(saving1, saving2));  // ✅ 2개 반환

        CompareProductsResponseDTO result = compareService.getProducts("SAVING", List.of(2L, 1L));

        assertThat(result.getSavings()).hasSize(2);  // ✅ 이제 통과
        assertThat(result.getSavings().get(0).getFinPrdtName()).isEqualTo("적금상품1");
    }


    @Test
    void getProducts_withCardType_returnsCards() {
        CompareCardResponseDTO card = new CompareCardResponseDTO();
        card.setFinPrdtName("신용카드");
        when(compareProductMapper.selectCardProductsByIds(List.of(2L), 1L)).thenReturn(List.of(card));

        CompareProductsResponseDTO result = compareService.getProducts("CARD", List.of(2L));

        assertThat(result.getCards()).hasSize(1);
        assertThat(result.getCards().get(0).getFinPrdtName()).isEqualTo("신용카드");
    }

    @Test
    void getProducts_withUnsupportedType_throwsCustomException() {
        assertThrows(CustomException.class, () -> {
            compareService.getProducts("UNKNOWN", List.of(1L));
        });
    }

}
