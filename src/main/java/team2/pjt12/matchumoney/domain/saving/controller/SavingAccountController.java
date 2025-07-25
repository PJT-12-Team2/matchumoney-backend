package team2.pjt12.matchumoney.domain.saving.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.service.SavingAccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class SavingAccountController implements SavingAccountApi{

    private final SavingAccountService savingAccountService;

    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccount(@RequestBody BankLoginRequestDTO requestDto) {
        List<MySavingProductResponseDTO> accountList = savingAccountService.retrieveAccounts(requestDto);
        return ResponseEntity.ok(accountList); // JSON 형식으로 반환
    }

    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> getMySavingAccount() {
        List<MySavingProductResponseDTO> accountList = savingAccountService.getSavingAccountList();
        return ResponseEntity.ok(accountList); // JSON 형식으로 반환
    }

}
