package team2.pjt12.matchumoney.domain.saving.service;

import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;

import java.util.List;

//controller 에서 사용하는 service
public interface SavingAccountService {
    List<MySavingProductResponseDTO> getSavingAccountList();

    List<MySavingProductResponseDTO> retrieveAccounts(BankLoginRequestDTO requestDto);

    //은행에서 계좌 정보 동기화
    @Transactional
    List<MySavingProductResponseDTO> retrieveAccountsPre();

    List<SavingListItemResponseDTO> getUserRecommendedSavingAccounts(Long id, int page, int size);

    //정보 제거
    String deleteConnectedId();
}
