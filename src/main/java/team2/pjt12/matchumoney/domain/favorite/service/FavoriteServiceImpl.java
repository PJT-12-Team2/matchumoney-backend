package team2.pjt12.matchumoney.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.ProductType;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    private final UserMapper userMapper;

    @Transactional
    public void addFavorite(Long productId, ProductType productType) {
        Long userId = getCurrentUser().getUserId();
        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));

        switch (productType) {
            case DEPOSIT -> {
                if (userMapper.isDepositFavoriteExists(userId, productId)) {
                    throw new CustomException(ErrorCode.FAVORITE_ALREADY_EXISTS);
                }
                userMapper.addDepositFavorite(userId, productId);
            }
            case SAVING -> {
                if (userMapper.isSavingFavoriteExists(userId, productId)) {
                    throw new CustomException(ErrorCode.FAVORITE_ALREADY_EXISTS);
                }
                userMapper.addSavingFavorite(userId, productId);
            }
            case CARD -> {
                if (userMapper.isCardFavoriteExists(userId, productId)) {
                    throw new CustomException(ErrorCode.FAVORITE_ALREADY_EXISTS);
                }
                userMapper.addCardFavorite(userId, productId);
            }
            default -> throw new CustomException(ErrorCode.INVALID_PRODUCT_TYPE);
        }
    }
}
