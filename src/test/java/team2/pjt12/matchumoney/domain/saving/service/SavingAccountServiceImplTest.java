package team2.pjt12.matchumoney.domain.saving.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonNode;
import org.apache.ibatis.session.RowBounds;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import team2.pjt12.matchumoney.domain.saving.codef.CodefApiClient;
import team2.pjt12.matchumoney.domain.saving.codef.CodefConnectedIdProvider;
import team2.pjt12.matchumoney.domain.saving.codef.mapper.CodefMapper;
import team2.pjt12.matchumoney.domain.saving.codef.service.CodefAccountRetrievalService;
import team2.pjt12.matchumoney.domain.saving.domain.SavingAccountVO;
import team2.pjt12.matchumoney.domain.saving.dto.BankLoginRequestDTO;
import team2.pjt12.matchumoney.domain.saving.dto.MySavingProductResponseDTO;
import team2.pjt12.matchumoney.domain.saving.dto.SavingListItemResponseDTO;
import team2.pjt12.matchumoney.domain.saving.mapper.SavingAccountMapper;
import team2.pjt12.matchumoney.domain.saving.util.SavingAccountConverter;
import team2.pjt12.matchumoney.domain.user.domain.UserVO;
import team2.pjt12.matchumoney.global.util.SecurityUtils;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class SavingAccountServiceImplTest {

    private final Long userId = 1L;

    @Mock
    private SavingAccountMapper savingAccountMapper;
    @Mock
    private CodefApiClient codefApiClient;
    @Mock
    private CodefConnectedIdProvider codefConnectedIdProvider;
    @Mock
    private CodefAccountRetrievalService codefAccountRetrievalService;
    @Mock
    private SavingAccountConverter dataTransformService;
    @Mock
    private CodefMapper codefMapper;
    @InjectMocks
    private SavingAccountServiceImpl savingAccountService;


    @BeforeEach
    void setUp() {
        // ✅ 1. UserVO mock 객체 생성
        UserVO mockUser = UserVO.builder()
                .userId(userId)
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
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(userId);
        }
    }

    @Test
    void testGetSavingAccountList() {
        UserVO mockUser = UserVO.builder()
                .userId(userId)
                .nickname("테스트유저")
                .email("test@example.com")
                .socialLogin(false)
                .build();

        try (MockedStatic<SecurityUtils> mockedStatic = mockStatic(SecurityUtils.class)) {
            mockedStatic.when(SecurityUtils::getCurrentUser).thenReturn(mockUser);
            mockedStatic.when(SecurityUtils::getCurrentUserId).thenReturn(userId);

            // given
            MySavingProductResponseDTO dto = new MySavingProductResponseDTO();
            when(savingAccountMapper.getSavingAccountList(anyLong()))
                    .thenReturn(Collections.singletonList(dto));

            // when
            List<MySavingProductResponseDTO> result = savingAccountService.getSavingAccountList();

            // then
            assertThat(result).hasSize(1);
            verify(savingAccountMapper).getSavingAccountList(anyLong());
        }
    }


    @Test
    void testRetrieveAccounts_NewConnectedId() throws JsonProcessingException {
        BankLoginRequestDTO request = new BankLoginRequestDTO();
        request.setId("testId");
        request.setPassword("pw");
        request.setBankCode("004");
        request.setBirthDate("19990101");

        when(codefApiClient.getAccessToken()).thenReturn("token");
        when(codefMapper.getCodefConnectedIdByUserId(anyLong())).thenReturn(null);
        when(codefConnectedIdProvider.createConnectedId(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn("newConnectedId");
        when(savingAccountMapper.getSavingAccountList(anyLong()))
                .thenReturn(Collections.emptyList());

        List<MySavingProductResponseDTO> result = savingAccountService.retrieveAccounts(request);

        assertThat(result).isEmpty();
        verify(codefMapper).insertCodefConnectedId(any());
        verify(codefMapper).insertCodefConnectedIdOrganization(anyString(), anyString());
    }

    @Test
    void testRetrieveAccountsPre() {
        when(codefApiClient.getAccessToken()).thenReturn("token");
        when(codefMapper.getCodefConnectedIdByUserId(anyLong())).thenReturn("connectedId");
        when(codefAccountRetrievalService.getOrganizationCodes(anyString()))
                .thenReturn(Arrays.asList("004", "007"));
        when(savingAccountMapper.getSavingAccountList(anyLong())).thenReturn(Collections.emptyList());

        List<MySavingProductResponseDTO> result = savingAccountService.retrieveAccountsPre();

        assertThat(result).isEmpty();
        verify(codefAccountRetrievalService).getOrganizationCodes("connectedId");
    }

    @Test
    void testProcessTransactionHistory() {
        JsonNode node = mock(JsonNode.class);
        SavingAccountVO vo = new SavingAccountVO();
        when(codefAccountRetrievalService.retrieveTransactionHistory(anyString(), anyString(), anyString(), anyString(), anyString()))
                .thenReturn(node);
        when(dataTransformService.transformToVO(any(JsonNode.class), anyLong(), anyLong()))
                .thenReturn(vo);

        boolean result = savingAccountService.processTransactionHistory("t", "cid", "acc", 1L, 2L, "1990", "004");

        assertThat(result).isTrue();
        verify(savingAccountMapper).insertSavingAccount(vo);
    }

    @Test
    void testGetUserRecommendedSavingAccounts_Default() {
        when(savingAccountMapper.getRecommendDefaultSavingAccountList(anyLong(), any(RowBounds.class)))
                .thenReturn(Collections.singletonList(new SavingListItemResponseDTO()));

        List<SavingListItemResponseDTO> result = savingAccountService.getUserRecommendedSavingAccounts(-1L, 0, 10);

        assertThat(result).hasSize(1);
        verify(savingAccountMapper).getRecommendDefaultSavingAccountList(anyLong(), any(RowBounds.class));
    }

    @Test
    void testDeleteConnectedId_Success() {
        when(codefMapper.getCodefConnectedIdByUserId(anyLong())).thenReturn("cid");
        doNothing().when(codefAccountRetrievalService).deleteConnectedId("cid");
        when(codefMapper.deleteCodefConnectedIdByUserId(anyLong())).thenReturn(true);

        String result = savingAccountService.deleteConnectedId();

        assertThat(result).contains("cid");
        verify(codefMapper).deleteCodefConnectedIdByUserId(anyLong());
    }

    @Test
    void testDeleteConnectedId_Fail_NoConnectedId() {
        when(codefMapper.getCodefConnectedIdByUserId(anyLong())).thenReturn(null);

        assertThrows(RuntimeException.class, () -> savingAccountService.deleteConnectedId());
    }
}
