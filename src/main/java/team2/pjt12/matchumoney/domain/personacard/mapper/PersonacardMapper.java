package team2.pjt12.matchumoney.domain.personacard.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.personacard.dto.PersonacardDTO;

import java.util.List;

@Mapper
public interface PersonacardMapper {
    String selectPersonaNameById(Long personaId);
    List<PersonacardDTO> selectCardsByPersonaId(Long personaId);
}
