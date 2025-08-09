package team2.pjt12.matchumoney.domain.compare.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.compare.dto.*;

import java.util.List;

@Mapper
public interface CompareProductMapper {

    List<CompareSavingResponseDTO> selectSavingProductsByIds(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    List<RateDTO> selectRatesBySavingProductIds(@Param("ids") List<Long> ids);

    List<CompareDepositResponseDTO> selectDepositProductsByIds(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    List<RateDTO> selectRatesByDepositProductIds(@Param("ids") List<Long> ids);

    List<CompareCardResponseDTO> selectCardProductsByIds(@Param("ids") List<Long> ids, @Param("userId") Long userId);

    List<CardOptionDTO> selectCardOptionsByCardIds(@Param("ids") List<Long> ids);

    List<SearchProductResponseDTO> selectAllSavingProducts();

    List<SearchProductResponseDTO> selectAllDepositProducts();

    List<SearchProductResponseDTO> selectAllCardProducts();
}
