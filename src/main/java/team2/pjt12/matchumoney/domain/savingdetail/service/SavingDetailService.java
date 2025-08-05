package team2.pjt12.matchumoney.domain.savingdetail.service;

import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;

public interface SavingDetailService {
    SavingDetailResponseDTO getSavingDetailById(Long id);
}