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
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

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
//        log.info("📋 적금 계좌 목록 조회 - 사용자ID: {}", userId);

        return savingAccountMapper.getSavingAccountList(userId);
    }


    //은행에서 계좌 정보 동기화
    @Transactional
    @Override
    public List<MySavingProductResponseDTO> retrieveAccounts(BankLoginRequestDTO requestDto) {
        Long userId = getCurrentUser().getUserId();
//        log.info("🏦 은행 계좌 동기화 시작 - 사용자ID: {}", userId);

        try {
            // 1. Access Token 발급
            String accessToken = codefApiClient.getAccessToken();

            // 2. Connected ID 생성
            String connectedId = codefConnectedIdProvider.createConnectedId(
                    accessToken,
                    requestDto.getId(),
                    requestDto.getPassword(),
                    CodefApiConstants.ORG_CODE_KB
            );

            // 3. 계좌 목록 조회 및 저장
            processAccountSynchronization(accessToken, connectedId, userId);

            // 4. 동기화된 계좌 목록 반환
            List<MySavingProductResponseDTO> result = savingAccountMapper.getSavingAccountList(userId);
//            log.info("✅ 계좌 동기화 완료 - {}개 계좌", result.size());

            return result;

        } catch (CustomException e) {
//            log.error("❌ 계좌 동기화 실패 - ", e);
            throw e;
        } catch (Exception e) {
//            log.error("❌ 계좌 동기화 실패 - 예상치 못한 오류", e);
            throw new CustomException(ErrorCode.CODEF_SAVING);
        }
    }

    //계좌 동기화 처리
    private void processAccountSynchronization(String accessToken, String connectedId, Long userId) {
        try {
            // 기존 적금 계좌 삭제
            Long finId = Long.parseLong(CodefApiConstants.ORG_CODE_KB);
            savingAccountMapper.deleteByUserIdAndFinId(userId, finId);
//            log.info("기존 적금 계좌 삭제 완료 - 사용자ID: {}, 금융기관ID: {}", userId, finId);

            // 계좌 목록 조회
            List<JsonNode> savingAccounts = codefAccountRetrievalService.retrieveAccountList(
                    accessToken, connectedId, CodefApiConstants.ORG_CODE_KB
            );

            // 각 적금 계좌에 대해 거래내역 조회 및 저장
            int processedCount = 0;
            for (JsonNode account : savingAccounts) {
                String accountNumber = account.path("resAccount").asText();

                if (processTransactionHistory(accessToken, connectedId, accountNumber, userId, finId)) {
                    processedCount++;
                }
            }

//            log.info("계좌 처리 완료 - 총 {}개 중 {}개 성공", savingAccounts.size(), processedCount);

        } catch (Exception e) {
//            log.error("계좌 동기화 처리 중 예외 발생", e);
            throw new RuntimeException("계좌 동기화 처리 실패", e);
        }
    }

    //거래내역 처리
    private boolean processTransactionHistory(String accessToken, String connectedId,
                                              String accountNumber, Long userId, Long finId) {
        try {
            JsonNode transactionData = codefAccountRetrievalService.retrieveTransactionHistory(
                    accessToken, connectedId, CodefApiConstants.ORG_CODE_KB, accountNumber
            );

            if (transactionData == null) {
//                log.warn("내역이 없어 처리를 건너뜀 - 계좌번호: {}", accountNumber);
                return false;
            }

            SavingAccountVO vo = dataTransformService.transformToVO(transactionData, userId, finId);
            savingAccountMapper.insertSavingAccount(vo);

//            log.info("✅ 적금 계좌 저장 완료 - 계좌번호: {}", accountNumber);
            return true;

        } catch (Exception e) {
//            log.error("거래내역 처리 중 예외 발생 - 계좌번호: {}", accountNumber, e);
            return false;
        }
    }

    //내 계좌에 대한 추천 리스트
    @Override
    public List<SavingListItemResponseDTO> getUserRecommendedSavingAccounts(Long id) {
        if (id == -1) {
            return savingAccountMapper.getRecommendDefaultSavingAccountList(userId);
        }
        MySavingProductResponseDTO mySavingProduct = savingAccountMapper.getSavingAccount(id);
        if (mySavingProduct == null) {
//            log.warn("해당 ID로 조회된 적금 계좌가 없습니다. id={}", id);
            throw new CustomException(ErrorCode.CODEF_ERROR);
        }
//        log.info(String.valueOf(mySavingProduct));
        String period = mySavingProduct.getPeriod();
        double rate = Double.parseDouble(mySavingProduct.getRate());


        return savingAccountMapper.getRecommendSavingAccountList(period, rate);
    }
}
