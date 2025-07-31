package team2.pjt12.matchumoney.global.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;

import java.util.concurrent.Executor;

@Slf4j
@Configuration
@EnableAsync
public class AsyncConfig {

    @Bean(name = "cardRecommendationExecutor")
    public Executor cardRecommendationExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(2);
        executor.setMaxPoolSize(5);
        executor.setQueueCapacity(100);
        executor.setThreadNamePrefix("CardRecommendation-");
        executor.setRejectedExecutionHandler((r, executor1) -> {
            log.warn("카드 추천 재계산 작업이 거부되었습니다. 큐가 가득 참.");
        });
        executor.initialize();
        return executor;
    }
}