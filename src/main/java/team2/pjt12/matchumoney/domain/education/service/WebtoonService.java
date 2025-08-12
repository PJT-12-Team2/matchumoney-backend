package team2.pjt12.matchumoney.domain.education.service;

import team2.pjt12.matchumoney.domain.education.dto.WebtoonResponseDTO;
import java.util.List;

public interface WebtoonService {

    /**
     * 전체 웹툰 목록 조회
     * @return 웹툰 응답 DTO 목록
     */
    List<WebtoonResponseDTO> getAllWebtoons();


    List<WebtoonResponseDTO> getTop4Webtoons();



}