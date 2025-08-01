package team2.pjt12.matchumoney.domain.personasaving.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingDTO;

import java.util.List;

@Mapper
public interface PersonaSavingMapper {
    String selectPersonaNameById(Long personaId);
    Long findPersonaIdByUserId(Long userId);
    List<PersonaSavingDTO> selectSavingsByPersonaId(Long personaId);
}
