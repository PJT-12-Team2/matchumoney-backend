package team2.pjt12.matchumoney.domain.savingdetail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.savingdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.mapper.SavingDetailMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingDetailServiceImplTest {

    @Mock
    private SavingDetailMapper savingDetailMapper;

    @InjectMocks
    private SavingDetailServiceImpl savingDetailService;

    private Long userId;
    private Long savingProductId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        savingProductId = 100L;
    }

    @Test
    void getSavingDetailById_shouldReturnDetailWithLikeInfo() {
        // given
        SavingDetailResponseDTO dto = new SavingDetailResponseDTO();
        dto.setSavingProductId(savingProductId);
        dto.setKorCoNm("신한 적금 상품");

        when(savingDetailMapper.findSavingProductById(savingProductId, userId)).thenReturn(dto);
        when(savingDetailMapper.isLikedByUser(userId, savingProductId)).thenReturn(true);
        when(savingDetailMapper.countLikesByProductId(savingProductId)).thenReturn(30);

        // when
        SavingDetailResponseDTO result = savingDetailService.getSavingDetailById(userId, savingProductId);

        // then
        assertNotNull(result);
        assertEquals(savingProductId, result.getSavingProductId());
        assertEquals("신한 적금 상품", result.getKorCoNm());
        assertTrue(result.isLiked());
        assertEquals(30, result.getLikeCount());

        verify(savingDetailMapper).findSavingProductById(savingProductId, userId);
        verify(savingDetailMapper).isLikedByUser(userId, savingProductId);
        verify(savingDetailMapper).countLikesByProductId(savingProductId);
    }

    @Test
    void isUserLikedSaving_shouldReturnLikeTrue() {
        // given
        when(savingDetailMapper.isLikedByUser(userId, savingProductId)).thenReturn(false);

        // when
        LikeStatusResponseDTO result = savingDetailService.isUserLikedSaving(userId, savingProductId);

        // then
        assertTrue(result.isLiked());
        verify(savingDetailMapper).isLikedByUser(userId, savingProductId);
        verify(savingDetailMapper).insertUserLike(userId, savingProductId);
        verify(savingDetailMapper).countLikesByProductId(savingProductId);
    }

    @Test
    void isUserLikedSaving_shouldReturnLikeFalse() {
        // given
        when(savingDetailMapper.isLikedByUser(userId, savingProductId)).thenReturn(true);

        // when
        LikeStatusResponseDTO result = savingDetailService.isUserLikedSaving(userId, savingProductId);

        // then
        assertFalse(result.isLiked());
        verify(savingDetailMapper).isLikedByUser(userId, savingProductId);
        verify(savingDetailMapper).deleteUserLike(userId, savingProductId);
        verify(savingDetailMapper).countLikesByProductId(savingProductId);
    }
}
