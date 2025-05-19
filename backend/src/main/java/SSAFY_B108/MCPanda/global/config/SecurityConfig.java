package SSAFY_B108.MCPanda.global.config;

import SSAFY_B108.MCPanda.domain.auth.handler.CustomLogoutHandler; // Added import
import SSAFY_B108.MCPanda.global.jwt.JwtTokenAuthenticationFilter;
import SSAFY_B108.MCPanda.global.jwt.JwtTokenProvider;
import SSAFY_B108.MCPanda.domain.auth.oauth2.handler.OAuth2LoginSuccessHandler;
import SSAFY_B108.MCPanda.domain.auth.oauth2.service.CustomOAuth2UserService;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

/**
 * Spring Security 관련 설정
 */
@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SecurityConfig {

    // JWT 토큰 생성 및 검증을 담당하는 객체
    private final JwtTokenProvider jwtTokenProvider;

    // OAuth2 사용자 로딩을 담당하는 서비스
    private final CustomOAuth2UserService customOAuth2UserService;

    // OAuth2 인증 성공 시 처리를 담당하는 핸들러
    private final OAuth2LoginSuccessHandler oAuth2LoginSuccessHandler;
    private final CustomLogoutHandler customLogoutHandler; // Added CustomLogoutHandler

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring()
                .requestMatchers(
                        "/favicon.ico",
                        "/static/**",
                        "/css/**",
                        "/js/**",
                        "/images/**",
                        "/.well-known/appspecific/com.chrome.devtools.json"
                );
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(cors -> cors.configurationSource(corsConfigurationSource()))
                .csrf(csrf -> csrf.disable()) // CSRF 보호 비활성화(JWT 사용으로 불필요)
                .sessionManagement(session -> session // 세션을 생성하지 않고 상태를 저장하지 않음(JWT 기반 인증에 적합)
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 접근 허용
                        .requestMatchers(
                                "/",
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**").permitAll()

                        // OAuth2 인증 관련 엔드포인트 허용
                        .requestMatchers(
                                "/api/auth/login",
                                "/api/auth/reissue", // 토큰 재발급 URL 추가
                                "/oauth2/authorization/**",
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
                        new JwtTokenAuthenticationFilter(jwtTokenProvider),
                        UsernamePasswordAuthenticationFilter.class
                )
                .exceptionHandling(exception -> exception
                        .authenticationEntryPoint((request, response, authException) -> {
                            // API 요청에 대해서는 401 상태 코드만 반환 (리다이렉트 없음)
                            response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
                            response.setContentType("application/json");
                            response.getWriter().write("{\"error\":\"Unauthorized\",\"message\":\"인증이 필요합니다.\"}");
                        })
                )
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        // 로그인 페이지 URL
                        .loginPage("/api/auth/login")
                        // 사용자 정보를 가져오는 엔드포인트 설정
                        .userInfoEndpoint(userInfo -> userInfo
                                .userService(customOAuth2UserService))
                        // 인증 성공 시 처리할 핸들러
                        .successHandler(oAuth2LoginSuccessHandler)
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
                        // 커스텀 로그아웃 핸들러 추가
                        .addLogoutHandler(customLogoutHandler)
                        .logoutSuccessHandler((request, response, authentication) -> {
                            response.setContentType("application/json");
                            response.getWriter().write("{\"message\":\"로그아웃에 성공했습니다.\"}");
                            response.setStatus(HttpServletResponse.SC_OK);
                        })
                        .invalidateHttpSession(true) // JWT기반이므로 세션은 큰 의미 없을 수 있으나, 명시적으로 무효화
                        .clearAuthentication(true)); // SecurityContext에서 Authentication 객체 제거

        return http.build();
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource() {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedOrigins(List.of("http://localhost:3000","https://mcpanda.co.kr"));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        configuration.setAllowCredentials(true);
        configuration.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
