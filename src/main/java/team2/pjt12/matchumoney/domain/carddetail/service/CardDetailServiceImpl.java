package team2.pjt12.matchumoney.domain.carddetail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;
import team2.pjt12.matchumoney.domain.carddetail.mapper.CardDetailMapper;

@Service
@RequiredArgsConstructor
public class CardDetailServiceImpl implements CardDetailService {

    private final CardDetailMapper cardDetailMapper;

    @Override
    public CardDetailResponseDTO getCardDetailById(int id) {
        return cardDetailMapper.findCardDetailById(id);
    }
}