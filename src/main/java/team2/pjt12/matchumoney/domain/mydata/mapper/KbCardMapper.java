package team2.pjt12.matchumoney.domain.mydata.mapper;

import org.apache.ibatis.annotations.Mapper;
import team2.pjt12.matchumoney.domain.mydata.vo.CardHoldingVO;

import java.util.List;

@Mapper
public interface KbCardMapper {
    void insertKbCard(CardHoldingVO card);
    void deleteKbCardById(Long userId);
    List<CardHoldingVO> selectKbCardByUserId(Long userId);
}
