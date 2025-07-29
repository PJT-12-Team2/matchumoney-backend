package team2.pjt12.matchumoney.domain.personadeposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.mapper.PersonadepositMapper;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class PersonadepositServiceImpl implements PersonadepositService {

    private final UserMapper userMapper;
    private final PersonadepositMapper personadepositMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    @Override
    public PersonaDepositResponseDTO getRecommendedDeposit(Long userId) {
        Long personaId = userMapper.selectPersonaIdByUserId(userId);
        String personaName = personadepositMapper.selectPersonaNameById(personaId);
        List<PersonaDepositDTO> recommendedDeposits = getRandomizedDeposits(personaId);

        return buildRecommendationResponse(personaName, recommendedDeposits);
    }

    // personaId에 맞는 예금 추천 3개
    private List<PersonaDepositDTO> getRandomizedDeposits(Long personaId) {
        List<PersonaDepositDTO> allDeposits = personadepositMapper.selectDepositsByPersonaId(personaId);
        Collections.shuffle(allDeposits);
        return allDeposits.stream()
                .limit(RECOMMENDATION_LIMIT)
                .collect(Collectors.toList());
    }

    // build
    private PersonaDepositResponseDTO buildRecommendationResponse(
            String personaName, List<PersonaDepositDTO> deposits) {
        return PersonaDepositResponseDTO.builder()
                .personaName(personaName)
                .deposits(deposits)
                .build();
    }
}
