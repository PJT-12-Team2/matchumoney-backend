package team2.pjt12.matchumoney.domain.carddetail.service;

import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;

public interface CardDetailService {
    CardDetailResponseDTO getCardDetailById(int id);
}