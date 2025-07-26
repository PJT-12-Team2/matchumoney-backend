package team2.pjt12.matchumoney.domain.deposit.service;

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
     * 상품 코드로 예금 상품 조회
     * @param productCode 상품 코드
     * @return 예금 상품 정보
     */
    DepositProductResponseDTO getDepositProductByCode(String productCode);

    /**
     * 금리 범위로 예금 상품 조회
     * @param minRate 최소 금리
     * @param maxRate 최대 금리
     * @return 해당 금리 범위의 예금 상품 목록
     */
    List<DepositProductResponseDTO> getDepositProductsByInterestRate(Double minRate, Double maxRate);

    /**
     * 저축 기간으로 예금 상품 조회
     * @param saveTrm 저축 기간 (개월)
     * @return 해당 저축 기간을 가진 예금 상품 목록
     */
    List<DepositProductResponseDTO> getDepositProductsBySaveTerm(Integer saveTrm);
}