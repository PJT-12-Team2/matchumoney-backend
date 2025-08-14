package team2.pjt12.matchumoney.domain.user.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserPercentileServiceImpl implements UserPercentileService {
    private final UserMapper userMapper;

    @Override
    @Transactional(readOnly = true)
    public int calcTopPercent(long userId) {
        long myExp = nz(userMapper.percentileFindExpByUserId(userId));
        long total = nz(userMapper.percentileCountAllUsers());
        if (total <= 0) return 0;

        Map<String, Object> m = userMapper.percentileCountLowerAndEqualByExp(myExp);
        long lower = toLong(m.get("lowerCnt")); // 내 exp보다 작은 사람 수
        long equal = toLong(m.get("equalCnt")); // 내 exp와 같은 사람 수(본인 포함)

        // 1등은 항상 1%
        boolean isTopGroup = (lower + equal) >= total;
        if (isTopGroup) return 1;

        // mid-rank 하위 백분위
        double percentile = ((lower + 0.5d * equal) / (double) total) * 100.0d;

        // 상위 퍼센트로 전환 (100 - percentile)
        int top = (int) Math.ceil(100.0d - percentile);

        // 표시 보정 (1~100)
        return Math.max(1, Math.min(100, top));
    }

    private long nz(Long v) { return v == null ? 0L : v; }
    private long toLong(Object v) { return v == null ? 0L : ((Number) v).longValue(); }
}
