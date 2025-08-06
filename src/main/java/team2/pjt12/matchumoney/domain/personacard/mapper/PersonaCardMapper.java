package team2.pjt12.matchumoney.domain.personacard.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardOptionDTO;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonaCardDTO;

import java.util.List;

@Mapper
public interface PersonaCardMapper {
    String selectPersonaNameById(Long personaId);
    List<PersonaCardDTO> selectCardsByPersonaId(Long personaId);
    Long findPersonaIdByUserId(Long userId);
    List<CardOptionDTO> selectCardOptionsByCardId(@Param("cardId") Long cardId);
}
