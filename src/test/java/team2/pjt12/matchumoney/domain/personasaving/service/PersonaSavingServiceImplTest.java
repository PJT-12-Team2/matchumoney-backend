// src/test/java/team2/pjt12/matchumoney/domain/personasaving/service/PersonaSavingServiceImplTest.java
package team2.pjt12.matchumoney.domain.personasaving.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;

import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingDTO;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingResponseDTO;
import team2.pjt12.matchumoney.domain.personasaving.mapper.PersonaSavingMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.*;

/**
 * 커버 범위:
 * - getRecommendedSaving(personaId): PSV-001 ~ PSV-007
 * - getPersonaIdByUserId(userId): PSV-011 ~ PSV-013
 */
@ExtendWith(MockitoExtension.class)
class PersonaSavingServiceImplTest {

    @Mock private PersonaSavingMapper personaSavingMapper;
    @Mock private UserMapper userMapper;

    @InjectMocks
    private PersonaSavingServiceImpl service;

    private static final Long P_ID = 5L;

    // ---- 테스트 헬퍼: 더미 DTO 생성 ----
    private PersonaSavingDTO dto(int id) {
        PersonaSavingDTO d = new PersonaSavingDTO();
        // 필요 시 세터 호출해 필드 채우세요.
        return d;
    }
    private List<PersonaSavingDTO> listOfN(int n, int startId) {
        return IntStream.range(0, n).mapToObj(i -> dto(startId + i)).collect(Collectors.toList());
    }

    // ---------------------- getRecommendedSaving ----------------------
    @Nested
    @DisplayName("getRecommendedSaving(personaId)")
    class RecommendByPersonaId {

        @Test
        @DisplayName("PSV-001 정상 추천 3건: 이름 존재, 후보 5건 → 셔플 후 최대 3건")
        void psv001() {
            given(personaSavingMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            List<PersonaSavingDTO> base = listOfN(5, 1);
            given(personaSavingMapper.selectSavingsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base)); // 호출마다 새 리스트

            PersonaSavingResponseDTO res = service.getRecommendedSaving(P_ID);

            assertEquals("절약가", res.getPersonaName());
            assertNotNull(res.getSavings());
            assertEquals(3, res.getSavings().size());
        }

        @Test
        @DisplayName("PSV-002 후보 2건: 그대로 2건 반환")
        void psv002() {
            given(personaSavingMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            List<PersonaSavingDTO> base = listOfN(2, 1);
            given(personaSavingMapper.selectSavingsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            PersonaSavingResponseDTO res = service.getRecommendedSaving(P_ID);

            assertEquals("절약가", res.getPersonaName());
            assertNotNull(res.getSavings());
            assertEquals(2, res.getSavings().size());
        }

        @Test
        @DisplayName("PSV-003 후보 없음: 빈 리스트 반환")
        void psv003() {
            given(personaSavingMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            given(personaSavingMapper.selectSavingsByPersonaId(P_ID))
                    .willReturn(Collections.emptyList());

            PersonaSavingResponseDTO res = service.getRecommendedSaving(P_ID);

            assertEquals("절약가", res.getPersonaName());
            assertNotNull(res.getSavings());
            assertTrue(res.getSavings().isEmpty());
        }

        @Test
        @DisplayName("PSV-004 이름 없음(null 허용), 후보 4건 → 3건 제한")
        void psv004() {
            given(personaSavingMapper.selectPersonaNameById(P_ID)).willReturn(null);
            List<PersonaSavingDTO> base = listOfN(4, 1);
            given(personaSavingMapper.selectSavingsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            PersonaSavingResponseDTO res = service.getRecommendedSaving(P_ID);

            assertNull(res.getPersonaName());
            assertNotNull(res.getSavings());
            assertEquals(3, res.getSavings().size());
        }

        @Test
        @DisplayName("PSV-005 리스트 null → Collections.shuffle(null)로 NPE")
        void psv005() {
            given(personaSavingMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            given(personaSavingMapper.selectSavingsByPersonaId(P_ID)).willReturn(null);

            assertThrows(NullPointerException.class, () -> service.getRecommendedSaving(P_ID));
        }

        @Test
        @DisplayName("PSV-006 셔플 무작위성: 반복 호출 시 순서 달라질 수 있음(수량=3 유지)")
        void psv006() {
            given(personaSavingMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            List<PersonaSavingDTO> base = listOfN(5, 1);
            given(personaSavingMapper.selectSavingsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            Set<String> orders = new HashSet<>();
            for (int i = 0; i < 10; i++) {
                PersonaSavingResponseDTO res = service.getRecommendedSaving(P_ID);
                assertEquals(3, res.getSavings().size());
                String sig = res.getSavings().stream().map(Object::toString).collect(Collectors.joining(","));
                orders.add(sig);
            }
            assertTrue(orders.size() > 1, "셔플 결과가 최소 2가지 이상이어야 함");
        }

        @Test
        @DisplayName("PSV-007 대량 후보(10건) → 항상 3건")
        void psv007() {
            given(personaSavingMapper.selectPersonaNameById(P_ID)).willReturn("절약가");
            List<PersonaSavingDTO> base = listOfN(10, 1);
            given(personaSavingMapper.selectSavingsByPersonaId(P_ID))
                    .willAnswer(inv -> new ArrayList<>(base));

            PersonaSavingResponseDTO res = service.getRecommendedSaving(P_ID);

            assertEquals(3, res.getSavings().size());
        }
    }

    // ---------------------- getPersonaIdByUserId ----------------------
    @Nested
    @DisplayName("getPersonaIdByUserId(userId)")
    class GetPersonaId {

        @Test
        @DisplayName("PSV-011 정상 조회: UserVO{personaId=7} → 7 반환")
        void psv011() {
            UserVO user = mock(UserVO.class);
            when(user.getPersonaId()).thenReturn(7L);
            given(userMapper.findByUserId(100L)).willReturn(Optional.of(user));

            Long personaId = service.getPersonaIdByUserId(100L);

            assertEquals(7L, personaId);
        }

        @Test
        @DisplayName("PSV-012 해당 유저 없음: Optional.empty() → RuntimeException")
        void psv012() {
            given(userMapper.findByUserId(100L)).willReturn(Optional.empty());

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.getPersonaIdByUserId(100L));
            assertTrue(ex.getMessage().contains("해당 유저의 persona_id를 찾을 수 없습니다."));
        }

        @Test
        @DisplayName("PSV-013 유저는 있으나 personaId=null → RuntimeException (현 구현 기준)")
        void psv013() {
            UserVO user = mock(UserVO.class);
            when(user.getPersonaId()).thenReturn(null); // map(...) → Optional.empty()
            given(userMapper.findByUserId(100L)).willReturn(Optional.of(user));

            RuntimeException ex = assertThrows(RuntimeException.class,
                    () -> service.getPersonaIdByUserId(100L));
            assertTrue(ex.getMessage().contains("해당 유저의 persona_id를 찾을 수 없습니다."));
        }
    }
}
