package team2.pjt12.matchumoney.domain.education.controller;


import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.RestController;
import team2.pjt12.matchumoney.domain.education.dto.EduMovieResponseDTO;
import team2.pjt12.matchumoney.domain.education.service.EduService;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class EducationController implements EducationApi {

    private final EduService eduService;

    @Override
    public ResponseEntity<List<EduMovieResponseDTO>> getEducationVideos(Long offset, int pageSize) {
        List<EduMovieResponseDTO> eduMovieList = eduService.getEduMovieList(offset, pageSize);
        return ResponseEntity.ok(eduMovieList); // JSON 형식으로 반환
    }
}
