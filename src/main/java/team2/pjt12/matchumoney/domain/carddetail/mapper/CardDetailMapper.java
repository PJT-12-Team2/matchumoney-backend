package team2.pjt12.matchumoney.domain.carddetail.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;


import java.util.List;

@Mapper
public interface CardDetailMapper {
    CardDetailResponseDTO findCardDetailById(@Param("id") int id);

    // 좋아요 상태 확인 기능
    boolean isLikedByUser(@Param("userId") Long userId, @Param("cardProductId") int cardProductId);

    int countLikesByProductId(@Param("cardProductId") int cardProductId);
    // 좋아요 추가
    void insertUserLike(@Param("userId") Long userId, @Param("cardProductId") int cardProductId);

    // 좋아요 삭제
    void deleteUserLike(@Param("userId") Long userId, @Param("cardProductId") int cardProductId);
}