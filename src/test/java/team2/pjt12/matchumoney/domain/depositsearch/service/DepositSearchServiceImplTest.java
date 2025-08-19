package team2.pjt12.matchumoney.domain.depositsearch.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositOptionDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import team2.pjt12.matchumoney.domain.depositsearch.mapper.DepositSearchMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class DepositSearchServiceImplTest {

    @Mock
    private DepositSearchMapper depositSearchMapper;

    @InjectMocks
    private DepositSearchServiceImpl depositSearchService;

    private Long userId;
    private DepositSearchRequestDTO searchRequest;

    @BeforeEach
    void setUp() {
        userId = 1L;
        searchRequest = new DepositSearchRequestDTO();
    }

    @Test
    void searchDepositProducts_shouldReturnResultsWithOptions() {
        // given
        DepositSearchResponseDTO dto1 = createDepositItem(1L, "신한은행 예금", "SH001");
        DepositSearchResponseDTO dto2 = createDepositItem(2L, "국민은행 예금", "KB001");

        List<DepositSearchResponseDTO> mockList = Arrays.asList(dto1, dto2);

        when(depositSearchMapper.findAllDepositProducts(userId, null, null))
                .thenReturn(mockList);
        when(depositSearchMapper.findOptionsByProductId("SH001"))
                .thenReturn(List.of(new DepositOptionDTO("12", 2.1, 3.0)));
        when(depositSearchMapper.findOptionsByProductId("KB001"))
                .thenReturn(List.of(new DepositOptionDTO("24", 2.3, 3.2)));

        // when
        List<DepositSearchResponseDTO> result =
                depositSearchService.searchDepositProducts(userId, searchRequest);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("신한은행 예금", result.get(0).getKorCoNm());
        assertEquals(1, result.get(0).getDepositOptions().size());
        assertEquals("국민은행 예금", result.get(1).getKorCoNm());
        assertEquals(1, result.get(1).getDepositOptions().size());

        verify(depositSearchMapper).findAllDepositProducts(userId, null, null);
        verify(depositSearchMapper).findOptionsByProductId("SH001");
        verify(depositSearchMapper).findOptionsByProductId("KB001");
    }

    private DepositSearchResponseDTO createDepositItem(Long id, String name, String code) {
        DepositSearchResponseDTO dto = new DepositSearchResponseDTO();
        dto.setDepositProductId(id);
        dto.setKorCoNm(name);
        dto.setFinPrdtCd(code);
        return dto;
    }
}
