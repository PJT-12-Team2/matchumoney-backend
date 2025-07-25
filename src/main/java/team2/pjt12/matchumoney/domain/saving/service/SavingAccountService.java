package team2.pjt12.matchumoney.domain.saving.service;

import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;

import java.util.List;

public interface SavingAccountService {
    //controller 에서 사용하는 service
    List<MySavingProductResponseDTO> getSavingAccount();
    List<MySavingProductResponseDTO> retrieveAccounts(BankLoginRequestDTO requestDto);
}
