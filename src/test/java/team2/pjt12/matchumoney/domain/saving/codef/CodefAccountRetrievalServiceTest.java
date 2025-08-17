package team2.pjt12.matchumoney.domain.saving.codef;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import team2.pjt12.matchumoney.domain.saving.codef.service.CodefAccountRetrievalService;

class CodefAccountRetrievalServiceTest {
    @InjectMocks
    public static CodefAccountRetrievalService codefAccountRetrievalService;

    @Test
    void getConnectedIdList() {
        codefAccountRetrievalService.getConnectedIdList();
    }
}