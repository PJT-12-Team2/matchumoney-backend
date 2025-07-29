package team2.pjt12.matchumoney.domain.personacard.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.personacard.dto.RecommendedCardDTO;

import java.util.List;

@Mapper
public interface PersonacardMapper {

    String findPersonaNameById(Long personaId);

    List<RecommendedCardDTO> findCardsByPersonaId(Long personaId);
}
