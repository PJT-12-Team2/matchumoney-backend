package team2.pjt12.matchumoney.domain.education.service;

import team2.pjt12.matchumoney.domain.education.domain.WebtoonVO;
import team2.pjt12.matchumoney.domain.education.dto.WebtoonResponseDTO;
import team2.pjt12.matchumoney.domain.education.mapper.WebtoonMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
@Transactional(readOnly = true)
public class WebtoonServiceImpl implements WebtoonService {

    private final WebtoonMapper webtoonMapper;

    /**
     * 전체 웹툰 목록 조회
     * @return 웹툰 응답 DTO 목록
     */
    @Override
    public List<WebtoonResponseDTO> getAllWebtoons() {
        log.info("전체 웹툰 목록 조회 시작");

        try {
            // 웹툰 메인 이미지 목록 조회 (fileSn=1)
            List<WebtoonVO> webtoons = webtoonMapper.findWebtoonMain();

            // WebtoonVO를 WebtoonResponseDTO로 변환
            List<WebtoonResponseDTO> result = webtoons.stream()
                    .map(WebtoonResponseDTO::from)
                    .collect(Collectors.toList());

            log.info("전체 웹툰 목록 조회 완료. 조회된 웹툰 수: {}", result.size());
            return result;

        } catch (Exception e) {
            log.error("웹툰 목록 조회 중 오류 발생", e);
            throw new RuntimeException("웹툰 목록 조회에 실패했습니다.", e);
        }
    }

    @Override
    public List<WebtoonResponseDTO> getTop4Webtoons() {
        log.info("웹툰 top4 리스트 조회 시작");

        try {
            // 웹툰 메인 이미지 목록 조회 (fileSn=1)
            List<WebtoonVO> webtoons = webtoonMapper.findWebtoonMainTop4();

            // WebtoonVO를 WebtoonResponseDTO로 변환
            List<WebtoonResponseDTO> result = webtoons.stream()
                    .map(WebtoonResponseDTO::from)
                    .collect(Collectors.toList());

            log.info("웹툰 top4 리스트 조회 완료");
            return result;

        } catch (Exception e) {
            log.error("웹툰 목록 top4 조회 중 오류 발생", e);
            throw new RuntimeException("웹툰 목록 top4 조회에 실패했습니다.", e);
        }
    }
}