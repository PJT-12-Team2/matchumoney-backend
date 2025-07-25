package team2.pjt12.matchumoney.domain.savingsearch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingOptionDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;

import java.util.List;

@Mapper
public interface SavingSearchMapper {
    List<SavingSearchResponseDTO> findAllSavingProducts(SavingSearchRequestDTO request);

    List<SavingOptionDTO> findOptionsByProductId(String finPrdtCd);
}
