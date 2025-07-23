package team2.pjt12.matchumoney.domain.mydata.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.mydata.mapper.KbCardMapper;
import team2.pjt12.matchumoney.domain.mydata.util.KBCardApiUtil;
import team2.pjt12.matchumoney.domain.mydata.vo.CardInfoVO;

import java.util.List;

@Service
public class KbCardServiceImpl implements KbCardService {

    @Autowired
    private KbCardMapper kbCardMapper;

    @Autowired
    private KBCardApiUtil kbCardApiUtil;


    @Override
    public List<CardInfoVO> syncAndSaveCards(Long userId, String kbId, String kbPw) throws Exception {
        List<CardInfoVO> cards = kbCardApiUtil.fetchKbCards(kbId, kbPw, userId);

        kbCardMapper.deleteKbCardById(userId);
        for (CardInfoVO card : cards) {
            kbCardMapper.insertKbCard(card);
        }
        return cards;
    }

    @Override
    public List<CardInfoVO> getCards(Long userId) {
        return kbCardMapper.selectKbCardByUserId(userId);
    }
}
