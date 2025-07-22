package team2.pjt12.matchumoney.domain.user.service;


import team2.pjt12.matchumoney.domain.user.dto.req.UserUpdateRequestDTO;
import team2.pjt12.matchumoney.domain.user.dto.res.UserUpdateResponseDTO;

public interface UserService {

    UserUpdateResponseDTO updateUserInfo(UserUpdateRequestDTO reqDto);
}
