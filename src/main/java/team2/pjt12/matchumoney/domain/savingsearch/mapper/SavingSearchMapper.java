package team2.pjt12.matchumoney.domain.savingsearch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositOptionDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingOptionDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchRequestDTO;
import team2.pjt12.matchumoney.domain.savingsearch.dto.SavingSearchResponseDTO;

import java.util.List;

@Mapper
public interface SavingSearchMapper {
    List<SavingSearchResponseDTO> findAllSavingProducts(
            @Param("userId") Long userId,
            @Param("korCoNm") String korCoNm,
            @Param("maxLimit") Integer maxLimit
    );

    List<SavingOptionDTO> findOptionsByProductId(@Param("finPrdtCd") String finPrdtCd);
}
