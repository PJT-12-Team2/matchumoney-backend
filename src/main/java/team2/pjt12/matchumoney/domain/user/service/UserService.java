package team2.pjt12.matchumoney.domain.user.service;


import team2.pjt12.matchumoney.domain.user.dto.req.UpdatePasswordRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdateUserInfoRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.MyPageResponseDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserResponseDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;

public interface UserService {

    UserUpdateResponseDTO updateUserInfo(UpdateUserInfoRequestDTO reqDto);

    void updatePassword(UpdatePasswordRequestDTO reqDto);

    UserResponseDTO getMyInfo();

    void updatePersona(String personaId);

    MyPageResponseDTO getMyPage();
}
