package team2.pjt12.matchumoney.domain.savingdetail.service;

import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.dto.LikeStatusResponseDTO;

public interface SavingDetailService {
    SavingDetailResponseDTO getSavingDetailById(Long userId, Long id);
    LikeStatusResponseDTO isUserLikedSaving(Long userId, Long savingProductId);
}