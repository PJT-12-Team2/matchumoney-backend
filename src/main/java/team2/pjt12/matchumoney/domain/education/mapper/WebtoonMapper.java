package team2.pjt12.matchumoney.domain.education.mapper;

import team2.pjt12.matchumoney.domain.education.domain.WebtoonVO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import java.util.List;

@Mapper
public interface WebtoonMapper {

    /**
     * 전체 웹툰 목록 조회
     * @return 전체 웹툰 목록 추출
     */
    List<WebtoonVO> findWebtoonMain();

    /**
     * 전체 웹툰 목록 조회
     * @return 전체 웹툰 목록 중 상위 4개 추출
     */
    List<WebtoonVO> findWebtoonMainTop4();

}