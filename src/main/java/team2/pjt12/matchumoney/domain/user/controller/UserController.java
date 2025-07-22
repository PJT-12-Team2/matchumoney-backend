package team2.pjt12.matchumoney.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.domain.user.dto.req.UserUpdateRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;
import team2.pjt12.matchumoney.domain.user.service.UserService;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

    private final UserService userService;

    @PutMapping("/update")
    public SuccessResponse<UserUpdateResponseDTO> updateUserInfo(@RequestBody UserUpdateRequestDTO reqDto) {
        UserUpdateResponseDTO resDto = userService.updateUserInfo(reqDto);
        return new SuccessResponse<>(resDto, "회원정보 수정 성공");
    }
}
