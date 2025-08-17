package team2.pjt12.matchumoney.domain.education.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import team2.pjt12.matchumoney.domain.education.dto.EduMovieResponseDTO;
import team2.pjt12.matchumoney.domain.education.mapper.EduMapper;

import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

class EduServiceImplTest {

    @Mock
    private EduMapper eduMapper;

    @InjectMocks
    private EduServiceImpl eduService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getEduMovieList_success() {
        // given
        Long offset = 0L;
        int pageSize = 10;

        EduMovieResponseDTO dto1 = new EduMovieResponseDTO(1L, "금융사기 예방", "보이스피싱 설명", "금융감독원", "https://youtube.com/1");
        EduMovieResponseDTO dto2 = new EduMovieResponseDTO(2L, "불법사금융 대처법", "불법사금융 안내", "금융감독원", "https://youtube.com/2");

        List<EduMovieResponseDTO> mockList = Arrays.asList(dto1, dto2);
        when(eduMapper.getEduMovieList(offset, pageSize)).thenReturn(mockList);

        // when
        List<EduMovieResponseDTO> result = eduService.getEduMovieList(offset, pageSize);

        // then
        assertThat(result).hasSize(2);
        assertThat(result.get(0).getId()).isEqualTo(1L);
        assertThat(result.get(0).getTitle()).isEqualTo("금융사기 예방");
        assertThat(result.get(0).getSmrtnCntnt()).contains("보이스피싱");
        assertThat(result.get(0).getInstitution()).isEqualTo("금융감독원");
        assertThat(result.get(0).getLink()).isEqualTo("https://youtube.com/1");

        assertThat(result.get(1).getId()).isEqualTo(2L);
        assertThat(result.get(1).getTitle()).isEqualTo("불법사금융 대처법");

        verify(eduMapper, times(1)).getEduMovieList(offset, pageSize);
    }
}
