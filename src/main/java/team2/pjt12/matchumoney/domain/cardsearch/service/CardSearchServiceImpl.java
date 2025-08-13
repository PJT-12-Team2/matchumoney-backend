package team2.pjt12.matchumoney.domain.cardsearch.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardListItemDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CursorPageResponse;
import team2.pjt12.matchumoney.domain.cardsearch.mapper.CardSearchMapper;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonaCardMapper;

import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
public class CardSearchServiceImpl implements CardSearchService {
    private final CardSearchMapper cardSearchMapper;
    private final PersonaCardMapper personaCardMapper;

    @Override
    public List<CardSearchResponseDTO> searchCards(CardSearchRequestDTO request) {
        Long userId = getCurrentUser().getUserId();
        List<CardSearchResponseDTO> cards = cardSearchMapper.selectCardsByFilter(request, userId);
        for (CardSearchResponseDTO card : cards) {
            List<CardOptionDTO> options = personaCardMapper.selectCardOptionsByCardId(card.getId());
            card.setOptions(options);
        }
        return cards;
    }

    @Override
    public CursorPageResponse<CardListItemDTO> searchInfinite(Long userId, CardSearchRequestDTO req, String cursor, int size) {
        // 커서 파싱
        Long cursorId = (cursor == null || cursor.isBlank()) ? null : Long.parseLong(cursor);

        // size+1로 조회 후 hasNext 판정
        List<CardListItemDTO> rows = cardSearchMapper.selectCardsByCursor(userId, req, cursorId, size + 1);

        boolean hasNext = rows.size() > size;
        if (hasNext) rows = rows.subList(0, size);

        String nextCursor = null;
        if (hasNext && !rows.isEmpty()) {
            Long lastId = rows.get(rows.size() - 1).getId();
            nextCursor = String.valueOf(lastId); // 다음 요청에 cursor로 그대로 사용
        }
        return new CursorPageResponse<>(rows, hasNext, nextCursor);
    }
}
