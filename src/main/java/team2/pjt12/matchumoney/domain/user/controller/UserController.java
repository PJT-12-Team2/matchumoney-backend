package team2.pjt12.matchumoney.domain.user.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.*;
import team2.pjt12.matchumoney.global.success.SuccessResponse;

@RestController
@RequiredArgsConstructor
@RequestMapping("/user")
@Slf4j
public class UserController {

//    @PutMapping("/update")
//    public SuccessResponse<UserUpdateResponseDTO> updateUserInfo(@RequestBody UpdateReqestDTO reqDto) {
//        UserUpdateResponseDTO resDto = userService.updateUserInfo(reqDto);
//        return new SuccessResponse<>(resDto, "회원정보 수정 성공");
//    }
}
