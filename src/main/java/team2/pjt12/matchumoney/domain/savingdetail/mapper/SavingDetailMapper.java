package team2.pjt12.matchumoney.domain.savingdetail.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingOptionDTO;

import java.util.List;

@Mapper
public interface SavingDetailMapper {
    SavingDetailResponseDTO findSavingProductById(@Param("id") Long id);
    List<SavingOptionDTO> findOptionsByProductId(@Param("finPrdtCd") String finPrdtCd);
}