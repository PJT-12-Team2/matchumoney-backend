package team2.pjt12.matchumoney.domain.personadeposit.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositDTO;
import team2.pjt12.matchumoney.domain.personadeposit.dto.PersonaDepositResponseDTO;
import team2.pjt12.matchumoney.domain.personadeposit.mapper.PersonaDepositMapper;

import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaDepositServiceImpl implements PersonaDepositService {

    private final PersonaDepositMapper personaDepositMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonaDepositResponseDTO getRecommendedDeposit(Long personaId) {
        String personaName = personaDepositMapper.selectPersonaNameById(personaId);
        List<PersonaDepositDTO> recommendedDeposits = getRandomizedDeposits(personaId);

        return buildRecommendationResponse(personaName, recommendedDeposits);
    }

    // personaId에 맞는 예금 추천 3개
    private List<PersonaDepositDTO> getRandomizedDeposits(Long personaId) {
        List<PersonaDepositDTO> allDeposits = personaDepositMapper.selectDepositsByPersonaId(personaId);
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
}
