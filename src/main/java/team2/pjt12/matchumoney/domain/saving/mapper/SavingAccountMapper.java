package team2.pjt12.matchumoney.domain.saving.mapper;


import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;

import java.util.List;

@Mapper
public interface SavingAccountMapper {
    void deleteByUserIdAndFinId(@Param("userId") Long userId, @Param("finId") Long finId);
    void insertSavingAccount(SavingAccountVO savingAccountVO);

    List<MySavingProductResponseDTO> getSavingAccountList(@Param("userId") Long userId);
}
