package team2.pjt12.matchumoney.domain.savingsearch.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingOptionDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.mapper.SavingSearchMapper;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class SavingSearchServiceImplTest {

    @Mock
    private SavingSearchMapper savingSearchMapper;

    @InjectMocks
    private SavingSearchServiceImpl savingSearchService;

    private Long userId;
    private SavingSearchRequestDTO searchRequest;

    @BeforeEach
    void setUp() {
        userId = 1L;
        searchRequest = new SavingSearchRequestDTO();
    }

    @Test
    void searchSavingProducts_shouldReturnResultsWithOptions() {
        // given
        SavingSearchResponseDTO dto1 = createSavingItem(1L, "카카오뱅크 적금", "KA001");
        SavingSearchResponseDTO dto2 = createSavingItem(2L, "농협 적금", "NH001");

        List<SavingSearchResponseDTO> mockList = Arrays.asList(dto1, dto2);

        when(savingSearchMapper.findAllSavingProducts(userId, null, null))
                .thenReturn(mockList);
        when(savingSearchMapper.findOptionsByProductId("KA001"))
                .thenReturn(List.of(new SavingOptionDTO("12", 2.0, 3.0)));
        when(savingSearchMapper.findOptionsByProductId("NH001"))
                .thenReturn(List.of(new SavingOptionDTO("24", 2.5, 3.2)));

        // when
        List<SavingSearchResponseDTO> result =
                savingSearchService.searchSavingProducts(userId, searchRequest);

        // then
        assertNotNull(result);
        assertEquals(2, result.size());
        assertEquals("카카오뱅크 적금", result.get(0).getKorCoNm());
        assertEquals(1, result.get(0).getSavingOptions().size());
        assertEquals("농협 적금", result.get(1).getKorCoNm());
        assertEquals(1, result.get(1).getSavingOptions().size());

        verify(savingSearchMapper).findAllSavingProducts(userId, null, null);
        verify(savingSearchMapper).findOptionsByProductId("KA001");
        verify(savingSearchMapper).findOptionsByProductId("NH001");
    }

    private SavingSearchResponseDTO createSavingItem(Long id, String name, String code) {
        SavingSearchResponseDTO dto = new SavingSearchResponseDTO();
        dto.setSavingProductId(id);
        dto.setKorCoNm(name);
        dto.setFinPrdtCd(code);
        return dto;
    }
}
