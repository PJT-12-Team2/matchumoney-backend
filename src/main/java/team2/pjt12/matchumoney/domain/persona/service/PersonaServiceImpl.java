package team2.pjt12.matchumoney.domain.persona.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import team2.pjt12.matchumoney.domain.persona.dto.PersonaResponseDTO;
import team2.pjt12.matchumoney.domain.persona.dto.RecommendationDTO;
import team2.pjt12.matchumoney.domain.persona.mapper.PersonaMapper;
import team2.pjt12.matchumoney.global.exception.CustomException;
import team2.pjt12.matchumoney.global.exception.ErrorCode;

import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class PersonaServiceImpl implements PersonaService {
    private final PersonaMapper personaMapper;

    // 사용자의 페르소나 유형 code에 대한 상세 정보 반환
    @Override
    public PersonaResponseDTO getPersonaDetail(String code) {
        log.info("Fetching persona detail for code: {}", code);

        // 페르소나 정보 조회
        Map<String, Object> personaInfo = personaMapper.findPersonaInfoByCode(code);
        if (personaInfo == null) {
            throw new CustomException(ErrorCode.RESOURCE_NOT_FOUND);
        }

        // 페르소나 태그 목록 조회
        List<String> tags = personaMapper.findTagsByCode(code);

        // 페르소나 추천 목록 조회
        List<RecommendationDTO> recommendations = personaMapper.findRecommendationsByCode(code);

        // 조회한 데이터를 DTO로 변환 및 반환
        return PersonaResponseDTO.builder()
                .code(code)
                .nameKo((String) personaInfo.get("name_ko"))
                .quote((String) personaInfo.get("quote"))
                .userType((String) personaInfo.get("user_type"))
                .description((String) personaInfo.get("description"))
                .imageUrl((String) personaInfo.get("image_url"))
                .tags(tags)
                .recommendations(recommendations)
                .build();
    }
}