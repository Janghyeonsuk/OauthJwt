package com.example.oauthjwt.config;

import com.example.oauthjwt.auth.handler.JwtAccessDeniedHandler;
import com.example.oauthjwt.auth.handler.JwtAuthenticationEntryPoint;
import com.example.oauthjwt.auth.handler.OAuth2FailureHandler;
import com.example.oauthjwt.auth.handler.OAuth2SuccessHandler;
import com.example.oauthjwt.auth.jwt.TokenAuthenticationFilter;
import com.example.oauthjwt.auth.jwt.TokenExceptionFilter;
import com.example.oauthjwt.auth.service.CustomOAuth2UserService;
import com.example.oauthjwt.domain.user.entity.Role;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.annotation.web.configurers.HeadersConfigurer.FrameOptionsConfig;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

@RequiredArgsConstructor
@Configuration
@EnableWebSecurity
@EnableMethodSecurity
public class SecurityConfig {

    private final CustomOAuth2UserService oAuth2UserService;
    private final OAuth2SuccessHandler oAuth2SuccessHandler;
    private final TokenAuthenticationFilter tokenAuthenticationFilter;

    @Bean
    public WebSecurityCustomizer webSecurityCustomizer() {
        return web -> web.ignoring()
                .requestMatchers("/error", "/favicon.ico");
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        System.out.println("Configuring security filter chain");

        http
                .csrf(AbstractHttpConfigurer::disable)
                .cors(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .headers(c -> c.frameOptions(
                        FrameOptionsConfig::disable).disable())
                .sessionManagement(c ->
                        c.sessionCreationPolicy(SessionCreationPolicy.STATELESS))

                // request 인증, 인가 설정
                .authorizeHttpRequests(request ->
                        request.requestMatchers(
                                        new AntPathRequestMatcher("/"),
                                        new AntPathRequestMatcher("/api/auth/success/**"),
                                        new AntPathRequestMatcher("/login"),
                                        new AntPathRequestMatcher("/swagger-ui/**"),
                                        new AntPathRequestMatcher("/api-docs/**")
                                ).permitAll()
                                .requestMatchers(
                                        new AntPathRequestMatcher("/api/user/**")
                                ).hasAnyRole(Role.USER.name(), Role.ADMIN.name())
                                .requestMatchers(
                                        new AntPathRequestMatcher("/api/admin/**")
                                ).hasRole(Role.ADMIN.name())
                                .anyRequest().authenticated()
                )

                // oauth2 설정
                .oauth2Login(oauth ->
                        // OAuth2 로그인 성공 후 사용자 정보를 가져올 때의 설정
                        oauth.userInfoEndpoint(c -> c.userService(oAuth2UserService))
                                // 로그인 성공 시 핸들러
                                .successHandler(oAuth2SuccessHandler)
                                .failureHandler(new OAuth2FailureHandler())
                )

                // jwt 인증 관련 설정
                .addFilterBefore(tokenAuthenticationFilter,
                        UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(new TokenExceptionFilter(), tokenAuthenticationFilter.getClass())

                // 인증 예외 핸들링 401, 403 오류 발생
                .exceptionHandling((exceptions) -> exceptions
                        .authenticationEntryPoint(new JwtAuthenticationEntryPoint())
                        .accessDeniedHandler(new JwtAccessDeniedHandler()));
        return http.build();
    }

}
