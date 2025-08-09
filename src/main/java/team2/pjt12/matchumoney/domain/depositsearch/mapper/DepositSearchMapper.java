package team2.pjt12.matchumoney.domain.depositsearch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositOptionDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import java.util.List;

@Mapper
public interface DepositSearchMapper {

    List<DepositSearchResponseDTO> findAllDepositProducts(
            @Param("userId") Long userId,
            @Param("korCoNm") String korCoNm,
            @Param("maxLimit") Integer maxLimit
    );

    List<DepositOptionDTO> findOptionsByProductId(@Param("finPrdtCd") String finPrdtCd);
}
