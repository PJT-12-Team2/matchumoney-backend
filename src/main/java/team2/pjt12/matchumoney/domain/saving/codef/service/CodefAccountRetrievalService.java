package team2.pjt12.matchumoney.domain.saving.codef.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.saving.codef.CodefApiClient;
import team2.pjt12.matchumoney.domain.saving.codef.CodefConnectedIdProvider;
import team2.pjt12.matchumoney.domain.saving.codef.constant.CodefApiConstants;
import team2.pjt12.matchumoney.domain.saving.codef.mapper.CodefMapper;
import team2.pjt12.matchumoney.domain.saving.domain.DepositAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.mapper.SavingAccountMapper;
import team2.pjt12.matchumoney.global.exception.CodefApiException;
import team2.pjt12.matchumoney.global.exception.CodefApiSubError;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class CodefAccountRetrievalService {

    private final CodefApiClient codefApiClient;
    private final SavingAccountMapper savingAccountMapper;
    private final CodefMapper codefMapper;
    private final CodefConnectedIdProvider codefConnectedIdProvider;

    public List<MySavingProductResponseDTO> updateAccount(BankLoginRequestDTO requestDto) {
        Long userId = SecurityUtils.getCurrentUserId();
        String connectedId = codefMapper.getCodefConnectedIdByUserId(userId);
        codefConnectedIdProvider.updateAccountByConnectedId(requestDto, connectedId);
        //연결 로직 필요
        return savingAccountMapper.getSavingAccountList(userId);
    }

    public List<String> getOrganizationCodes(String connectedId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("connectedId", connectedId);

//        JsonNode res = codefApiClient.postJson(CodefApiConstants.ACCOUNT_GET_URL, params);
//        log.info(res.toString());
//        JsonNode list = res.path("data").path("organizationList");
        Long userId = SecurityUtils.getCurrentUser().getUserId();

        List<String> codes = codefMapper.selectOrganizationNamesByUserId(userId);
        return codes;
    }


    public List<JsonNode> retrieveAccountList(String connectedId, String bankCode) {
        // 1) 파라미터 구성
        HashMap<String, Object> params = new HashMap<>();
        params.put("connectedId", connectedId);
        params.put("organization", bankCode);

        // 2) API 호출 (경로는 반드시 "경로만": /v1/...)
        JsonNode res = codefApiClient.postJson(CodefApiConstants.ACCOUNT_LIST_URL, params);

        // 3) 결과 코드 검사
        String resultCode = res.path("result").path("code").asText();
        if (!"CF-00000".equals(resultCode)) {
            String message = res.path("result").path("message").asText();
            List<CodefApiSubError> subErrorList = new ArrayList<>();
            JsonNode errorList = res.path("data").path("errorList");
            if (errorList != null && errorList.isArray()) {
                for (JsonNode err : errorList) {
                    subErrorList.add(new CodefApiSubError(
                            err.path("code").asText(),
                            err.path("message").asText()
                    ));
                }
            }
            throw new CodefApiException(resultCode, message, subErrorList);
        }

        // 4) 계좌 배열 추출 (기관/상품 스펙에 맞춰 경로 확인)
        //    예: 적금/예금 통합 응답인 경우 resDepositTrust에 리스트가 담김
        JsonNode accountList = res.path("data").path("resDepositTrust");
        if (!accountList.isArray()) {
            log.warn("계좌 목록이 배열 형태가 아님 - 응답: {}", res.toPrettyString());
            return Collections.emptyList();
        }

        // 5) 사용자/기관 ID 준비
        Long userId = SecurityUtils.getCurrentUser().getUserId();
        Long finId;
        try {
            finId = Long.valueOf(bankCode);
        } catch (NumberFormatException e) {
            log.error("기관 코드 파싱 실패: {}", bankCode);
            throw new CustomException(ErrorCode.DATA_CONVERSION_FAILED);
        }

        // 6) 기존 예금/적금 정리(선택)
        //    동기화 정책에 따라 사전에 기존 레코드 삭제
        try {
            savingAccountMapper.deleteByUserIdAndFinId(userId, finId);          // 적금 테이블 정리
            savingAccountMapper.deleteDepositByUserIdAndFinId(userId, finId);   // 예금 테이블 정리
        } catch (Exception e) {
            log.error("기존 계좌 삭제 실패", e);
            throw new CustomException(ErrorCode.DB_SAVING_DELETE_FAILED);
        }

        // 7) 분기 저장: 적금은 반환용 리스트에 담고, 예금은 DB 저장
        List<JsonNode> savingAccounts = new ArrayList<>();
        for (JsonNode account : accountList) {
            // 기관 반환 필드명 확인: 아래 예시는 resAccountDeposit(예/적금 구분), resAccount(계좌번호)
            String depositType = account.path("resAccountDeposit").asText(); // "11"(예금), "12"/"14"(적금)
            if (CodefApiConstants.DEPOSIT_TYPE_SAVINGS.equals(depositType)
                    || CodefApiConstants.DEPOSIT_TYPE_SAVINGS2.equals(depositType)) {
                // ✅ 적금: 호출자에게 넘겨서 후속 거래내역 수집/적금테이블 저장에 사용
                savingAccounts.add(account);
            } else if (CodefApiConstants.DEPOSIT_TYPE_DEPOSIT.equals(depositType)) {
                // ✅ 예금: 바로 예금 테이블 저장
                try {
                    // 본인 프로젝트의 VO 생성자/매핑 로직에 맞게 사용
                    log.info("받아온 account: " + account);
                    DepositAccountVO deposit = new DepositAccountVO(account, userId, finId);
                    savingAccountMapper.insertDepositAccount(deposit);
                } catch (Exception e) {
                    log.warn("예금 계좌 저장 실패 - account: {}", account.path("resAccount").asText(), e);
                }
            } else {
                // 기타 유형이 있으면 필요 시 추가 분기
                log.debug("알 수 없는 예적금 유형: {}", depositType);
            }
        }

        log.info("✅ 적금 계좌 {}개, 예금 계좌는 DB 저장 완료", savingAccounts.size());
        return savingAccounts;
    }

    public JsonNode retrieveTransactionHistory(String connectedId, String bankCode, String accountNumber, String birthDate) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("connectedId", connectedId);
        params.put("organization", bankCode);
        params.put("account", accountNumber);
        if (birthDate != null && !birthDate.isBlank()) {
            params.put("birthDate", birthDate);
        }
        // 기간/정렬 등 추가 파라미터가 필요하면 여기에 확장
        params.put("startDate", CodefApiConstants.DEFAULT_START_DATE);

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
        String today = LocalDate.now().format(formatter);

        params.put("endDate", today);
        params.put("orderBy", "0");

        log.info("params:계좌 저장: " + params);
        return codefApiClient.postJson(CodefApiConstants.TRANSACTION_LIST_URL, params);
    }


    public void deleteConnectedId(String connectedId) {
        HashMap<String, Object> params = new HashMap<>();
        params.put("connectedId", connectedId);
        JsonNode res2 = codefApiClient.postJson(CodefApiConstants.ACCOUNT_GET_URL, params);
        params.put("accountList", res2.path("data").path("accountList"));

        JsonNode res = codefApiClient.postJson(CodefApiConstants.ACCOUNT_DELETE_URL, params);
        String code = res.path("result").path("code").asText();
        if (!"CF-00000".equals(code)) {
            String msg = res.path("result").path("message").asText();
            throw new CodefApiException(code, msg, Collections.emptyList());
        }
    }
}
