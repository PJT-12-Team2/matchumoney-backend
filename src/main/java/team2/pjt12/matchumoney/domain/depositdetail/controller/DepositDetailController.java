package team2.pjt12.matchumoney.domain.depositdetail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.service.DepositDetailService;

@RestController
@RequestMapping("/api/deposit-products")
@RequiredArgsConstructor
public class DepositDetailController {

    private final DepositDetailService depositDetailService;

    @GetMapping("/{id}")
    public DepositDetailResponseDTO getDepositProduct(@PathVariable Long id) {
        return depositDetailService.getDepositDetailById(id);
    }
}