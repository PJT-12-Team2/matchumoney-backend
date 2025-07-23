package team2.pjt12.matchumoney.domain.mydata.service;


import team2.pjt12.matchumoney.domain.mydata.vo.CardInfoVO;

import java.util.List;

public interface KbCardService {
    List<CardInfoVO> syncAndSaveCards(Long userId, String kbId, String kbPw) throws Exception;
    List<CardInfoVO> getCards(Long userId);
}
