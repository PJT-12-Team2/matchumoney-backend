package team2.pjt12.matchumoney.domain.depositsearch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositOptionDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchRequestDTO;
import team2.pjt12.matchumoney.domain.depositsearch.dto.DepositSearchResponseDTO;
import java.util.List;

@Mapper
public interface DepositSearchMapper {
    List<DepositSearchResponseDTO> findAllDepositProducts(DepositSearchRequestDTO request);

    List<DepositOptionDTO> findOptionsByProductId(String finPrdtCd);
}
