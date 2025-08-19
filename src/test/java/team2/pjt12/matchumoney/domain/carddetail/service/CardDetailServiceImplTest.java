package team2.pjt12.matchumoney.domain.carddetail.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;
import team2.pjt12.matchumoney.domain.carddetail.mapper.CardDetailMapper;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardDetailServiceImplTest {

    @Mock
    private CardDetailMapper cardDetailMapper;

    @Mock
    private UserMapper userMapper;

    @InjectMocks
    private CardDetailServiceImpl cardDetailService;

    private Long userId;
    private int cardProductId;

    @BeforeEach
    void setUp() {
        userId = 1L;
        cardProductId = 100;
    }

    @Test
    void getCardDetailById_shouldReturnFullDetail() {
        // given
        CardDetailResponseDTO dto = new CardDetailResponseDTO();
        dto.setCardProductId(cardProductId);
        dto.setName("현대 M카드");
        dto.setType("신용카드");

        when(cardDetailMapper.findCardDetailById(cardProductId)).thenReturn(dto);
        when(cardDetailMapper.isLikedByUser(userId, cardProductId)).thenReturn(true);
        when(cardDetailMapper.countLikesByProductId(cardProductId)).thenReturn(30);
        when(userMapper.isCardFavoriteExists(userId, (long) cardProductId)).thenReturn(true);

        // when
        CardDetailResponseDTO result = cardDetailService.getCardDetailById(userId, cardProductId);

        // then
        assertNotNull(result);
        assertEquals(cardProductId, result.getCardProductId());
        assertEquals("현대 M카드", result.getName());
        assertTrue(result.isLiked());
        assertEquals(30, result.getLikeCount());
        assertTrue(result.isStarred());

        verify(cardDetailMapper).findCardDetailById(cardProductId);
        verify(cardDetailMapper).isLikedByUser(userId, cardProductId);
        verify(cardDetailMapper).countLikesByProductId(cardProductId);
        verify(userMapper).isCardFavoriteExists(userId, (long) cardProductId);
    }

    @Test
    void isUserLikedCard_shouldReturnLikeTrue() {
        when(cardDetailMapper.isLikedByUser(userId, cardProductId)).thenReturn(false);
        LikeStatusResponseDTO result = cardDetailService.isUserLikedCard(userId, cardProductId);

        assertTrue(result.isLiked());
        verify(cardDetailMapper).isLikedByUser(userId, cardProductId);
    }

    @Test
    void isUserLikedCard_shouldReturnLikeFalse() {
        when(cardDetailMapper.isLikedByUser(userId, cardProductId)).thenReturn(true);

        LikeStatusResponseDTO result = cardDetailService.isUserLikedCard(userId, cardProductId);

        assertFalse(result.isLiked());
        verify(cardDetailMapper).isLikedByUser(userId, cardProductId);
    }
}
