package team2.pjt12.matchumoney.domain.depositdetail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.depositdetail.dto.DepositDetailResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.depositdetail.mapper.DepositDetailMapper;

@Service
@RequiredArgsConstructor
public class DepositDetailServiceImpl implements DepositDetailService {

    private final DepositDetailMapper depositDetailMapper;

    @Override
    public DepositDetailResponseDTO getDepositDetailById(Long userId,Long id) {
        DepositDetailResponseDTO product = depositDetailMapper.findDepositProductById(id);
        product.setOptions(depositDetailMapper.findOptionsByProductId(id));
        product.setUserId(userId);
        if (userId != null) {
            boolean isLiked = depositDetailMapper.isLikedByUser(userId, id);
            int likeCount = depositDetailMapper.countLikesByProductId(id);
            product.setLiked(isLiked);
            product.setLikeCount(likeCount);
        }
        return product;
    }

    @Override
    public LikeStatusResponseDTO isUserLikedDeposit(Long userId, Long depositProductId) {
        boolean isLiked = depositDetailMapper.isLikedByUser(userId, depositProductId);
        if (isLiked) {
            depositDetailMapper.deleteUserLike(userId, depositProductId);
        } else {
            depositDetailMapper.insertUserLike(userId, depositProductId);
        }
        int likeCount = depositDetailMapper.countLikesByProductId(depositProductId);
        return new LikeStatusResponseDTO(!isLiked, likeCount);
    }
}