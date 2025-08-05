package team2.pjt12.matchumoney.domain.saving.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;

import java.util.List;

@Api(tags = "SavingAccount", description = "예적금 계좌 관련 API")
@RequestMapping("/api/user/me/savings")
public interface SavingAccountApi {

    @ApiOperation(value = "예적금 계좌 동기화 및 조회", notes = "은행 로그인 정보로 사용자의 예적금 계좌를 동기화 후 조회합니다.")
    @PostMapping("/sync")
    ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccount(
            @ApiParam(value = "은행 로그인 요청 DTO") @RequestBody BankLoginRequestDTO requestDto);

    @ApiOperation(value = "예적금 계좌 조회", notes = "DB에 저장된 사용자의 예적금 계좌 정보를 조회합니다.")
    @GetMapping("")
    ResponseEntity<List<MySavingProductResponseDTO>> getMySavingAccount();

    @ApiOperation(value = "추천 적금 리스트 (페이징)", notes = "특정 적금 계좌 기반 추천 적금 리스트를 페이징으로 반환")
    @GetMapping("/{id}/recommend")
    ResponseEntity<List<SavingListItemResponseDTO>> getRecommendSavingAccountList(
            @ApiParam(value = "적금 계좌 ID") @PathVariable("id") Long id,
            @ApiParam(value = "페이지 번호", defaultValue = "0") @RequestParam(defaultValue = "0") int page,
            @ApiParam(value = "페이지 크기", defaultValue = "10") @RequestParam(defaultValue = "10") int size
    );
}
