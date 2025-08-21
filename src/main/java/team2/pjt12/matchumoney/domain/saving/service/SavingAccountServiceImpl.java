// 6. 개선된 메인 서비스
package team2.pjt12.matchumoney.domain.saving.service;

import com.fasterxml.jackson.databind.JsonNode;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.session.RowBounds;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.saving.codef.CodefConnectedIdProvider;
import team2.pjt12.matchumoney.domain.saving.codef.domain.ConnectedIdVO;
import team2.pjt12.matchumoney.domain.saving.codef.mapper.CodefMapper;
import team2.pjt12.matchumoney.domain.saving.codef.service.CodefAccountRetrievalService;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;
import team2.pjt12.matchumoney.domain.saving.mapper.SavingAccountMapper;
import team2.pjt12.matchumoney.domain.saving.util.SavingAccountConverter;
import team2.pjt12.matchumoney.global.exception.CodefApiException;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Slf4j
@Service
@RequiredArgsConstructor
public class SavingAccountServiceImpl implements SavingAccountService {

    private final SavingAccountMapper savingAccountMapper;
    private final CodefConnectedIdProvider codefConnectedIdProvider;
    private final CodefAccountRetrievalService codefAccountRetrievalService;
    private final SavingAccountConverter dataTransformService;
    private final CodefMapper codefMapper;

    // 사용자 적금 계좌 목록 조회
    @Override
    @Transactional(readOnly = true)
    public List<MySavingProductResponseDTO> getSavingAccountList() {
        Long userId = getCurrentUser().getUserId();
        log.info("📋 적금 계좌 목록 조회 - 사용자ID: {}", userId);
        return savingAccountMapper.getSavingAccountList(userId);
    }

    // 은행에서 계좌 정보 동기화
    @Transactional
    @Override
    public List<MySavingProductResponseDTO> retrieveAccounts(BankLoginRequestDTO requestDto) {
        Long userId = getCurrentUser().getUserId();
        log.info("🏦 은행 계좌 동기화 시작 - 사용자ID: {}", userId);

        // 1) Connected ID 생성/가져오기 및 계정 연결
        String connectedId;
        String existing = codefMapper.getCodefConnectedIdByUserId(userId);

        if (existing == null) {
            try {
                connectedId = codefConnectedIdProvider.createConnectedId(
                        requestDto.getId(),
                        requestDto.getPassword(),
                        requestDto.getBankCode(),
                        requestDto.getBirthDate()
                );
            } catch (CustomException e) {
                log.error("Connected ID 생성 실패 - {}", e.getErrorCode().getMessage());
                throw e;
            } catch (Exception e) {
                log.error("Connected ID 생성 예외", e);
                throw new CustomException(ErrorCode.CODEF_ERROR);
            }
            codefMapper.insertCodefConnectedId(
                    new ConnectedIdVO(userId, connectedId, LocalDateTime.now(), LocalDateTime.now())
            );
        } else {
            connectedId = existing;
            try {
                codefConnectedIdProvider.addAccountByConnectedId(
                        requestDto.getId(),
                        requestDto.getPassword(),
                        requestDto.getBankCode(),
                        requestDto.getBirthDate(),
                        connectedId
                );
            } catch (CustomException e) {
                log.error("계정 추가 실패 - {}", e.getErrorCode().getMessage());
                throw e;
            } catch (Exception e) {
                log.error("계정 추가 예외", e);
                throw new CustomException(ErrorCode.CODEF_ERROR);
            }
        }

        // 2) 계좌 목록 조회 및 저장
        processAccountSynchronization(connectedId, userId, requestDto.getBirthDate(), requestDto.getBankCode());

        // 은행 코드 DB에 추가(맵핑 테이블 관리)
        codefMapper.insertCodefConnectedIdOrganization(connectedId, requestDto.getBankCode());

        // 3) 결과 반환
        List<MySavingProductResponseDTO> result = savingAccountMapper.getSavingAccountList(userId);
        log.info("✅ 계좌 동기화 완료 - {}개 계좌", result.size());
        return result;
    }

    // 은행에서 계좌 정보 동기화(사전 연결된 기관 전체)
    @Transactional
    @Override
    public List<MySavingProductResponseDTO> retrieveAccountsPre() {
        Long userId = getCurrentUser().getUserId();
        log.info("🏦 은행 계좌 동기화(사전 연결 기관) 시작 - 사용자ID: {}", userId);

        String connectedId;
        try {
            connectedId = codefMapper.getCodefConnectedIdByUserId(userId);
            if (connectedId == null || connectedId.isBlank()) {
                throw new CustomException(ErrorCode.USER_NOT_FOUND);
            }
        } catch (CustomException e) {
            log.error("Connected ID 조회 실패 - {}", e.getErrorCode().getMessage());
            throw e;
        }

        List<String> codes = codefAccountRetrievalService.getOrganizationCodes(connectedId);

        for (String code : codes) {
            processAccountSynchronization(connectedId, userId, "", code);
        }

        List<MySavingProductResponseDTO> result = savingAccountMapper.getSavingAccountList(userId);
        log.info("✅ 계좌 동기화 완료 - {}개 계좌", result.size());
        return result;
    }

    // 계좌 동기화 처리
    @Transactional(rollbackFor = Exception.class)
    public void processAccountSynchronization(String connectedId, Long userId, String birthDate, String bankCode) {
        Long finId;
        try {
            finId = Long.parseLong(bankCode);
        } catch (NumberFormatException e) {
            log.error("금융기관 코드 파싱 실패: {}", bankCode);
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
            // v1: accessToken 넘기지 않음
            savingAccounts = codefAccountRetrievalService.retrieveAccountList(
                    connectedId, bankCode
            );
        } catch (CodefApiException e) {
            log.error("Codef API 실패 - code: {}, message: {}", e.getCode(), e.getMessage());
            throw e;
        }
        log.info("savingAccounts: " + savingAccounts);
        int processedCount = 0;
        List<String> failedAccounts = new ArrayList<>();

        for (JsonNode account : savingAccounts) {
            String accountNumber = account.path("resAccount").asText();

            try {
                if (processTransactionHistory(connectedId, accountNumber, userId, finId, birthDate, bankCode)) {
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
        }
    }

    // 거래내역 처리
    public boolean processTransactionHistory(String connectedId,
                                             String accountNumber, Long userId, Long finId,
                                             String birthDate, String bankCode) {

        // v1: accessToken 넘기지 않음
        JsonNode transactionData = codefAccountRetrievalService.retrieveTransactionHistory(
                connectedId, bankCode, accountNumber, birthDate
        );
        log.info("transactionData: " + transactionData);
        // transactionData 가 JsonNode 라는 가정
        JsonNode data = transactionData.path("data");
        log.info("data: " + data);
// ✅ VO 생성 (Long 필드는 내부에서 0L로 안전 파싱)
        SavingAccountVO vo = new SavingAccountVO(data, userId, finId);

        try {
            savingAccountMapper.insertSavingAccount(vo);
        } catch (Exception e) {
            log.error("적금 계좌 저장 실패 - 계좌번호: {}", accountNumber, e);
            throw new CustomException(ErrorCode.DB_SAVING_INSERT_FAILED);
        }
        log.info("✅ 적금 계좌 저장 완료 - 계좌번호: {}", accountNumber);
        return true;
    }

    // 내 계좌에 대한 추천 리스트
    @Override
    public List<SavingListItemResponseDTO> getUserRecommendedSavingAccounts(Long id, int page, int size) {
        Long userId = getCurrentUser().getUserId();

        int offset = page * size;
        RowBounds rowBounds = new RowBounds(offset, size);

        if (id == -1) {
            return savingAccountMapper.getRecommendDefaultSavingAccountList(userId, rowBounds);
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

        if (!mySavingProduct.getUserId().equals(userId)) {
            throw new CustomException(ErrorCode.USER_NOT_AUTHORIZED);
        }

        String period = mySavingProduct.getPeriod();
        double rate;
        try {
            rate = Double.parseDouble(mySavingProduct.getRate());
        } catch (NumberFormatException e) {
            log.error("적금 금리 파싱 실패: {}", mySavingProduct.getRate());
            throw new CustomException(ErrorCode.DATA_CONVERSION_FAILED);
        }

        return savingAccountMapper.getRecommendSavingAccountList(period, rate, userId, rowBounds);
    }

    // ConnectedId 삭제
    @Override
    @Transactional(rollbackFor = Exception.class)
    public String deleteConnectedId() {
        Long userId = getCurrentUser().getUserId();

        // 1) 현재 사용자 connectedId 조회
        String connectedId = codefMapper.getCodefConnectedIdByUserId(userId);
        if (connectedId == null || connectedId.isBlank()) {
            throw new CustomException(ErrorCode.USER_NOT_FOUND);
        }

        // 2) CODEF 계정 삭제 호출 (외부 API)
        codefAccountRetrievalService.deleteConnectedId(connectedId);

        // 3) DB에서 연결 정보 삭제
        boolean affected = codefMapper.deleteCodefConnectedIdByUserId(userId);
        if (!affected) {
            throw new IllegalStateException("삭제 대상이 없거나 여러 건이 삭제됨: affected=" + affected);
        }
        // 4) 성공 메시지
        return connectedId + "가 제거되었습니다.";
    }
}
