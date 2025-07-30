package team2.pjt12.matchumoney.domain.personasaving.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.personasaving.dto.SavingProductDTO;

import java.util.List;

@Mapper
public interface PersonaSavingMapper {
    List<SavingProductDTO> findByPersonaId(Long personaId);
}