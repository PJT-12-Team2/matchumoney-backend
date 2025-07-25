package team2.pjt12.matchumoney.domain.mydata.service;

import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;
import team2.pjt12.matchumoney.domain.mydata.vo.CardTransactionVO;

import java.time.LocalDate;
import java.util.List;

/**
 * KB카드 마이데이터 연동 서비스
 * 
 * KB카드 마이데이터 API를 통한 카드 정보 및 거래 내역 관리를 담당합니다.
 * 카드 정보 동기화, 거래 내역 조회, 카드고릴라와의 매칭 기능을 제공합니다.
 * 
 * @author MatchuMoney Team
 * @since 1.0
 */
public interface KbCardService {
    
    /**
     * KB카드 마이데이터 API를 통해 사용자의 카드 정보를 동기화하고 저장합니다.
     * 
     * 주요 기능
     *   기존 사용자 카드 정보 삭제
     *   KB카드 마이데이터 API 호출
     *   카드고릴라 데이터베이스와 카드명 매칭
     *   매칭된 카드 정보 저장
     *   추가 자동 매칭 수행
     * 
     * 매칭 우선순위
     *   정확한 카드명 매칭
     *   괄호 제거 후 매칭 (예: "카드명(타입)" → "카드명")
     *   대소문자 무시 매칭
     *   포함 관계 매칭
     *
     * 
     * @param userId 사용자 ID (로그인한 사용자의 고유 식별자)
     * @param kbId KB카드 아이디 (마이데이터 연동을 위한 KB카드 로그인 ID)
     * @param kbPw KB카드 비밀번호 (마이데이터 연동을 위한 KB카드 로그인 비밀번호)
     * @return 동기화된 카드 정보 목록 (CardHoldingVO 리스트)
     * @throws Exception 마이데이터 API 호출 실패, 네트워크 오류, 인증 실패 등
     * @see CardHoldingVO
     */
    List<CardHoldingVO> syncAndSaveCards(Long userId, String kbId, String kbPw) throws Exception;
    
    /**
     * 저장된 사용자의 카드 보유 정보를 조회합니다.
     * 
     * 조회되는 정보
     *   카드 보유 ID (holdingId)
     *   카드고릴라 매칭 ID (cardId)
     *   카드명, 카드번호(마스킹)
     *   카드 상태, 유효기간
     *   발급일, 이미지 링크
     * 
     * @param userId 사용자 ID
     * @return 사용자의 카드 보유 정보 목록
     * @see CardHoldingVO
     */
    List<CardHoldingVO> getCards(Long userId);
    
    /**
     * 특정 카드의 거래 내역을 KB카드 마이데이터 API를 통해 동기화하고 저장합니다.
     * 
     * 주요 기능
     *   카드 정보 유효성 검증 (connectedId 확인)
     *   KB카드 거래 내역 API 호출
     *   기존 거래 내역 삭제
     *   새로운 거래 내역 저장
     * 
     * 제약사항
     *   조회 기간: 최대 1년
     *   일일 API 호출 제한 적용
     *   카드 연동이 완료된 카드만 조회 가능
     * 
     * @param userId 사용자 ID
     * @param holdingId 카드 보유 ID (mydata_card_holdings의 holding_id)
     * @param cardNo 카드번호 (마이데이터 인증용)
     * @param cardPw2 카드 비밀번호 뒤 2자리
     * @param birthDate 생년월일 (YYYYMMDD 형식)
     * @param startDate 조회 시작일 (LocalDate)
     * @param endDate 조회 종료일 (LocalDate)
     * @return 동기화된 거래 내역 목록
     * @throws Exception 마이데이터 API 호출 실패, 카드 정보 없음, 날짜 범위 오류 등
     * @throws RuntimeException connectedId가 유효하지 않은 경우
     * @see CardTransactionVO
     */
    List<CardTransactionVO> syncAndSaveCardTransactions(
            Long userId, 
            Long holdingId, 
            String cardNo, 
            String cardPw2, 
            String birthDate, 
            LocalDate startDate, 
            LocalDate endDate
    ) throws Exception;
    
    /**
     * 저장된 특정 카드의 거래 내역을 조회합니다.
     * 
     * 조회되는 정보
     *   거래일시, 거래금액
     *   가맹점명, 가맹점 정보
     *   승인번호, 할부개월
     *   취소 여부, 캐시백 정보
     *   해외/국내 구분
     * 
     * 정렬 순서: 거래일시 내림차순 (최신 거래 우선)
     * 
     * @param userId 사용자 ID
     * @param holdingId 카드 보유 ID
     * @return 해당 카드의 거래 내역 목록
     * @see CardTransactionVO
     */
    List<CardTransactionVO> getCardTransactions(Long userId, Long holdingId);
}
