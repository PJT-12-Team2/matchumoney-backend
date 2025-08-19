package team2.pjt12.matchumoney.domain.depositdetail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.mapper.DepositDetailMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositDetailServiceImplTest {

    @Mock
    private DepositDetailMapper depositDetailMapper;

    @InjectMocks
    private DepositDetailServiceImpl depositDetailService;

    private Long userId;
    private Long depositProductId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        depositProductId = 200L;
    }

    @Test
    void getDepositDetailById_shouldReturnDetailWithLikeInfo() {
        // given
        DepositDetailResponseDTO dto = new DepositDetailResponseDTO();
        dto.setDepositProductId(depositProductId);
        dto.setKorCoNm("우리은행 예금 상품");

        when(depositDetailMapper.findDepositProductById(depositProductId, userId)).thenReturn(dto);
        when(depositDetailMapper.isLikedByUser(userId, depositProductId)).thenReturn(true);
        when(depositDetailMapper.countLikesByProductId(depositProductId)).thenReturn(21);

        // when
        DepositDetailResponseDTO result = depositDetailService.getDepositDetailById(userId, depositProductId);

        // then
        assertNotNull(result);
        assertEquals(depositProductId, result.getDepositProductId());
        assertEquals("우리은행 예금 상품", result.getKorCoNm());
        assertTrue(result.getLiked());
        assertEquals(21, result.getLikeCount());

        verify(depositDetailMapper).findDepositProductById(depositProductId, userId);
        verify(depositDetailMapper).isLikedByUser(userId, depositProductId);
        verify(depositDetailMapper).countLikesByProductId(depositProductId);
    }

    @Test
    void isUserLikedDeposit_shouldReturnLikeTrue() {
        // 사용자가 좋아요를 아직 누르지 않은 경우
        when(depositDetailMapper.isLikedByUser(userId, depositProductId)).thenReturn(false);
        when(depositDetailMapper.countLikesByProductId(depositProductId)).thenReturn(10);

        LikeStatusResponseDTO result = depositDetailService.isUserLikedDeposit(userId, depositProductId);

        assertTrue(result.isLiked());
        assertEquals(10, result.getLikeCount());

        verify(depositDetailMapper).insertUserLike(userId, depositProductId);
        verify(depositDetailMapper).countLikesByProductId(depositProductId);
    }

    @Test
    void isUserLikedDeposit_shouldReturnLikeFalse() {
        // 사용자가 이미 좋아요를 누른 상태인 경우
        when(depositDetailMapper.isLikedByUser(userId, depositProductId)).thenReturn(true);
        when(depositDetailMapper.countLikesByProductId(depositProductId)).thenReturn(9);

        LikeStatusResponseDTO result = depositDetailService.isUserLikedDeposit(userId, depositProductId);

        assertFalse(result.isLiked());
        assertEquals(9, result.getLikeCount());

        verify(depositDetailMapper).deleteUserLike(userId, depositProductId);
        verify(depositDetailMapper).countLikesByProductId(depositProductId);
    }
}
