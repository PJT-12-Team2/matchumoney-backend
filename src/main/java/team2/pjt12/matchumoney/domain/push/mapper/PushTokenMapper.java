package team2.pjt12.matchumoney.domain.push.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.push.domain.PushTokenVO;

import java.time.Instant;
import java.util.List;

@Mapper
public interface PushTokenMapper {
    int upsert(@Param("userId") long userId,
               @Param("token") String token,
               @Param("userAgent") String userAgent);

    int deleteByToken(@Param("token") String token);

    List<PushTokenVO> findAllByUserId(@Param("userId") Long userId);

    int updateLastSeenAt(@Param("token") String token,
                         @Param("lastSeenAt") Instant lastSeenAt);

    List<String> findTokensByUserId(@Param("userId") Long userId);

    List<String> findTokensByUserIds(@Param("userIds") List<Long> userIds);
}