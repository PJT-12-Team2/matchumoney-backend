package team2.pjt12.matchumoney.domain.education.service;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.education.mapper.WebtoonMapper;

import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class WebtoonServiceImplTest {

    @Mock WebtoonMapper webtoonMapper;
    @InjectMocks WebtoonServiceImpl webtoonService;

    @Test
    @DisplayName("DB 오류 시 '웹툰 목록 조회에 실패했습니다.' 메시지를 포함한 예외 발생")
    void shouldThrowRuntimeException_whenDbErrorOccursOnFindAll() {
        // given
        when(webtoonMapper.findWebtoonMain())
                .thenThrow(new RuntimeException("DB connection fail"));

        // when & then
        assertThatThrownBy(() -> webtoonService.getAllWebtoons())
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("웹툰 목록 조회에 실패했습니다.");

        verify(webtoonMapper, times(1)).findWebtoonMain();
        verifyNoMoreInteractions(webtoonMapper);
    }
}
