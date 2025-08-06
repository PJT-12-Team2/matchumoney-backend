package team2.pjt12.matchumoney.domain.depositdetail.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositOptionDTO;

import java.util.List;

@Mapper
public interface DepositDetailMapper {
    DepositDetailResponseDTO findDepositProductById(@Param("id") Long id);
    List<DepositOptionDTO> findOptionsByProductId(@Param("productId") Long productId);

    // 좋아요 상태 확인 기능
    boolean isLikedByUser(@Param("userId") Long userId, @Param("depositProductId") Long depositProductId);

    int countLikesByProductId(@Param("depositProductId") Long depositProductId);
    // 좋아요 추가
    void insertUserLike(@Param("userId") Long userId, @Param("depositProductId") Long depositProductId);

    // 좋아요 삭제
    void deleteUserLike(@Param("userId") Long userId, @Param("depositProductId") Long depositProductId);
}
