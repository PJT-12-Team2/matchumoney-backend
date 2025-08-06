package team2.pjt12.matchumoney.domain.depositdetail.service;

import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;

public interface DepositDetailService {
    DepositDetailResponseDTO getDepositDetailById(Long id, Long userId);

    LikeStatusResponseDTO isUserLikedDeposit(Long userId, Long depositProductId);

    int getDepositLikeCount(Long depositProductId);
}