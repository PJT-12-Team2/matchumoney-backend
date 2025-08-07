package team2.pjt12.matchumoney.domain.carddetail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.carddetail.dto.CardDetailResponseDTO;
import team2.pjt12.matchumoney.domain.carddetail.mapper.CardDetailMapper;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;

@Service
@RequiredArgsConstructor
public class CardDetailServiceImpl implements CardDetailService {

    private final CardDetailMapper cardDetailMapper;

    @Override
    public CardDetailResponseDTO getCardDetailById(Long userId, int id) {
        CardDetailResponseDTO product = cardDetailMapper.findCardDetailById(id);
        product.setUserId(userId);
        if (userId != null) {
            boolean isLiked = cardDetailMapper.isLikedByUser(userId, id);
            int likeCount = cardDetailMapper.countLikesByProductId(id);
            product.setLiked(isLiked);
            product.setLikeCount(likeCount);
        }
        return product;
    }

    @Override
    public LikeStatusResponseDTO isUserLikedCard(Long userId, int cardProductId) {
        boolean isLiked = cardDetailMapper.isLikedByUser(userId, cardProductId);
        if (isLiked) {
            cardDetailMapper.deleteUserLike(userId, cardProductId);
        } else {
            cardDetailMapper.insertUserLike(userId, cardProductId);
        }
        int likeCount = cardDetailMapper.countLikesByProductId(cardProductId);
        return new LikeStatusResponseDTO(!isLiked, likeCount);
    }
}