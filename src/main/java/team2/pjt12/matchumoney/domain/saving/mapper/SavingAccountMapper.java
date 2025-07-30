package team2.pjt12.matchumoney.domain.saving.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.saving.domain.DepositAccountVO;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;

import java.util.List;

@Mapper
public interface SavingAccountMapper {
    void deleteByUserIdAndFinId(@Param("userId") Long userId, @Param("finId") Long finId);

    void insertSavingAccount(SavingAccountVO savingAccountVO);

    void deleteDepositByUserIdAndFinId(@Param("userId") Long userId, @Param("finId") Long finId);

    void insertDepositAccount(DepositAccountVO depositAccountVO);

    MySavingProductResponseDTO getSavingAccount(@Param("id") Long id);

    List<SavingListItemResponseDTO> getRecommendSavingAccountList(@Param("period") String period, @Param("rate") Double rate, @Param("user_id") Long userId);

    List<SavingListItemResponseDTO> getRecommendDefaultSavingAccountList(@Param("user_id") Long userId);

    List<MySavingProductResponseDTO> getSavingAccountList(@Param("userId") Long userId);
}
