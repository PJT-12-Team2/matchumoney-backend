package team2.pjt12.matchumoney.domain.deposit.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.deposit.domain.UserDepositVO;
import team2.pjt12.matchumoney.domain.deposit.dto.req.BalanceRequestDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.DepositProductResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.dto.res.UserDepositResponseDTO;
import team2.pjt12.matchumoney.domain.deposit.mapper.DepositProductMapper;
import team2.pjt12.matchumoney.domain.deposit.mapper.UserDepositMapper;
import team2.pjt12.matchumoney.domain.deposit.util.AmountExtractorUtil; // 🆕 추가

import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class UserDepositServiceImpl implements UserDepositService {
    private final UserDepositMapper userDepositMapper;
    private final DepositProductMapper depositProductMapper;

    @Override
    public List<UserDepositResponseDTO> getAccountsByUserId(String userId) {
        List<UserDepositVO> userDepositVOList = userDepositMapper.getAccountsByUserId(userId);

        // 계좌가 없는 사용자 처리
        if (userDepositVOList.isEmpty()) {
            log.warn("계좌가 없는 사용자: userId={}", userId);
        }

        // VO -> DTO 변환
        List<UserDepositResponseDTO> userDepositResponseDTOList = userDepositVOList.stream()
                .map(UserDepositResponseDTO::from)
                .collect(Collectors.toList());

        return userDepositResponseDTOList;
    }


}