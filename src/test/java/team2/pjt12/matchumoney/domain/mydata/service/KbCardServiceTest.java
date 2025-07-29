package team2.pjt12.matchumoney.domain.mydata.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.mydata.mapper.KbCardMapper;
import team2.pjt12.matchumoney.domain.mydata.util.KBCardApiUtil;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.BDDMockito.*;

@ExtendWith(MockitoExtension.class)
@DisplayName("KbCardService 카드 리스트 테스트")
class KbCardServiceTest {

    @Mock
    private KbCardMapper kbCardMapper;

    @InjectMocks
    private KbCardServiceImpl kbCardService;

    private Long testUserId;
    private List<CardHoldingVO> mockCardList;

    @BeforeEach
    void setUp() {
        testUserId = 1L;
        
        // 테스트용 카드 데이터 생성
        CardHoldingVO card1 = new CardHoldingVO();
        card1.setHoldingId(1L);
        card1.setCardId(146);
        card1.setFinId(1234567890L);
        card1.setCardName("KB국민 The Easy카드");
        card1.setResCardNo("1234-****-****-5678");
        card1.setResCardType("신용");
        card1.setResState("정상");
        card1.setUserId(testUserId);
        
        CardHoldingVO card2 = new CardHoldingVO();
        card2.setHoldingId(2L);
        card2.setCardId(null); // 매칭되지 않은 카드
        card2.setFinId(9876543210L);
        card2.setCardName("알 수 없는 카드");
        card2.setResCardNo("9876-****-****-4321");
        card2.setResCardType("체크");
        card2.setResState("정상");
        card2.setUserId(testUserId);
        
        mockCardList = Arrays.asList(card1, card2);
    }

    @Test
    @DisplayName("사용자 카드 목록 조회 - 성공")
    void getCards_Success() {
        // given
        given(kbCardMapper.selectKbCardByUserId(testUserId)).willReturn(mockCardList);

        // when
        List<CardHoldingVO> result = kbCardService.getCards(testUserId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getCardName()).isEqualTo("KB국민 The Easy카드");
        assertThat(result.get(0).getCardId()).isEqualTo(146); // 매칭된 카드
        assertThat(result.get(1).getCardName()).isEqualTo("알 수 없는 카드");
        assertThat(result.get(1).getCardId()).isNull(); // 매칭되지 않은 카드
        
        verify(kbCardMapper).selectKbCardByUserId(testUserId);
    }

    @Test
    @DisplayName("사용자 카드 목록 조회 - 빈 목록")
    void getCards_EmptyList() {
        // given
        given(kbCardMapper.selectKbCardByUserId(testUserId)).willReturn(Arrays.asList());

        // when
        List<CardHoldingVO> result = kbCardService.getCards(testUserId);

        // then
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();
        
        verify(kbCardMapper).selectKbCardByUserId(testUserId);
    }

    @Test
    @DisplayName("매칭된 카드와 매칭되지 않은 카드 구분 테스트")
    void cardMatching_StatusTest() {
        // given
        given(kbCardMapper.selectKbCardByUserId(testUserId)).willReturn(mockCardList);

        // when
        List<CardHoldingVO> result = kbCardService.getCards(testUserId);

        // then
        // 매칭된 카드 확인
        CardHoldingVO matchedCard = result.stream()
                .filter(card -> card.getCardId() != null)
                .findFirst()
                .orElse(null);
        assertThat(matchedCard).isNotNull();
        assertThat(matchedCard.getCardName()).isEqualTo("KB국민 The Easy카드");
        
        // 매칭되지 않은 카드 확인
        CardHoldingVO unmatchedCard = result.stream()
                .filter(card -> card.getCardId() == null)
                .findFirst()
                .orElse(null);
        assertThat(unmatchedCard).isNotNull();
        assertThat(unmatchedCard.getCardName()).isEqualTo("알 수 없는 카드");
    }
}