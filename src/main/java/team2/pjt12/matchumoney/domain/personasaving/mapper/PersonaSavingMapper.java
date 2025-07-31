package team2.pjt12.matchumoney.domain.personasaving.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonasavingDTO;

import java.util.List;

@Mapper
public interface PersonasavingMapper {
    String selectPersonaNameById(Long personaId);
    List<PersonasavingDTO> selectSavingsByPersonaId(Long personaId);
}
