package team2.pjt12.matchumoney.domain.mydata.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.mydata.vo.CardInfoVO;

import java.util.List;

@Mapper
public interface KbCardMapper {
    void insertKbCard(CardInfoVO card);
    void deleteKbCardById(Long userId);
    List<CardInfoVO> selectKbCardByUserId(Long userId);
}
