package team2.pjt12.matchumoney.domain.user.dto.res;

import lombok.Getter;

@Getter
public final class UserUpdateResponseDTO {

    private final Long userId;

    public UserUpdateResponseDTO(Long userId) {
        this.userId = userId;
    }
}