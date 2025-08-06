package team2.pjt12.matchumoney.domain.depositdetail.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositOptionDTO;

import java.util.List;

@Mapper
public interface DepositDetailMapper {
    DepositDetailResponseDTO findDepositProductById(@Param("id") Long id);
    List<DepositOptionDTO> findOptionsByProductId(@Param("productId") Long productId);
}