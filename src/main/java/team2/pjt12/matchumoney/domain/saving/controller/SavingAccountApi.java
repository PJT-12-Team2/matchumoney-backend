package team2.pjt12.matchumoney.domain.saving.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.Tag;
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

    @ApiOperation(value = "각 적금 계좌에 대한 추천 적금 리스트", notes = "사용자의 해당 계좌에 대한 타 적금을 추천합니다..")
    @GetMapping("/{id}/recommend")
    ResponseEntity<List<SavingListItemResponseDTO>> getRecommendSavingAccountList(@ApiParam(value = "해당 적금 상품 계좌 id") @PathVariable("id") Long id);

}
