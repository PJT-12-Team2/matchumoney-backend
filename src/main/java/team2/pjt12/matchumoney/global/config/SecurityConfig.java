package team2.pjt12.matchumoney.global.config;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import team2.pjt12.matchumoney.domain.user.mapper.UserMapper;
import team2.pjt12.matchumoney.global.jwt.JwtAuthenticationFilter;
import team2.pjt12.matchumoney.global.jwt.JwtLogoutHandler;
import team2.pjt12.matchumoney.global.jwt.JwtService;
import org.springframework.security.crypto.password.DelegatingPasswordEncoder;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;
import java.util.List;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@Slf4j
public class SecurityConfig {
    private final JwtLogoutHandler jwtLogoutHandler;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RedisTemplate<String, String> redisTemplate;

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers(
                                // 문서/정적
                                "/swagger-ui/**", "/swagger-ui.html", "/v2/api-docs",
                                "/swagger-resources/**", "/v3/api-docs/**",
                                "/webjars/**", "/static/**",
                                "/kakao_login_medium_narrow.png", "/page/login",

                                // OAuth 콜백/연동
                                "/oauth/**",

                                // ===== 공개 Auth 엔드포인트만 정확히 지정 =====
                                "/api/auth/login",
                                "/api/auth/kakao-login",
                                "/api/auth/signup",
                                "/api/auth/signup/email/send",
                                "/api/auth/email/verify",
                                "/api/auth/reset/**",

                                // (필요 시) 비회원도 조회 가능한 공개 API들만 여기에 추가
                                "/api/chatbot",
                                "/api/persona/**",
                                "/api/saving/**",
                                "/api/persona-saving/recommendation",
                                "/api/deposits/**",
                                "/api/deposit/**",
                                "/api/cards/**",
                                "/api/deposit-products/**",
                                "/api/saving-products/**",
                                "/api/card-products/**",
                                "/api/like/**",
                                "/api/favorite/**",
                                "/api/webtoon", "api/webtoon/**"
                        ).permitAll()

                        // ===== 인증 필요한 엔드포인트 =====
                        // 비밀번호 검증은 반드시 인증 필요
                        .requestMatchers("/api/auth/verify/password").authenticated()
                        // (예: 사용자 정보 수정/조회 등)
                        .requestMatchers("/api/user/**").authenticated()
                        .requestMatchers("/user/update").authenticated()

                        // 그 외는 모두 인증 필요
                        .anyRequest().authenticated()
                )
                .csrf(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(logout -> logout
                        .addLogoutHandler(jwtLogoutHandler)
                        .logoutSuccessHandler((req, res, auth) -> res.setStatus(HttpServletResponse.SC_OK)))
                .sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.asList("*"));
        configuration.setAllowedMethods(Arrays.asList("GET", "POST", "PUT","PATCH", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드
        configuration.setAllowedHeaders(Arrays.asList("*")); // 모든 헤더 허용
        configuration.setExposedHeaders(Arrays.asList("Authorization"));
        configuration.setAllowCredentials(true); // 자격 증명 허용

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration); // 모든 경로에 대해 CORS 설정 적용
        return source;
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        Map<String, PasswordEncoder> encoders = new HashMap<>();
        encoders.put("bcrypt", new BCryptPasswordEncoder());

        DelegatingPasswordEncoder dpe = new DelegatingPasswordEncoder("bcrypt", encoders);
        // DB 값에 {bcrypt} 프리픽스가 없어도 bcrypt로 매칭되도록
        dpe.setDefaultPasswordEncoderForMatches(new BCryptPasswordEncoder());
        return dpe;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration authenticationConfiguration) throws Exception {
        return authenticationConfiguration.getAuthenticationManager();
    }

    @Bean
    public JwtAuthenticationFilter jwtAuthenticationFilter() {
        return new JwtAuthenticationFilter(jwtService, redisTemplate, userMapper);
    }
}

