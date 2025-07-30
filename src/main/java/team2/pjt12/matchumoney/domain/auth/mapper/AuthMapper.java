package team2.pjt12.matchumoney.domain.auth.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;

@Mapper
public interface AuthMapper {

    void save(UserVO user);

    void updatePassword(
            @Param("email") String email,
            @Param("password") String password
    );
}