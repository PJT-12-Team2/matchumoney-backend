package team2.pjt12.matchumoney.domain.deposit.service;

import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import java.util.List;

public interface DepositProductService {

    /**
     * 모든 예금 상품 조회
     * @return 예금 상품 목록
     */
    List<DepositProductResponseDTO> getAllDepositProducts();

    /**
     * 은행별 예금 상품 조회
     * @param bankName 은행명
     * @return 해당 은행의 예금 상품 목록
     */
    List<DepositProductResponseDTO> getDepositProductsByBank(String bankName);

    /**
     * 사용자 잔액 기반으로 가입 가능한 상품 추천
     * @param request 사용자 ID와 잔액 정보
     * @return 가입 가능한 상품 리스트
     */
    List<DepositProductResponseDTO> getProductsByBalance(BalanceRequestDTO request);

    List<DepositProductResponseDTO> getAllDepositProductsWithFavorites(Long userId);
}