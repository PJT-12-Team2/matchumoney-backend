package team2.pjt12.matchumoney.domain.push.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDateTime;

@Mapper
public interface WatermarkMapper {
    LocalDateTime getLastChecked(@Param("table") String table);
    int upsertLastChecked(@Param("table") String table, @Param("ts") LocalDateTime ts);
}