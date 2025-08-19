package team2.pjt12.matchumoney.domain.cardsearch.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardListItemDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CursorPageResponse;
import team2.pjt12.matchumoney.domain.cardsearch.mapper.CardSearchMapper;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonaCardMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CardSearchServiceImplTest {

    @Mock
    private CardSearchMapper cardSearchMapper;

    @Mock
    private PersonaCardMapper personaCardMapper;

    @InjectMocks
    private CardSearchServiceImpl cardSearchService;

    private Long userId;
    private CardSearchRequestDTO searchRequest;

    @BeforeEach
    void setUp() {
        userId = 1L;
        searchRequest = new CardSearchRequestDTO(true, true, List.of("교통", "통신"));
    }

    @Test
    void searchCards_shouldReturnPagedResults() {
        // given
        int offset = 0;
        int size = 2;

        List<CardListItemDTO> mockList = Arrays.asList(
                createCardItem(1L, "신한카드"),
                createCardItem(2L, "국민카드")
        );

        when(cardSearchMapper.selectCardsByPage(searchRequest, userId, offset, size))
                .thenReturn(mockList);

        // when
        List<CardListItemDTO> result = cardSearchService.searchCards(userId, searchRequest, offset, size);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("신한카드", result.get(0).getIssuer());
        verify(cardSearchMapper).selectCardsByPage(searchRequest, userId, offset, size);
    }

    @Test
    void searchInfinite_shouldReturnCursorPageResponse() {
        // given
        String cursor = null;
        int size = 2;

        List<CardListItemDTO> mockList = Arrays.asList(
                createCardItem(10L, "롯데카드"),
                createCardItem(9L, "하나카드"),
                createCardItem(8L, "삼성카드")
        );

        when(cardSearchMapper.selectCardsByCursor(userId, searchRequest, null, size + 1))
                .thenReturn(mockList);

        // when
        CursorPageResponse<CardListItemDTO> response = cardSearchService.searchInfinite(userId, searchRequest, cursor, size);

        // then
        assertNotNull(response);
        assertTrue(response.isHasNext());
        assertEquals("9", response.getNextCursor());
        assertEquals(2, response.getItems().size());
        verify(cardSearchMapper).selectCardsByCursor(userId, searchRequest, null, size + 1);
    }

    private CardListItemDTO createCardItem(Long id, String issuer) {
        CardListItemDTO dto = new CardListItemDTO();
        dto.setId(id);
        dto.setIssuer(issuer);
        dto.setName("테스트카드");
        dto.setImageUrl("http://example.com/test.png");
        return dto;
    }
}
