package team2.pjt12.matchumoney.domain.education.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.education.dto.EduMovieResponseDTO;
import team2.pjt12.matchumoney.domain.education.mapper.EduMapper;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class EduServiceImpl implements EduService {

    private final EduMapper eduMapper;

    //교육 영상 조회
    @Override
    @Transactional(readOnly = true)
    public List<EduMovieResponseDTO> getEduMovieList(Long offset, int pageSize) {

        return eduMapper.getEduMovieList(offset, pageSize);
    }
}
