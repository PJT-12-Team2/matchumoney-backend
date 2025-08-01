package team2.pjt12.matchumoney.domain.deposit.mapper;


import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.deposit.domain.UserDepositVO;

import java.util.List;

@Mapper
public interface UserDepositMapper {
    /**
     * 특정 사용자의 모든 계좌 정보를 조회
     *
     * @param userId 조회할 사용자의 ID
     * @return 해당 사용자의 계좌 정보 리스트 (없으면 빈 리스트 반환)
     */
    List<UserDepositVO> getAccountsByUserId(@Param("userId") String userId);
}
