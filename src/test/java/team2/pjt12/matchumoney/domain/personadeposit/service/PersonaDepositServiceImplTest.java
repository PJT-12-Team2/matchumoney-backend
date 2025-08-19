// src/test/java/team2/pjt12/matchumoney/domain/personadeposit/service/PersonaDepositServiceImplTest.java
package team2.pjt12.matchumoney.domain.personadeposit.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.RepeatedTest;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.mapper.PersonaDepositMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * 시나리오 커버:
 * PD-001 ~ PD-007: getRecommendedDeposit(personaId)
 * PD-011 ~ PD-013: getPersonaIdByUserId(userId)
 */
@ExtendWith(MockitoExtension.class)
class PersonaDepositServiceImplTest {

    @Mock private PersonaDepositMapper personaDepositMapper;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private PersonaDepositServiceImpl service;

    private static final Long P_ID = 5L;

    // ---- 테스트 헬퍼: 더미 DTO 생성 ----
    private PersonaDepositDTO dto(int id) {
        // 필드가 무엇이든 상관없고, 목록/순서만 검증하므로 최소 생성
        PersonaDepositDTO d = new PersonaDepositDTO();
        // 필요 시 세터가 있으면 d.setXXX(...) 써도 됨
        return d;
    }
    private List<PersonaDepositDTO> listOfN(int n, int startId) {
        return IntStream.range(0, n).mapToObj(i -> dto(startId + i)).collect(Collectors.toList());
    }

    @BeforeEach
    void init() {
        // 공통 초기화 필요 시 여기에
    }

    // ---------------------- getRecommendedDeposit ----------------------
    @Nested
    @DisplayName("getRecommendedDeposit(personaId)")
    class RecommendByPersonaId {

        @Test
        @DisplayName("PD-001 정상 추천 3건: 이름 존재, 5건 후보 → 셔플 후 3건 제한")
        void pd001() {
            given(personaDepositMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            // 셔플이 내부에서 원본 리스트를 바꾸므로, 각 호출마다 '새 리스트'를 반환하도록 설정
            List<PersonaDepositDTO> base = listOfN(5, 1);
            given(personaDepositMapper.selectDepositsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            PersonaDepositResponseDTO res = service.getRecommendedDeposit(P_ID);

            assertEquals("절약가", res.getPersonaName());
            assertNotNull(res.getDeposits());
            assertEquals(3, res.getDeposits().size());
        }

        @Test
        @DisplayName("PD-002 후보 2건: 그대로 2건 반환")
        void pd002() {
            given(personaDepositMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            List<PersonaDepositDTO> base = listOfN(2, 1);
            given(personaDepositMapper.selectDepositsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            PersonaDepositResponseDTO res = service.getRecommendedDeposit(P_ID);

            assertEquals("절약가", res.getPersonaName());
            assertNotNull(res.getDeposits());
            assertEquals(2, res.getDeposits().size());
        }

        @Test
        @DisplayName("PD-003 후보 없음: 빈 리스트 반환")
        void pd003() {
            given(personaDepositMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            given(personaDepositMapper.selectDepositsByPersonaId(P_ID))
                    .willReturn(Collections.emptyList());

            PersonaDepositResponseDTO res = service.getRecommendedDeposit(P_ID);

            assertEquals("절약가", res.getPersonaName());
            assertNotNull(res.getDeposits());
            assertTrue(res.getDeposits().isEmpty());
        }

        @Test
        @DisplayName("PD-004 페르소나 이름 없음(null 허용), 후보 4건 → 3건 제한")
        void pd004() {
            given(personaDepositMapper.selectPersonaNameById(P_ID)).willReturn(null);
            List<PersonaDepositDTO> base = listOfN(4, 1);
            given(personaDepositMapper.selectDepositsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            PersonaDepositResponseDTO res = service.getRecommendedDeposit(P_ID);

            assertNull(res.getPersonaName());
            assertNotNull(res.getDeposits());
            assertEquals(3, res.getDeposits().size());
        }

        @Test
        @DisplayName("PD-005 예금 리스트가 null → Collections.shuffle(null)로 NPE")
        void pd005() {
            given(personaDepositMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            given(personaDepositMapper.selectDepositsByPersonaId(P_ID)).willReturn(null);

            assertThrows(NullPointerException.class, () -> service.getRecommendedDeposit(P_ID));
        }

        @Test
        @DisplayName("PD-006 셔플 동작: 반복 호출 시 순서가 달라질 수 있음(내용 수량=3 유지)")
        void pd006() {
            given(personaDepositMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            List<PersonaDepositDTO> base = listOfN(5, 1);
            given(personaDepositMapper.selectDepositsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base)); // 호출마다 새 리스트

            // 여러 번 호출하며 결과 순서를 문자열로 저장해 중복 여부 확인
            Set<String> seenOrders = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                PersonaDepositResponseDTO res = service.getRecommendedDeposit(P_ID);
                assertEquals(3, res.getDeposits().size());
                String orderSig = res.getDeposits().stream()
                        .map(Object::toString).collect(Collectors.joining(","));
                seenOrders.add(orderSig);
            }
            // 매우 드물게 전부 동일할 수도 있으나, 10회면 순서가 보통 달라진다.
            assertTrue(seenOrders.size() > 1, "셔플 결과가 최소 2가지 이상이어야 함");
        }

        @Test
        @DisplayName("PD-007 대량 후보(10건) → 항상 3건으로 제한")
        void pd007() {
            given(personaDepositMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            List<PersonaDepositDTO> base = listOfN(10, 1);
            given(personaDepositMapper.selectDepositsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            PersonaDepositResponseDTO res = service.getRecommendedDeposit(P_ID);

            assertEquals(3, res.getDeposits().size());
        }
    }

    // ---------------------- getPersonaIdByUserId ----------------------
    @Nested
    @DisplayName("getPersonaIdByUserId(userId)")
    class GetPersonaId {

        @Test
        @DisplayName("PD-011 정상 조회: UserVO{personaId=7} → 7 반환")
        void pd011() {
            UserVO user = mock(UserVO.class);
            when(user.getPersonaId()).thenReturn(7L);
            given(userMapper.findByUserId(100L)).willReturn(Optional.of(user));

            Long personaId = service.getPersonaIdByUserId(100L);

            assertEquals(7L, personaId);
        }

        @Test
        @DisplayName("PD-012 해당 유저 없음: Optional.empty() → RuntimeException")
        void pd012() {
            given(userMapper.findByUserId(100L)).willReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.getPersonaIdByUserId(100L));
            assertTrue(ex.getMessage().contains("해당 유저의 persona_id를 찾을 수 없습니다."));
        }

        @Test
        @DisplayName("PD-013 유저는 있으나 personaId가 null → RuntimeException (현 구현 기준)")
        void pd013() {
            UserVO user = mock(UserVO.class);
            when(user.getPersonaId()).thenReturn(null); // map(...) 결과 Optional.empty()
            given(userMapper.findByUserId(100L)).willReturn(Optional.of(user));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.getPersonaIdByUserId(100L));
            assertTrue(ex.getMessage().contains("해당 유저의 persona_id를 찾을 수 없습니다."));
        }
    }
}
