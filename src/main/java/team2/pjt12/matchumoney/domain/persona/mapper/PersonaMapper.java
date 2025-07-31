package team2.pjt12.matchumoney.domain.persona.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.persona.dto.RecommendationDTO;

import java.util.List;
import java.util.Map;

@Mapper
public interface PersonaMapper {

    // 페르소나 정보 조회
    Map<String, Object> findPersonaInfoByCode(@Param("code") String code);

    // 페르소나 태그 목록 조회
    List<String> findTagsByCode(@Param("code") String code);

    // 페르소나 추천 상품 목록 조회
    List<RecommendationDTO> findRecommendationsByCode(@Param("code") String code);

    Map<String, Object> findPersonaInfoById(@Param("personaId") Long personaId);

    List<String> findTagsById(@Param("personaId") Long personaId);

    List<RecommendationDTO> findRecommendationsById(@Param("personaId") Long personaId);
}
