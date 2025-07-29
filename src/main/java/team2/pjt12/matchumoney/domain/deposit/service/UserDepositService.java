package team2.pjt12.matchumoney.domain.deposit.service;

import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;  // req로 변경
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.UserDepositResponseDTO;

import java.util.List;

public interface UserDepositService {
    /**
     * 특정 사용자의 계좌 목록을 조회하여 화면용 DTO로 변환하여 반환
     * @param userId 조회할 사용자 ID
     * @return 화면 표시용으로 가공된 계좌 정보 리스트
     * @throws RuntimeException 사용자가 존재하지 않거나 조회 중 오류 발생시
     */
    List<UserDepositResponseDTO> getAccountsByUserId(String userId);

    /**
     * 사용자 잔액 기반으로 가입 가능한 상품 추천
     * @param request 사용자 ID와 잔액 정보
     * @return 가입 가능한 상품 리스트
     */
    List<DepositProductResponseDTO> getProductsByBalance(BalanceRequestDTO request);
}