package team2.pjt12.matchumoney.domain.saving.util;


import com.fasterxml.jackson.databind.JsonNode;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingAccountDto;

import java.time.LocalDateTime;

//dto -> vo 변환기
@Slf4j
@Service
public class SavingAccountConverter {

    public SavingAccountVO transformToVO(JsonNode transactionData, Long userId, Long finId) {
        try {
            SavingAccountDto dto = new SavingAccountDto(transactionData, userId, finId);

            SavingAccountVO vo = SavingAccountVO.builder()
                    .userId(dto.getUserId())
                    .finId(dto.getFinId())
                    .resAccount(dto.getResAccount())
                    .resAccountStatus(dto.getResAccountStatus())
                    .resAccountDisplay(dto.getResAccountDisplay())
                    .resAccountName(dto.getResAccountName())
                    .resAccountNickName(dto.getResAccountNickName())
                    .resAccountHolder(dto.getResAccountHolder())
                    .resFinalRoundNo(dto.getResFinalRoundNo())
                    .resAccountStartDate(dto.getResAccountStartDate())
                    .resAccountEndDate(dto.getResAccountEndDate())
                    .resAccountBalance(dto.getResAccountBalance())
                    .resMonthlyPayment(dto.getResMonthlyPayment())
                    .resValidPeriod(dto.getResValidPeriod())
                    .resType(dto.getResType())
                    .resManagementBranch(dto.getResManagementBranch())
                    .resRate(dto.getResRate())
                    .resContractAmount(dto.getResContractAmount())
                    .resPaymentMethods(dto.getResPaymentMethods())
                    .resLastTranDate(dto.getResLastTranDate())
                    .commStartDate(dto.getCommStartDate())
                    .commEndDate(dto.getCommEndDate())
                    .createdTime(LocalDateTime.now())
                    .lastModifiedTime(LocalDateTime.now())
                    .build();

            return vo;

        } catch (Exception e) {
//            log.error("데이터 변환 중 예외 발생", e);
            throw new RuntimeException("데이터 변환 실패", e);
        }
    }
}
