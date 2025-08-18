package team2.pjt12.matchumoney.domain.education.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import team2.pjt12.matchumoney.domain.education.dto.EduMovieResponseDTO;

import java.util.List;

@Api(tags = "Education API", description = "교육자료 API")
@RequestMapping("/api/education")
public interface EducationApi {

    @ApiOperation(value = "교육 영상 조회", notes = "DB에 저장된 교육 영상 정보를 조회합니다.")
    @GetMapping("/movie")
    ResponseEntity<List<EduMovieResponseDTO>> getEducationVideos(Long offset, int pageSize);


}
