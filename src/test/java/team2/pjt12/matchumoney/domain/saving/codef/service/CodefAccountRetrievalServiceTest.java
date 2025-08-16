package team2.pjt12.matchumoney.domain.saving.codef.service;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import team2.pjt12.matchumoney.domain.saving.codef.CodefAccountRetrievalService;
import team2.pjt12.matchumoney.domain.saving.codef.CodefApiClient;
import team2.pjt12.matchumoney.domain.saving.codef.CodefConnectedIdProvider;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.mapper.SavingAccountMapper;
import team2.pjt12.matchumoney.domain.saving.util.SavingAccountConverter;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CodefAccountRetrievalServiceTest {

    @Mock
    CodefApiClient codefApiClient;
    @Mock
    CodefMapper codefMapper;
    @Mock
    SavingAccountMapper savingAccountMapper;
    @Mock
    CodefConnectedIdProvider codefConnectedIdProvider;
    @Mock
    SavingAccountConverter dataTransformService;

    @InjectMocks
    CodefAccountRetrievalService service;

    @BeforeEach
    void setUp() {
        // ✅ 1. UserVO mock 객체 생성
        UserVO mockUser = UserVO.builder()
                .userId(1L)
                .nickname("테스트유저")
                .email("test@example.com")
                .socialLogin(false)
                .build();

        // ✅ 2. principal에 UserVO 삽입하여 인증 정보 설정
        UsernamePasswordAuthenticationToken authentication =
                new UsernamePasswordAuthenticationToken(mockUser, null, null);
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // ✅ 3. SecurityUtils의 static 메서드 mocking
        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(1L);
        }
    }


    @Test
    void getBanksByConnectedId_success() {
        when(codefMapper.selectOrganizationNamesByUserId(1L)).thenReturn(List.of("004", "089"));

        List<String> result = service.getBanksByConnectedId();

        assertThat(result).containsExactly("004", "089");
        verify(codefMapper).selectOrganizationNamesByUserId(1L);
    }

    @Test
    void getConnectedIdList_success() {
        String token = "access-token";
        ObjectMapper mapper = new ObjectMapper();

        // 정확한 구조로 JsonNode 만들기
        ObjectNode mockResponse = mapper.createObjectNode();
        mockResponse.putObject("result").put("code", "CF-00000");

        ObjectNode dataNode = mapper.createObjectNode();
        dataNode.put("connectedId", "cid");
        mockResponse.set("data", dataNode);

        when(codefApiClient.getAccessToken()).thenReturn(token);
        when(codefApiClient.postJson(anyString(), eq(token), anyString())).thenReturn(mockResponse);

        JsonNode result = service.getConnectedIdList();

        assertThat(result).isNotNull();
        assertThat(result.path("connectedId").asText()).isEqualTo("cid");
    }

    @Test
    void getOrganizationCodes_success() {
        String cid = "cid";
        String token = "access-token";
        ObjectMapper mapper = new ObjectMapper();

        ObjectNode mockResponse = mapper.createObjectNode();
        mockResponse.putObject("result").put("code", "CF-00000");

        // accountList 구성
        ObjectNode data = mapper.createObjectNode();
        data.putArray("accountList")
                .add(mapper.createObjectNode().put("organization", "004"))
                .add(mapper.createObjectNode().put("organization", "081"));
        mockResponse.set("data", data);

        when(codefApiClient.getAccessToken()).thenReturn(token);
        when(codefApiClient.postJson(anyString(), eq(token), anyString())).thenReturn(mockResponse);

        List<String> result = service.getOrganizationCodes(cid);

        assertThat(result).containsExactly("004", "081");
    }

    @Test
    void updateConnectedId_success() throws Exception {
        String token = "access-token";
        String connectedId = "cid";
        BankLoginRequestDTO dto = new BankLoginRequestDTO("id", "pw", "004", "900101");
        List<MySavingProductResponseDTO> mockList = List.of(
                MySavingProductResponseDTO.builder().title("title1").build()
        );

        when(codefMapper.getCodefConnectedIdByUserId(1L)).thenReturn(connectedId);
        when(codefApiClient.getAccessToken()).thenReturn(token);

        // 수정된 부분: 반환값이 존재할 경우 when-thenReturn
        when(codefConnectedIdProvider.updateAccountByConnectedId(any(), any(), any(), any(), any(), eq(connectedId)))
                .thenReturn(null); // 실제 반환 타입에 맞게 수정

        when(savingAccountMapper.getSavingAccountList(1L)).thenReturn(mockList);

        List<MySavingProductResponseDTO> result = service.updateConnectedId(dto);

        assertThat(result).hasSize(1);
        verify(codefConnectedIdProvider).updateAccountByConnectedId(any(), any(), any(), any(), any(), eq(connectedId));
    }

    @Test
    void updateConnectedId_noConnectedId_throws() {
        when(codefMapper.getCodefConnectedIdByUserId(1L)).thenReturn(null);
        BankLoginRequestDTO dto = new BankLoginRequestDTO("id", "pw", "004", "900101");

        assertThrows(Exception.class, () -> service.updateConnectedId(dto));
    }
}
