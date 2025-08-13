package team2.pjt12.matchumoney.domain.education.service;

import team2.pjt12.matchumoney.domain.education.dto.EduMovieResponseDTO;

import java.util.List;

public interface EduService {
    List<EduMovieResponseDTO> getEduMovieList(Long offset, int pageSize);
}
