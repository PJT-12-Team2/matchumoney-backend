package team2.pjt12.matchumoney.domain.push.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.push.dto.NewProductResponseDTO;

import java.time.LocalDateTime;
import java.util.List;

@Mapper
public interface ProductScanMapper {
    List<NewProductResponseDTO> findNewCards(@Param("since") LocalDateTime since);
    List<NewProductResponseDTO> findNewDeposits(@Param("since") LocalDateTime since);
    List<NewProductResponseDTO> findNewSavings(@Param("since") LocalDateTime since);

    List<Long> findUserIdsByPersona(@Param("personaId") Long personaId);

    // 디듀프 확인/기록
    int existsPushLog(@Param("userId") Long userId,
                      @Param("type") String type,
                      @Param("productId") Long productId);
    int insertPushLog(@Param("userId") Long userId,
                      @Param("type") String type,
                      @Param("productId") Long productId);
}