package team2.pjt12.matchumoney.domain.saving.codef.controller;

import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.saving.codef.CodefConnectedIdProvider;
import team2.pjt12.matchumoney.domain.saving.codef.mapper.CodefMapper;
import team2.pjt12.matchumoney.domain.saving.codef.service.CodefAccountRetrievalService;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.service.SavingAccountService;

import java.util.Collections;
import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@RestController
@RequiredArgsConstructor
public class CodefController implements CodefApi {

    private final SavingAccountService savingAccountService;
    private final CodefAccountRetrievalService codefAccountRetrievalService;
    private final CodefMapper codefMapper;

    private final CodefConnectedIdProvider codefConnectedIdProvider;

    /**
     * (통합) 계정 추가/수정 + 동기화
     * - Connected ID 없으면 생성
     * - 있으면 계정 추가
     * - 이후 사전연결 기관 기준 재동기화
     */
    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> updateConnectedId(
            @ApiParam(value = "은행 로그인 요청 DTO") @RequestBody BankLoginRequestDTO requestDto) {

        // 통합 처리: 생성/추가 + 동기화
        List<MySavingProductResponseDTO> accountList = codefAccountRetrievalService.updateAccount(requestDto);
        return ResponseEntity.ok(accountList);
    }

    /**
     * 단건 동기화 (요청 DTO의 은행 코드 기준)
     */
    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccount(@RequestBody BankLoginRequestDTO requestDto) {
        List<MySavingProductResponseDTO> accountList = savingAccountService.retrieveAccounts(requestDto);
        return ResponseEntity.ok(accountList);
    }

    /**
     * 사전 연결된 모든 기관에 대해 동기화
     */
    @Override
    public ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccountPre() {
        List<MySavingProductResponseDTO> accountList = savingAccountService.retrieveAccountsPre();
        return ResponseEntity.ok(accountList);
    }

    /**
     * 현재 사용자 Connected ID 조회 (없으면 빈 리스트)
     * 기존 시그니처를 유지하기 위해 List<String>으로 반환
     */
    @Override
    public ResponseEntity<List<String>> getConnectedIdList() {
        Long userId = getCurrentUser().getUserId();
        String connectedId = codefMapper.getCodefConnectedIdByUserId(userId);
        if (connectedId == null || connectedId.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(Collections.singletonList(connectedId));
    }

    /**
     * Connected ID 삭제
     */
    @Override
    public ResponseEntity<String> deleteConnectedId() {
        return ResponseEntity.ok(savingAccountService.deleteConnectedId());
    }

    /**
     * 현재 사용자 Connected ID에 연결된 은행(organization) 코드 목록
     */
    @Override
    public ResponseEntity<List<String>> getBanksByConnectedId() {
        Long userId = getCurrentUser().getUserId();
        String connectedId = codefMapper.getCodefConnectedIdByUserId(userId);
        if (connectedId == null || connectedId.isBlank()) {
            return ResponseEntity.ok(Collections.emptyList());
        }
        return ResponseEntity.ok(codefAccountRetrievalService.getOrganizationCodes(connectedId));
    }
}
