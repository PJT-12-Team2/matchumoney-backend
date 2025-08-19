package team2.pjt12.matchumoney.domain.deposit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.deposit.domain.UserDepositVO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.UserDepositResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.UserDepositMapper;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class UserDepositServiceImplTest {

    @Mock private UserDepositMapper userDepositMapper;
    @InjectMocks private UserDepositServiceImpl userDepositService;

    private UserDepositVO createMockUserDepositVO(String accountNo, String accountName, Long balance, String nickname) {
        return UserDepositVO.builder()
                .accountNo(accountNo)
                .accountName(accountName)
                .balance(balance)
                .userId("user123")
                .nickname(nickname)
                .build();
    }

    @Test
    @DisplayName("사용자 계좌 조회 시 계좌 목록과 잔액이 올바르게 반환된다")
    void getAccountsByUserId_returnsMaskedAccountInfoAndFormattedBalance() {
        // Given
        String userId = "user123";
        List<UserDepositVO> mockUserDeposits = Arrays.asList(
                createMockUserDepositVO("1234567890123", "KB 주거래 예금", 1000000L, "머니머니"),
                createMockUserDepositVO("9876543210987", "신한 자유적금", 500000L, "머니머니"),
                createMockUserDepositVO("5555666677778", "우리 정기예금", 2000000L, "머니머니")
        );

        when(userDepositMapper.getAccountsByUserId(userId)).thenReturn(mockUserDeposits);

        // When
        List<UserDepositResponseDTO> result = userDepositService.getAccountsByUserId(userId);

        // Then
        assertThat(result).hasSize(3);

        // 첫 번째 계좌 검증
        UserDepositResponseDTO firstAccount = result.get(0);
        assertThat(firstAccount.getAccountNo()).isEqualTo("12345****90123"); // 마스킹 확인
        assertThat(firstAccount.getAccountName()).isEqualTo("KB 주거래 예금");
        assertThat(firstAccount.getFormattedBalance()).isEqualTo("1,000,000원"); // 포맷팅 확인
        assertThat(firstAccount.getNickname()).isEqualTo("머니머니");

        // 두 번째 계좌 검증
        UserDepositResponseDTO secondAccount = result.get(1);
        assertThat(secondAccount.getAccountNo()).isEqualTo("98765****10987");
        assertThat(secondAccount.getFormattedBalance()).isEqualTo("500,000원");

        // 세 번째 계좌 검증
        UserDepositResponseDTO thirdAccount = result.get(2);
        assertThat(thirdAccount.getFormattedBalance()).isEqualTo("2,000,000원");

        verify(userDepositMapper).getAccountsByUserId(userId);
    }
}
