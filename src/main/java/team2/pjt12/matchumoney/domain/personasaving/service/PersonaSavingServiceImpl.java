package team2.pjt12.matchumoney.domain.personasaving.service;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingDTO;
import team2.pjt12.matchumoney.domain.personasaving.dto.PersonaSavingResponseDTO;

import team2.pjt12.matchumoney.domain.personasaving.mapper.PersonaSavingMapper;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import java.util.Collections;
import java.util.List;

@Service
@RequiredArgsConstructor
public class PersonaSavingServiceImpl implements PersonaSavingService {
    private final PersonaSavingMapper personasavingMapper;
    private final UserMapper userMapper;
    private static final int RECOMMENDATION_LIMIT = 3;

    // personaId로 personaName 조회
    @Override
    public PersonaSavingResponseDTO getRecommendedSaving(Long personaId) {
        String personaName = personasavingMapper.selectPersonaNameById(personaId);
        List<PersonaSavingDTO> recommendedSavings = getRandomizedSavings(personaId);

        return buildRecommendationResponse(personaName, recommendedSavings);
    }

    // personaId에 맞는 적금 추천 3개
    private List<PersonaSavingDTO> getRandomizedSavings(Long personaId) {
        List<PersonaSavingDTO> allSavings = personasavingMapper.selectSavingsByPersonaId(personaId);
        Collections.shuffle(allSavings);
        return allSavings.stream()
                .limit(RECOMMENDATION_LIMIT).toList();
    }

    // build
    private PersonaSavingResponseDTO buildRecommendationResponse(
            String personaName, List<PersonaSavingDTO> savings) {
        return PersonaSavingResponseDTO.builder()
                .personaName(personaName)
                .savings(savings)
                .build();
    }
    @Override
    public Long getPersonaIdByUserId(Long userId) {
        return userMapper.findByUserId(userId)
                .map(UserVO::getPersonaId)
                .orElseThrow(() -> new RuntimeException("해당 유저의 persona_id를 찾을 수 없습니다."));
    }
}
