package team2.pjt12.matchumoney.domain.personadeposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonadepositDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonadepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.mapper.PersonadepositMapper;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonadepositServiceImpl implements PersonadepositService {

    private final PersonadepositMapper personadepositMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonadepositResponseDTO getRecommendedDeposit(Long personaId) {
        String personaName = personadepositMapper.selectPersonaNameById(personaId);
        List<PersonadepositDTO> recommendedDeposits = getRandomizedDeposits(personaId);

        return buildRecommendationResponse(personaName, recommendedDeposits);
    }

    // personaId에 맞는 예금 추천 3개
    private List<PersonadepositDTO> getRandomizedDeposits(Long personaId) {
        List<PersonadepositDTO> allDeposits = personadepositMapper.selectDepositsByPersonaId(personaId);
        Collections.shuffle(allDeposits);
        return allDeposits.stream()
                .limit(RECOMMENDATION_LIMIT).toList();
    }

    // build
    private PersonadepositResponseDTO buildRecommendationResponse(
            String personaName, List<PersonadepositDTO> deposits) {
        return PersonadepositResponseDTO.builder()
                .personaName(personaName)
                .deposits(deposits)
                .build();
    }
}
