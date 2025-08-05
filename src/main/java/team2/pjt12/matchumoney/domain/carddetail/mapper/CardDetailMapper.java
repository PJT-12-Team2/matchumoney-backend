package team2.pjt12.matchumoney.domain.carddetail.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;

@Mapper
public interface CardDetailMapper {
    CardDetailResponseDTO findCardDetailById(int id);
}