package team2.pjt12.matchumoney.domain.savingdetail.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.service.SavingDetailService;

@RestController
@RequestMapping("/api/saving-products")
@RequiredArgsConstructor
public class SavingDetailController {

    private final SavingDetailService savingDetailService;

    @GetMapping("/{id}")
    public SavingDetailResponseDTO getSavingProduct(@PathVariable Long id) {
        return savingDetailService.getSavingDetailById(id);
    }
}