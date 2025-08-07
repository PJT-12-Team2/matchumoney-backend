package team2.pjt12.matchumoney.domain.favorite.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import team2.pjt12.matchumoney.domain.favorite.domain.FavoriteVO;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.ProductType;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.List;

import static team2.pjt12.matchumoney.global.util.SecurityUtils.getCurrentUser;

@Service
@RequiredArgsConstructor
@Slf4j
public class FavoriteServiceImpl implements FavoriteService {

    private final UserMapper userMapper;

    /**
     * 사용자의 즐겨찾기 항목을 추가하는 메서드
     *
     * @param productId    즐겨찾기할 상품 ID
     * @param productType  상품 종류 (DEPOSIT, SAVING, CARD)
     */
    public void addFavorite(Long productId, ProductType productType) {
        // 현재 로그인한 사용자의 ID를 가져옴
        Long userId = getCurrentUser().getUserId();
        // 사용자 정보가 존재하는지 확인하고, 없으면 예외 발생
        UserVO user = userMapper.findByUserId(userId)
                .orElseThrow(() -> new CustomException(ErrorCode.USER_NOT_FOUND));
        // 상품 타입에 따라 즐겨찾기 추가 처리
        switch (productType) {
            case DEPOSIT -> {
                // 이미 해당 예금 상품이 즐겨찾기에 등록되어 있는지 확인
                if (userMapper.isDepositFavoriteExists(userId, productId)) {
                    // 중복일 경우 예외 발생
                    throw new CustomException(ErrorCode.FAVORITE_ALREADY_EXISTS);
                }
                // 중복이 아니라면 즐겨찾기에 추가
                userMapper.addDepositFavorite(userId, productId);
            }
            case SAVING -> {
                // 이미 해당 적금 상품이 즐겨찾기에 등록되어 있는지 확인
                if (userMapper.isSavingFavoriteExists(userId, productId)) {
                    throw new CustomException(ErrorCode.FAVORITE_ALREADY_EXISTS);
                }
                userMapper.addSavingFavorite(userId, productId);
            }
            case CARD -> {
                // 이미 해당 카드 상품이 즐겨찾기에 등록되어 있는지 확인
                if (userMapper.isCardFavoriteExists(userId, productId)) {
                    throw new CustomException(ErrorCode.FAVORITE_ALREADY_EXISTS);
                }
                userMapper.addCardFavorite(userId, productId);
            }
            // 정의되지 않은 상품 타입이 들어온 경우 예외 처리
            default -> throw new CustomException(ErrorCode.INVALID_PRODUCT_TYPE);
        }
    }



    @Transactional
    public void deleteFavorite(Long productId, ProductType productType) {
        Long userId = getCurrentUser().getUserId();
        userMapper.deleteFavorite(userId, productId, productType.name());
    }

    @Transactional(readOnly = true)
    public List<FavoriteVO> getFavorites() {
        Long userId = getCurrentUser().getUserId();
        return userMapper.getFavorites(userId);
    }
}
