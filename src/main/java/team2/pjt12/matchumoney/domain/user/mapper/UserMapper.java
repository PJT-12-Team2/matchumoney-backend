package team2.pjt12.matchumoney.domain.user.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;

import java.util.Optional;

@Mapper
public interface UserMapper {

    Optional<UserVO> findBySocialIdAndSocialProvider(@Param("socialId") String socialId, @Param("socialProvider") String socialProvider);

    void save(UserVO user);
}