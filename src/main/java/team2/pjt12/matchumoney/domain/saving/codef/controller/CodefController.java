package team2.pjt12.matchumoney.domain.saving.codef.controller;

import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.saving.codef.service.CodefAccountRetrievalService;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.service.SavingAccountService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class CodefController implements CodefApi {

    private final SavingAccountService savingAccountService;
    private final CodefAccountRetrievalService codefAccountRetrievalService;

    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> updateConnectedId(@ApiParam(value = "은행 로그인 요청 DTO") @RequestBody BankLoginRequestDTO requestDto) {
        codefAccountRetrievalService.updateConnectedId(requestDto);
        List<MySavingProductResponseDTO> accountList = savingAccountService.retrieveAccountsPre();
        return ResponseEntity.ok(accountList); // JSON 형식으로 반환
    }

    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccount(@RequestBody BankLoginRequestDTO requestDto) {
        List<MySavingProductResponseDTO> accountList = savingAccountService.retrieveAccounts(requestDto);
        return ResponseEntity.ok(accountList); // JSON 형식으로 반환
    }

    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccountPre() {
        List<MySavingProductResponseDTO> accountList = savingAccountService.retrieveAccountsPre();
        return ResponseEntity.ok(accountList); // JSON 형식으로 반환
    }

    @Override
    public ResponseEntity<List<String>> getConnectedIdList() {
        codefAccountRetrievalService.getConnectedIdList();
        return null;
    }

    @Override
    public ResponseEntity<String> deleteConnectedId() {
        return ResponseEntity.ok(savingAccountService.deleteConnectedId());
    }

    @Override
    public ResponseEntity<List<String>> getBanksByConnectedId() {

        return ResponseEntity.ok(codefAccountRetrievalService.getBanksByConnectedId());
    }
}
