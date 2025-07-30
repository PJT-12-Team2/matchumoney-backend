package team2.pjt12.matchumoney.domain.personadeposit.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;

import java.util.List;

@Mapper
public interface PersonadepositMapper {
    String selectPersonaNameById(Long personaId);
    List<PersonaDepositDTO> selectDepositsByPersonaId(Long personaId);
}
