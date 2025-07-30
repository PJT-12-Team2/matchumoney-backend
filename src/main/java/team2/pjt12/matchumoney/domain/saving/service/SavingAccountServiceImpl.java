// 6. 개선된 메인 서비스
package team2.pjt12.matchumoney.domain.saving.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.saving.codef.CodefAccountRetrievalService;
import team2.pjt12.matchumoney.domain.saving.codef.CodefApiClient;
import team2.pjt12.matchumoney.domain.saving.codef.CodefApiConstants;
import team2.pjt12.matchumoney.domain.saving.codef.CodefConnectedIdProvider;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;
import team2.pjt12.matchumoney.domain.saving.mapper.SavingAccountMapper;
import team2.pjt12.matchumoney.domain.saving.util.SavingAccountConverter;
import team2.pjt12.matchumoney.global.exception.CodefApiException;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.ArrayList;
import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingAccountServiceImpl implements SavingAccountService {

    private final SavingAccountMapper savingAccountMapper;
    private final CodefApiClient codefApiClient;
    private final CodefConnectedIdProvider codefConnectedIdProvider;
    private final CodefAccountRetrievalService codefAccountRetrievalService;
    private final SavingAccountConverter dataTransformService;


    //사용자 적금 계좌 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MySavingProductResponseDTO> getSavingAccountList() {
        Long userId = getCurrentUser().getUserId();
        log.info("📋 적금 계좌 목록 조회 - 사용자ID: {}", userId);

        return savingAccountMapper.getSavingAccountList(userId);
    }


    //은행에서 계좌 정보 동기화
    @Transactional
    @Override
    public List<MySavingProductResponseDTO> retrieveAccounts(BankLoginRequestDTO requestDto) {
        Long userId = getCurrentUser().getUserId();
        log.info("🏦 은행 계좌 동기화 시작 - 사용자ID: {}", userId);

        // 1. Access Token 발급
        String accessToken = codefApiClient.getAccessToken();


        // 2. Connected ID 생성

        String connectedId;
        try {
            connectedId = codefConnectedIdProvider.createConnectedId(
                    accessToken,
                    requestDto.getId(),
                    requestDto.getPassword(),
                    CodefApiConstants.ORG_CODE_KB
            );
        } catch (CustomException e) {
            log.error("Connected ID 생성 실패 - {}", e.getErrorCode().getMessage());
            throw e;
        }

        // 3. 계좌 목록 조회 및 저장
        processAccountSynchronization(accessToken, connectedId, userId);

        // 4. 동기화된 계좌 목록 반환
        List<MySavingProductResponseDTO> result = savingAccountMapper.getSavingAccountList(userId);
        log.info("✅ 계좌 동기화 완료 - {}개 계좌", result.size());

        return result;
    }

    //계좌 동기화 처리
    @Transactional(rollbackFor = Exception.class)
    public void processAccountSynchronization(String accessToken, String connectedId, Long userId) {
        Long finId;
        try {
            finId = Long.parseLong(CodefApiConstants.ORG_CODE_KB);
        } catch (NumberFormatException e) {
            log.error("금융기관 코드 파싱 실패: {}", CodefApiConstants.ORG_CODE_KB);
            throw new CustomException(ErrorCode.DATA_CONVERSION_FAILED);
        }

        try {
            savingAccountMapper.deleteByUserIdAndFinId(userId, finId);
            log.info("기존 적금 계좌 삭제 완료 - 사용자ID: {}, 금융기관ID: {}", userId, finId);
        } catch (Exception e) {
            log.error("적금 계좌 삭제 실패", e);
            throw new CustomException(ErrorCode.DB_SAVING_DELETE_FAILED);
        }

        List<JsonNode> savingAccounts;
        try {
            savingAccounts = codefAccountRetrievalService.retrieveAccountList(
                    accessToken, connectedId, CodefApiConstants.ORG_CODE_KB
            );
        } catch (CodefApiException e) {
            log.error("Codef API 실패 - code: {}, message: {}", e.getCode(), e.getMessage());
            throw e; // GlobalExceptionHandler에서 응답 내려감
        }

        int processedCount = 0;
        List<String> failedAccounts = new ArrayList<>();

        for (JsonNode account : savingAccounts) {
            String accountNumber = account.path("resAccount").asText();

            try {
                if (processTransactionHistory(accessToken, connectedId, accountNumber, userId, finId)) {
                    processedCount++;
                } else {
                    failedAccounts.add(accountNumber);
                }
            } catch (Exception e) {
                log.warn("거래내역 처리 실패 - accountNumber: {}, error: {}", accountNumber, e.getMessage());
                failedAccounts.add(accountNumber);
            }
        }

        log.info("✅ 계좌 처리 완료 - 총 {}개 중 {}개 성공", savingAccounts.size(), processedCount);

        if (!failedAccounts.isEmpty()) {
            log.warn("❗ 실패한 계좌 목록: {}", failedAccounts);
            // 경우에 따라 프론트에 실패 목록까지 전달하는 DTO도 구성 가능
        }
    }


    //거래내역 처리
    private boolean processTransactionHistory(String accessToken, String connectedId,
                                              String accountNumber, Long userId, Long finId) {

        JsonNode transactionData = codefAccountRetrievalService.retrieveTransactionHistory(
                accessToken, connectedId, CodefApiConstants.ORG_CODE_KB, accountNumber
        );

        SavingAccountVO vo = dataTransformService.transformToVO(transactionData, userId, finId);
        
        try {
            savingAccountMapper.insertSavingAccount(vo);
        } catch (Exception e) {
            log.error("적금 계좌 저장 실패 - 계좌번호: {}", accountNumber, e);
            throw new CustomException(ErrorCode.DB_SAVING_INSERT_FAILED);
        }
        log.info("✅ 적금 계좌 저장 완료 - 계좌번호: {}", accountNumber);
        return true;
    }

    //내 계좌에 대한 추천 리스트
    @Override
    public List<SavingListItemResponseDTO> getUserRecommendedSavingAccounts(Long id) {
        Long userId = getCurrentUser().getUserId();
        if (id == -1) {
            return savingAccountMapper.getRecommendDefaultSavingAccountList(userId);
        }
        MySavingProductResponseDTO mySavingProduct;
        try {
            mySavingProduct = savingAccountMapper.getSavingAccount(id);
        } catch (Exception e) {
            log.error("적금 계좌 단건 조회 실패 - id: {}", id, e);
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        if (mySavingProduct == null) {
            log.warn("해당 ID로 조회된 적금 계좌가 없습니다. id={}", id);
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }
        if (mySavingProduct.getUser_id() != userId) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
        }
        log.info(String.valueOf(mySavingProduct));
        String period = mySavingProduct.getPeriod();

        double rate;
        try {
            rate = Double.parseDouble(mySavingProduct.getRate());
        } catch (NumberFormatException e) {
            log.error("적금 금리 파싱 실패: {}", mySavingProduct.getRate());
            throw new CustomException(ErrorCode.DATA_CONVERSION_FAILED);
        }

        return savingAccountMapper.getRecommendSavingAccountList(period, rate, userId);
    }
}
