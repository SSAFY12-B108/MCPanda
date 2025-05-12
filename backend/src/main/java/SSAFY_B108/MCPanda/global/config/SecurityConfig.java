package SSAFY_B108.MCPanda.global.config;

import SSAFY_B108.MCPanda.global.jwt.JwtAuthenticationFilter;
import SSAFY_B108.MCPanda.global.jwt.JwtProvider;
import SSAFY_B108.MCPanda.global.oauth2.handler.OAuth2SuccessHandler;
import SSAFY_B108.MCPanda.global.oauth2.service.CustomOAuth2UserService;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Spring Security 관련 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰 생성 및 검증을 담당하는 객체
    private final JwtProvider jwtProvider;

    // OAuth2 사용자 로딩을 담당하는 서비스
    private final CustomOAuth2UserService customOAuth2UserService;

    // OAuth2 인증 성공 시 처리를 담당하는 핸들러
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화(JWT 사용으로 불필요)
                .sessionManagement(session -> session // 세션을 생성하지 않고 상태를 저장하지 않음(JWT 기반 인증에 적합)
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 접근 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/").permitAll()

                        // OAuth2 인증 관련 엔드포인트 허용
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/login/oauth2/code/**").permitAll()

                        // 인증 필요 없는 API 엔드포인트
                        .requestMatchers(HttpMethod.GET, "/api/main").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/main/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articles").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/articles/**").permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/history/**").permitAll()  // 디버깅용

                        // 나머지 요청은 인증 필요
                        .anyRequest().authenticated()
                )
                // JWT 인증 필터 추가 (UsernamePasswordAuthenticationFilter 이전에 실행)
                .addFilterBefore(
                        new JwtAuthenticationFilter(jwtProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 페이지 URL
                        .loginPage("/api/auth/login")
                        // 사용자 정보를 가져오는 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        // 인증 성공 시 처리할 핸들러
                        .successHandler(oAuth2SuccessHandler)
                        // OAuth2 인증 요청 엔드포인트 설정
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/auth/oauth2/authorization"))
                        // OAuth2 콜백 URL 설정
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/api/auth/login/oauth2/code/*"))
                )
                // 로그아웃 설정
                .logout(logout -> logout
                        // 로그아웃 URL
                        .logoutUrl("/api/auth/logout")
                        // 로그아웃 성공 시 쿠키 삭제
                        .deleteCookies("accessToken")
                        // 로그아웃 성공 시 리다이렉트할 URL
                        .logoutSuccessUrl("/")
                        // 세션 무효화
                        .invalidateHttpSession(true)
                        // 인증 정보 제거
                        .clearAuthentication(true));

        return http.build();
    }
}