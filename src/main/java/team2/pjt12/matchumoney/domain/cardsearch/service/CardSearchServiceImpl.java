package team2.pjt12.matchumoney.domain.cardsearch.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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

@Slf4j
@Service
@RequiredArgsConstructor
public class CardSearchServiceImpl implements CardSearchService {
    private final CardSearchMapper cardSearchMapper;
    private final PersonaCardMapper personaCardMapper;

    @Override
    public List<CardListItemDTO> searchCards(Long userId, CardSearchRequestDTO req, int page, int size) {
        log.info("[searchCards] userId={}, page={}, size={}, credit={}, debit={}, benefits={}",
                userId, page, size, req.isCreditCard(), req.isDebitCard(), req.getSelectedBenefits());
        if (!req.isCreditCard() && !req.isDebitCard()) {
            req = new CardSearchRequestDTO(true, true, req.getSelectedBenefits());
        }

        int offset = Math.max(0, page) * Math.max(1, size);
        List<CardListItemDTO> rows = cardSearchMapper.selectCardsByPage(req, userId, offset, size);

        for (CardListItemDTO it : rows) {
            var opts = personaCardMapper.selectCardOptionsByCardId(it.getId());
            if (opts != null && opts.size() > 3) opts = opts.subList(0, 3);
            it.setOptions(opts);
        }
        return rows;
    }

    @Override
    public CursorPageResponse<CardListItemDTO> searchInfinite(Long userId, CardSearchRequestDTO req, String cursor, int size) {
        // 커서 파싱
        Long cursorId = (cursor == null || cursor.isBlank()) ? null : Long.parseLong(cursor);

        // size+1로 조회 후 hasNext 판정
        List<CardListItemDTO> rows = cardSearchMapper.selectCardsByCursor(userId, req, cursorId, size + 1);

        boolean hasNext = rows.size() > size;
        if (hasNext) rows = rows.subList(0, size);

        for (CardListItemDTO it : rows) {
            List<team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO> opts =
                    personaCardMapper.selectCardOptionsByCardId(it.getId());
            if (opts != null && opts.size() > 3) {
                opts = opts.subList(0, 3); // 화면은 Top3만
            }
            it.setOptions(opts);
        }

        String nextCursor = null;
        if (hasNext && !rows.isEmpty()) {
            Long lastId = rows.get(rows.size() - 1).getId();
            nextCursor = String.valueOf(lastId); // 다음 요청에 cursor로 그대로 사용
        }
        return new CursorPageResponse<>(rows, hasNext, nextCursor);
    }
}
