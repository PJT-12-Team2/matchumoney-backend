package team2.pjt12.matchumoney.domain.carddetail.service;

import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;

public interface CardDetailService {
    CardDetailResponseDTO getCardDetailById(Long userId, int cardProductId);

    LikeStatusResponseDTO isUserLikedCard(Long userId, int cardProductId);
}