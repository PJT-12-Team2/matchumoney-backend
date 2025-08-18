package team2.pjt12.matchumoney.domain.user.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdatePasswordRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdateUserInfoRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.MyPageResponseDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserResponseDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;
import team2.pjt12.matchumoney.domain.user.service.UserPercentileService;
import team2.pjt12.matchumoney.domain.user.service.UserService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.validation.Valid;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/user")
@Slf4j
@Api(tags = "User API", description = "회원 관련 API (내 정보, 마이페이지, 수정 등)")
public class UserController {

    private final UserService userService;
    private final UserPercentileService userPercentileService;

    @PatchMapping("/update")
    @ApiOperation(value = "회원 정보 수정", notes = "닉네임, 성별, 생년월일, 프로필 이미지 등 수정")
    public SuccessResponse<UserUpdateResponseDTO> updateUserInfo(
            @RequestBody @Valid UpdateUserInfoRequestDTO reqDto) {
        UserUpdateResponseDTO resDto = userService.updateUserInfo(reqDto);
        return new SuccessResponse<>(resDto, "회원정보 수정 성공");
    }

    @PatchMapping("/update/password")
    @ApiOperation(value = "비밀번호 수정", notes = "현재 비밀번호 검증 후 새 비밀번호로 변경")
    public SuccessResponse<String> updatePassword(
            @RequestBody @Valid UpdatePasswordRequestDTO reqDto) {
        userService.updatePassword(reqDto);
        return new SuccessResponse<>("비밀번호 수정 성공");
    }

    @GetMapping("/me")
    @ApiOperation(value = "내 정보 조회", notes = "로그인한 회원의 정보를 조회합니다.")
    public SuccessResponse<UserResponseDTO> getMyInfo() {
        UserResponseDTO resDto = userService.getMyInfo();
        return new SuccessResponse<>(resDto, "내 정보 조회 성공");
    }

    @GetMapping("/mypage")
    @ApiOperation(value = "마이페이지 정보 조회", notes = "회원의 페르소나, 경험치, 관심 상품 등을 포함한 정보를 조회합니다.")
    public SuccessResponse<MyPageResponseDTO> getMyPage() {
        MyPageResponseDTO resDto = userService.getMyPage();
        return new SuccessResponse<>(resDto, "마이페이지 조회 성공");
    }

    @PatchMapping("/update/persona")
    @ApiOperation(value = "페르소나 저장", notes = "선택한 페르소나 ID를 저장합니다.")
    public SuccessResponse<String> updatePersona(
            @ApiParam(value = "선택한 페르소나 ID", example = "P01", required = true)
            @RequestParam("persona_id") String personaId) {
        userService.updatePersona(personaId);
        return new SuccessResponse<>("페르소나 저장 성공");
    }

    @GetMapping("/mypage/top-percent")
    @ApiOperation(value = "EXP 기준 상위 퍼센트 조회", notes = "전체 사용자 중 본인의 누적 EXP 기준 상위 퍼센트를 조회합니다.")
    public SuccessResponse<Integer> getMyTopPercent() {
        long userId = getCurrentUser().getUserId();
        int top = userPercentileService.calcTopPercent(userId);
        return new SuccessResponse<>(top, "전체 유저 누적 EXP 기준 상위 퍼센트");
    }
}
