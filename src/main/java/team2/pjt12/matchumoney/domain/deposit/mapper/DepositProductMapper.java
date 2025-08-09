package team2.pjt12.matchumoney.domain.deposit.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.List;

@Mapper
public interface DepositProductMapper {

    /**
     * 모든 예금 상품 조회 (save_trm이 가장 큰 옵션의 금리 정보 포함)
     * @return 예금 상품 목록
     */
    List<DepositProductResponseDTO> findAllDepositProducts(@Param("userId") Long userId);

    /**
     * 은행별 예금 상품 조회
     * @param bankName 은행명
     * @return 해당 은행의 예금 상품 목록
     */
    List<DepositProductResponseDTO> findDepositProductsByBankName(@Param("bankName") String bankName);

}