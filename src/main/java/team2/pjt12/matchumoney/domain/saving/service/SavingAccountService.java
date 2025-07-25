package team2.pjt12.matchumoney.domain.saving.service;

import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;

import java.util.List;

//controller 에서 사용하는 service
public interface SavingAccountService {
    List<MySavingProductResponseDTO> getSavingAccountList();

    List<MySavingProductResponseDTO> retrieveAccounts(BankLoginRequestDTO requestDto);

    List<SavingListItemResponseDTO> getUserRecommendedSavingAccounts(Long id);
}
