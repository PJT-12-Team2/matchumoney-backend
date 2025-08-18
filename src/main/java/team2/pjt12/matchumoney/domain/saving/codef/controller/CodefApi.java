package team2.pjt12.matchumoney.domain.saving.codef.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;

import java.util.List;


@Api(tags = "Codef API", description = "Codef 서비스에서 connectedID와 관련된 여러 작업을 수행합니다.")
@RequestMapping("/api/codef")
public interface CodefApi {

    @ApiOperation(value = "예적금 계좌 동기화 및 조회(처음)", notes = "은행 로그인 정보로 사용자의 예적금 계좌를 동기화 후 조회합니다.")
    @PostMapping("/saving/sync")
    ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccount(
            @ApiParam(value = "은행 로그인 요청 DTO") @RequestBody BankLoginRequestDTO requestDto);

    @ApiOperation(value = "예적금 계좌 동기화 및 조회(재연결)", notes = "저장된 정보로 사용자의 예적금 계좌를 동기화 후 조회합니다.")
    @PostMapping("/saving/sync/pre")
    ResponseEntity<List<MySavingProductResponseDTO>> syncSavingAccountPre();

    @ApiOperation(value = "연결된 connectedId 리스트", notes = "테스트용 api 입니다.")
    @GetMapping("")
    ResponseEntity<List<String>> getConnectedIdList();

    @ApiOperation(value = "coneectedId 제거", notes = "연결되어 있던 은행 (로그인) 정보를 제거합니다.")
    @DeleteMapping("/connectedId")
    ResponseEntity<String> deleteConnectedId();

    @ApiOperation(value = "connectedId 업데이트", notes = "연결된 은행 정보를 업데이트합니다. (로그인 정보가 변경된 경우)")
    @PutMapping("/connectedId")
    ResponseEntity<List<MySavingProductResponseDTO>> updateConnectedId(@ApiParam(value = "은행 로그인 요청 DTO") @RequestBody BankLoginRequestDTO requestDto);

    @ApiOperation(value = "connectedId에 연결된 은행 리스트", notes = "테스트용 api 입니다.")
    @GetMapping("/connectedId")
    ResponseEntity<List<String>> getBanksByConnectedId();

}