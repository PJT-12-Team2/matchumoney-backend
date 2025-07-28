package team2.pjt12.matchumoney.domain.saving.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;

import java.util.List;

@Mapper
public interface SavingAccountMapper {
    void deleteByUserIdAndFinId(@Param("userId") Long userId, @Param("finId") Long finId);

    void insertSavingAccount(SavingAccountVO savingAccountVO);

    MySavingProductResponseDTO getSavingAccount(@Param("id") Long id);

    List<SavingListItemResponseDTO> getRecommendSavingAccountList(@Param("period") String period, @Param("rate") Double rate);

    List<SavingListItemResponseDTO> getRecommendDefaultSavingAccountList();

    List<MySavingProductResponseDTO> getSavingAccountList(@Param("userId") Long userId);
}
