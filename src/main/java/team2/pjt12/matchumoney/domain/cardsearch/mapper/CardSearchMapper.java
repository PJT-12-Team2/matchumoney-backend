package team2.pjt12.matchumoney.domain.cardsearch.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchRequestDTO;
import team2.pjt12.matchumoney.domain.cardsearch.dto.CardSearchResponseDTO;

import java.util.List;

@Mapper
public interface CardSearchMapper {

    List<CardSearchResponseDTO> selectCardsByFilter(CardSearchRequestDTO request);
}
