package SSAFY_B108.MCPanda.global.config;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractAuthenticationFilterConfigurer;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 관련 설정
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        // Swagger UI 접근 허용
                        .requestMatchers(
                                "/swagger-ui/**",
                                "/swagger-ui.html",
                                "/api-docs/**",
                                "/v3/api-docs/**",
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
                .formLogin(AbstractAuthenticationFilterConfigurer::permitAll)
                // OAuth2 로그인 설정
                .oauth2Login(oauth2 -> oauth2
                        .loginPage("/api/auth/login")
                        .defaultSuccessUrl("/api/auth/success", true)
                        .failureUrl("/api/auth/failure")
                        // OAuth2 설정
                        .authorizationEndpoint(endpoint -> endpoint
                                .baseUri("/api/auth/oauth2/authorization"))
                        // OAuth2 콜백 설정
                        .redirectionEndpoint(endpoint -> endpoint
                                .baseUri("/api/auth/login/oauth2/code/*"))
                );

        return http.build();
    }
}