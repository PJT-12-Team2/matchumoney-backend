package team2.pjt12.matchumoney.domain.user.service;


import team2.pjt12.matchumoney.domain.user.dto.req.UpdatePasswordRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.req.UpdateUserInfoRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;

public interface UserService {

    UserUpdateResponseDTO updateUserInfo(UpdateUserInfoRequestDTO reqDto);

    void updatePassword(UpdatePasswordRequestDTO reqDto);

    void updatePersona(String personaId);
}
