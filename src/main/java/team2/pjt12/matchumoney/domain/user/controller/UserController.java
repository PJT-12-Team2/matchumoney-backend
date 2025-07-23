package team2.pjt12.matchumoney.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdatePasswordRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdateUserInfoRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;
import team2.pjt12.matchumoney.domain.user.service.UserService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

import javax.validation.Valid;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @PatchMapping("/update")
    public SuccessResponse<UserUpdateResponseDTO> updateUserInfo(@RequestBody @Valid UpdateUserInfoRequestDTO reqDto) {
        UserUpdateResponseDTO resDto = userService.updateUserInfo(reqDto);
        return new SuccessResponse<>(resDto, "회원정보 수정 성공");
    }

    @PatchMapping("/update/password")
    public SuccessResponse<String> updatePassword(@RequestBody @Valid UpdatePasswordRequestDTO reqDto) {
        userService.updatePassword(reqDto);
        return new SuccessResponse<>("비밀번호 수정 성공");
    }
}
