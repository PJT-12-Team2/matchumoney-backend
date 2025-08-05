package team2.pjt12.matchumoney.domain.depositdetail.service;

import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;

public interface DepositDetailService {
    DepositDetailResponseDTO getDepositDetailById(Long id);
}