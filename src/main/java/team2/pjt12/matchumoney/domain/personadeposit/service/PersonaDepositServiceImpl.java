package team2.pjt12.matchumoney.domain.personadeposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.mapper.PersonaDepositMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaDepositServiceImpl implements PersonaDepositService {

    private final PersonaDepositMapper personadepositMapper;
    private final UserMapper userMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonaDepositResponseDTO getRecommendedDeposit(Long personaId) {
        String personaName = personadepositMapper.selectPersonaNameById(personaId);
        List<PersonaDepositDTO> recommendedDeposits = getRandomizedDeposits(personaId);

        return buildRecommendationResponse(personaName, recommendedDeposits);
    }

    // personaId에 맞는 예금 추천 3개
    private List<PersonaDepositDTO> getRandomizedDeposits(Long personaId) {
        List<PersonaDepositDTO> allDeposits = personadepositMapper.selectDepositsByPersonaId(personaId);
        Collections.shuffle(allDeposits);
        return allDeposits.stream()
                .limit(RECOMMENDATION_LIMIT).toList();
    }

    // build
    private PersonaDepositResponseDTO buildRecommendationResponse(
            String personaName, List<PersonaDepositDTO> deposits) {
        return PersonaDepositResponseDTO.builder()
                .personaName(personaName)
                .deposits(deposits)
                .build();
    }

    @Override
    public Long getPersonaIdByUserId(Long userId) {
        return userMapper.findByUserId(userId)
                .map(UserVO::getPersonaId)
                .orElseThrow(() -> new RuntimeException("해당 유저의 persona_id를 찾을 수 없습니다."));
    }
}
