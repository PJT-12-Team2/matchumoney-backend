// src/test/java/team2/pjt12/matchumoney/domain/personacard/service/PersonaCardServiceImplTest.java
package team2.pjt12.matchumoney.domain.personacard.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardResponseDTO;
import team2.pjt12.matchumoney.domain.personacard.mapper.PersonaCardMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class PersonaCardServiceImplTest {

    @Mock private PersonaCardMapper personaCardMapper;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private PersonaCardServiceImpl service;

    private static final Long P_ID = 5L;

    private PersonaCardDTO mockCard(long cardId) {
        PersonaCardDTO card = mock(PersonaCardDTO.class);
        when(card.getCardId()).thenReturn(cardId);
        return card;
    }

    private List<PersonaCardDTO> mockCards(long startId, int n) {
        return IntStream.range(0, n).mapToObj(i -> mockCard(startId + i)).collect(Collectors.toList());
    }

    // ---------------------- getRecommendedCards ----------------------
    @Nested
    @DisplayName("getRecommendedCards(personaId)")
    class RecommendByPersonaId {

        @Test
        @DisplayName("PC-002 후보 2건 → 2건 반환 + 옵션 세팅")
        void pc002() {
            given(personaCardMapper.selectPersonaNameById(P_ID)).willReturn("소비왕");
            List<PersonaCardDTO> base = mockCards(200, 2);
            given(personaCardMapper.selectCardsByPersonaId(P_ID)).willReturn(base);

            for (PersonaCardDTO card : base) {
                when(personaCardMapper.selectCardOptionsByCardId(card.getCardId()))
                        .thenReturn(List.of(mock(CardOptionDTO.class)));
            }

            PersonaCardResponseDTO res = service.getRecommendedCards(P_ID);

            assertEquals("소비왕", res.getPersonaName());
            assertEquals(2, res.getCards().size());
            for (PersonaCardDTO card : res.getCards()) {
                verify(personaCardMapper).selectCardOptionsByCardId(card.getCardId());
                verify(card).setOptions(anyList());
            }
        }

        @Test
        @DisplayName("PC-003 후보 없음 → 빈 리스트 반환")
        void pc003() {
            given(personaCardMapper.selectPersonaNameById(P_ID)).willReturn("소비왕");
            given(personaCardMapper.selectCardsByPersonaId(P_ID)).willReturn(Collections.emptyList());

            PersonaCardResponseDTO res = service.getRecommendedCards(P_ID);

            assertEquals("소비왕", res.getPersonaName());
            assertTrue(res.getCards().isEmpty());
            verify(personaCardMapper, never()).selectCardOptionsByCardId(anyLong());
        }

        @Test
        @DisplayName("PC-005 카드 리스트 null → NPE 발생")
        void pc005() {
            given(personaCardMapper.selectPersonaNameById(P_ID)).willReturn("소비왕");
            given(personaCardMapper.selectCardsByPersonaId(P_ID)).willReturn(null);

            assertThrows(NullPointerException.class, () -> service.getRecommendedCards(P_ID));
        }

        @Test
        @DisplayName("PC-006 옵션 조회 결과 null → setOptions(null) 호출, 예외 없음")
        void pc006() {
            given(personaCardMapper.selectPersonaNameById(P_ID)).willReturn("소비왕");
            List<PersonaCardDTO> base = mockCards(400, 2);
            given(personaCardMapper.selectCardsByPersonaId(P_ID)).willReturn(base);

            when(personaCardMapper.selectCardOptionsByCardId(base.get(0).getCardId())).thenReturn(null);
            when(personaCardMapper.selectCardOptionsByCardId(base.get(1).getCardId()))
                    .thenReturn(List.of(mock(CardOptionDTO.class)));

            PersonaCardResponseDTO res = service.getRecommendedCards(P_ID);

            assertEquals(2, res.getCards().size());
            for (PersonaCardDTO card : res.getCards()) {
                verify(card).setOptions(any()); // null 또는 리스트
            }
        }

        @Test
        @DisplayName("PC-008 셔플 무작위성 → 호출마다 순서 달라질 수 있음")
        void pc008() {
            given(personaCardMapper.selectPersonaNameById(P_ID)).willReturn("소비왕");
            List<PersonaCardDTO> base = mockCards(600, 5);
            given(personaCardMapper.selectCardsByPersonaId(P_ID)).willAnswer(inv -> new ArrayList<>(base));

            for (PersonaCardDTO card : base) {
                when(personaCardMapper.selectCardOptionsByCardId(card.getCardId()))
                        .thenReturn(Collections.emptyList());
            }

            Set<String> orders = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                PersonaCardResponseDTO res = service.getRecommendedCards(P_ID);
                String sig = res.getCards().stream()
                        .map(c -> String.valueOf(c.getCardId()))
                        .collect(Collectors.joining(","));
                orders.add(sig);
            }
            assertTrue(orders.size() > 1);
        }
    }

    // ---------------------- getPersonaIdByUserId ----------------------
    @Nested
    @DisplayName("getPersonaIdByUserId(userId)")
    class GetPersonaId {

        @Test
        @DisplayName("PC-011 정상 조회 → personaId 반환")
        void pc011() {
            UserVO user = mock(UserVO.class);
            when(user.getPersonaId()).thenReturn(7L);
            given(userMapper.findByUserId(100L)).willReturn(Optional.of(user));

            Long personaId = service.getPersonaIdByUserId(100L);
            assertEquals(7L, personaId);
        }

        @Test
        @DisplayName("PC-012 유저 없음 → RuntimeException")
        void pc012() {
            given(userMapper.findByUserId(100L)).willReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.getPersonaIdByUserId(100L));
            assertTrue(ex.getMessage().contains("persona_id"));
        }

        @Test
        @DisplayName("PC-013 personaId=null → RuntimeException")
        void pc013() {
            UserVO user = mock(UserVO.class);
            when(user.getPersonaId()).thenReturn(null);
            given(userMapper.findByUserId(100L)).willReturn(Optional.of(user));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.getPersonaIdByUserId(100L));
            assertTrue(ex.getMessage().contains("persona_id"));
        }
    }
}
