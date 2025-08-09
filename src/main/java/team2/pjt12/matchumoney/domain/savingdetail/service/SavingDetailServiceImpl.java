package team2.pjt12.matchumoney.domain.savingdetail.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.savingdetail.dto.SavingDetailResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.dto.LikeStatusResponseDTO;
import team2.pjt12.matchumoney.domain.savingdetail.mapper.SavingDetailMapper;

@Service
@RequiredArgsConstructor
public class SavingDetailServiceImpl implements SavingDetailService {

    private final SavingDetailMapper savingDetailMapper;

    @Override
    public SavingDetailResponseDTO getSavingDetailById(Long userId, Long id) {
        SavingDetailResponseDTO product = savingDetailMapper.findSavingProductById(id, userId);
        product.setOptions(savingDetailMapper.findOptionsByProductId(product.getFinPrdtCd()));
        product.setUserId(userId);
        if (userId != null) {
            boolean isLiked = savingDetailMapper.isLikedByUser(userId, id);
            int likeCount = savingDetailMapper.countLikesByProductId(id);
            product.setLiked(isLiked);
            product.setLikeCount(likeCount);
        }
        return product;
    }

    @Override
    public LikeStatusResponseDTO isUserLikedSaving(Long userId, Long savingProductId) {
        boolean isLiked = savingDetailMapper.isLikedByUser(userId, savingProductId);
        if (isLiked) {
            savingDetailMapper.deleteUserLike(userId, savingProductId);
        } else {
            savingDetailMapper.insertUserLike(userId, savingProductId);
        }
        int likeCount = savingDetailMapper.countLikesByProductId(savingProductId);
        return new LikeStatusResponseDTO(!isLiked, likeCount);
    }
}