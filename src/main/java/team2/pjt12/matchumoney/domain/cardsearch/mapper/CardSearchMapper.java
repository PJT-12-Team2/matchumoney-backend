package team2.pjt12.matchumoney.domain.cardsearch.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardListItemDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;

import java.util.List;

@Mapper
public interface CardSearchMapper {

    List<CardSearchResponseDTO> selectCardsByFilter(@Param("request") CardSearchRequestDTO request, @Param("userId") Long userId);

    // 커서 기반 무한스크롤 (size+1 조회)
    List<CardListItemDTO> selectCardsByCursor(
            @Param("userId") Long userId,
            @Param("request") CardSearchRequestDTO request,
            @Param("cursorId") Long cursorId, // 마지막으로 본 id (null이면 첫 페이지)
            @Param("limit") int limit // size+1로 호출
    );

    List<CardListItemDTO> selectCardsByPage(@Param("request") CardSearchRequestDTO request,
                                            @Param("userId") Long userId,
                                            @Param("offset") int offset,
                                            @Param("limit") int limit);

}


