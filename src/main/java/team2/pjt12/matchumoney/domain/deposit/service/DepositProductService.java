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

}