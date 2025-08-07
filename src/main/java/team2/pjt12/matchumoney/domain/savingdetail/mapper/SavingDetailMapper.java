package team2.pjt12.matchumoney.domain.savingdetail.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingOptionDTO;

import java.util.List;

@Mapper
public interface SavingDetailMapper {
    SavingDetailResponseDTO findSavingProductById(@Param("id") Long id);
    List<SavingOptionDTO> findOptionsByProductId(@Param("finPrdtCd") String finPrdtCd);

    // 좋아요 상태 확인 기능
    boolean isLikedByUser(@Param("userId") Long userId, @Param("savingProductId") Long savingProductId);

    int countLikesByProductId(@Param("savingProductId") Long savingProductId);

    // 좋아요 추가
    void insertUserLike(@Param("userId") Long userId, @Param("savingProductId") Long savingProductId);

    // 좋아요 삭제
    void deleteUserLike(@Param("userId") Long userId, @Param("savingProductId") Long savingProductId);
}