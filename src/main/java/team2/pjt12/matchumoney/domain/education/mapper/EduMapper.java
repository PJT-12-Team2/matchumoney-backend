package team2.pjt12.matchumoney.domain.education.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import team2.pjt12.matchumoney.domain.education.dto.EduMovieResponseDTO;

import java.util.List;

@Mapper
public interface EduMapper {
    List<EduMovieResponseDTO> getEduMovieList(@Param("offset") Long offset,
                                              @Param("pageSize") int pageSize);

    long countEduMovie();
}
