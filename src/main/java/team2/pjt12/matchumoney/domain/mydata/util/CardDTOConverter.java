package team2.pjt12.matchumoney.domain.mydata.util;

import team2.pjt12.matchumoney.domain.mydata.dto.res.CardInfoResponseDTO;
import team2.pjt12.matchumoney.domain.mydata.dto.res.CardTransactionResponseDTO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardTransactionVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardVO;

import java.util.List;
import java.util.stream.Collectors;

/**
 * VO를 DTO로 변환하는 유틸리티 클래스
 * 
 * @author MatchuMoney Team
 * @since 1.0
 */
public class CardDTOConverter {
    
    /**
     * CardInfoVO를 CardInfoResponseDTO로 변환합니다.
     * 
     * @param cardHoldingVO 변환할 카드 정보 VO
     * @param matchedCard 매칭된 카드고릴라 카드 정보 (nullable)
     * @return 변환된 카드 정보 응답 DTO
     */
    public static CardInfoResponseDTO toCardInfoResponseDTO(CardHoldingVO cardHoldingVO, CardVO matchedCard) {
        return CardInfoResponseDTO.builder()
                .holdingId(cardHoldingVO.getHoldingId())
                .cardId(cardHoldingVO.getCardId())
                .finId(cardHoldingVO.getFinId())
                .cardName(cardHoldingVO.getCardName())
                .maskedCardNo(cardHoldingVO.getResCardNo())
                .cardType(cardHoldingVO.getResCardType())
                .cardState(cardHoldingVO.getResState())
                .sleepYn(cardHoldingVO.getResSleepYn())
                .trafficYn(cardHoldingVO.getResTrafficYn())
                .imageUrl(cardHoldingVO.getResImageLink())
                .issueDate(cardHoldingVO.getResIssueDate())
                .validPeriod(cardHoldingVO.getResValidPeriod())
                .matchStatus(cardHoldingVO.getCardId() != null ? "MATCHED" : "UNMATCHED")
                .matchedCardName(matchedCard != null ? matchedCard.getName() : null)
                .build();
    }
    
    /**
     * CardInfoVO를 CardInfoResponseDTO로 변환합니다. (매칭 정보 없음)
     * 
     * @param cardHoldingVO 변환할 카드 정보 VO
     * @return 변환된 카드 정보 응답 DTO
     */
    public static CardInfoResponseDTO toCardInfoResponseDTO(CardHoldingVO cardHoldingVO) {
        return toCardInfoResponseDTO(cardHoldingVO, null);
    }
    
    /**
     * CardInfoVO 리스트를 CardInfoResponseDTO 리스트로 변환합니다.
     * 
     * @param cardHoldingVOList 변환할 카드 정보 VO 리스트
     * @return 변환된 카드 정보 응답 DTO 리스트
     */
    public static List<CardInfoResponseDTO> toCardInfoResponseDTOList(List<CardHoldingVO> cardHoldingVOList) {
        return cardHoldingVOList.stream()
                .map(CardDTOConverter::toCardInfoResponseDTO)
                .collect(Collectors.toList());
    }
    
    /**
     * CardTransactionVO를 CardTransactionResponseDTO로 변환합니다.
     * 
     * @param transactionVO 변환할 거래 내역 VO
     * @return 변환된 거래 내역 응답 DTO
     */
    public static CardTransactionResponseDTO toCardTransactionResponseDTO(CardTransactionVO transactionVO) {
        return CardTransactionResponseDTO.builder()
                .transactionId(transactionVO.getTransactionId())
                .holdingId(transactionVO.getFinId()) // holdingId 매핑 확인 필요
                .cardName(transactionVO.getCardName())
                .transactionDate(transactionVO.getResUsedDate())
                .transactionTime(transactionVO.getResUsedTime())
                .merchantName(transactionVO.getResMemberStoreName())
                .merchantCategory(transactionVO.getResMemberStoreType()) // 카테고리 추가
                .amount(transactionVO.getResUsedAmount())
                .paymentType(transactionVO.getResPaymentType())
                .installmentMonth(transactionVO.getResInstallmentMonth())
                .approvalNo(transactionVO.getResApprovalNo())
                .cancelYn(transactionVO.getResCancelYn())
                .cancelAmount(transactionVO.getResCancelAmount())
                .vat(transactionVO.getResVat())
                .cashBack(transactionVO.getResCashBack())
                .homeForeignType(transactionVO.getResHomeForeignType())
                .merchantAddress(transactionVO.getResMemberStoreAddr())
                .merchantPhone(transactionVO.getResMemberStoreTelNo())
                .build();
    }
    
    /**
     * CardTransactionVO 리스트를 CardTransactionResponseDTO 리스트로 변환합니다.
     * 
     * @param transactionVOList 변환할 거래 내역 VO 리스트
     * @return 변환된 거래 내역 응답 DTO 리스트
     */
    public static List<CardTransactionResponseDTO> toCardTransactionResponseDTOList(List<CardTransactionVO> transactionVOList) {
        return transactionVOList.stream()
                .map(CardDTOConverter::toCardTransactionResponseDTO)
                .collect(Collectors.toList());
    }
}
