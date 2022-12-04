package com.minswap.hrms.configuration;

import com.minswap.hrms.security.RestAuthenticationEntryPoint;
import com.minswap.hrms.security.TokenAuthenticationFilter;
import com.minswap.hrms.security.oauth2.HttpCookieOAuth2AuthorizationRequestRepository;
import com.minswap.hrms.security.oauth2.OAuth2AuthenticationFailureHandler;
import com.minswap.hrms.security.oauth2.OAuth2AuthenticationSuccessHandler;
import com.minswap.hrms.security.oauth2.OAuth2UserServiceImpl;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.cors.CorsConfiguration;

import java.util.List;

import static com.minswap.hrms.constants.CommonConstant.*;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(
        securedEnabled = true,
        jsr250Enabled = true,
        prePostEnabled = true
)
@Slf4j
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final OAuth2UserServiceImpl oAuth2UserService;
    private static final String LOGIN_PATH = "https://ms-hrms.software/login";
    private static final List<String> ALLOWED_METHOD = List.of(
            HttpMethod.GET.name(),
            HttpMethod.POST.name(),
            HttpMethod.DELETE.name(),
            HttpMethod.PUT.name(),
            HttpMethod.OPTIONS.name()
    );

    private static final String[] MANAGER_URL_PATTERNS = {MANAGER + "/**"};
    private static final String[] HR_URL_PATTERNS = {HR + "/**"};
    private static final String[] IT_SUPPORT_URL_PATTERNS = {ITSUPPORT + "/**"};
    private static final String[] WHITE_LIST_URL_PATTERNS = {"/oauth2/**"};
    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .cors().configurationSource(req -> corsConfiguration())
            .and().sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
            .and().csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
            .exceptionHandling()
                .authenticationEntryPoint(new RestAuthenticationEntryPoint())
            .and().authorizeRequests()
            .antMatchers(WHITE_LIST_URL_PATTERNS).permitAll()
            .antMatchers(HR_URL_PATTERNS).hasAnyAuthority("HR", "Admin")
            .antMatchers(MANAGER_URL_PATTERNS).hasAnyAuthority("Manager", "Admin")
            .antMatchers(IT_SUPPORT_URL_PATTERNS).hasAnyAuthority("IT Support", "Admin")
            .anyRequest().authenticated()
            .and()
            .oauth2Login()
                .loginPage(LOGIN_PATH)
                .authorizationEndpoint()
                .baseUri("/oauth2/authorization")
                .authorizationRequestRepository(cookieAuthorizationRequestRepository())
                .and()
                .redirectionEndpoint()
                .baseUri("/oauth2/callback/*")
                .and()
                .userInfoEndpoint()
                .userService(oAuth2UserService)
            .and().successHandler(oAuth2AuthenticationSuccessHandler)
            .failureHandler(oAuth2AuthenticationFailureHandler);
        http.addFilterBefore(tokenAuthenticationFilter(), UsernamePasswordAuthenticationFilter.class);
    }
    private final OAuth2AuthenticationSuccessHandler oAuth2AuthenticationSuccessHandler;

    private final OAuth2AuthenticationFailureHandler oAuth2AuthenticationFailureHandler;
    @Bean
    public CorsConfiguration corsConfiguration()
    {
        CorsConfiguration configuration = new CorsConfiguration();
        configuration.setAllowedMethods(ALLOWED_METHOD);
        configuration.applyPermitDefaultValues();
        return configuration;
    }

    @Bean
    public TokenAuthenticationFilter tokenAuthenticationFilter() {
        return new TokenAuthenticationFilter();
    }

    @Bean
    public HttpCookieOAuth2AuthorizationRequestRepository cookieAuthorizationRequestRepository() {
        return new HttpCookieOAuth2AuthorizationRequestRepository();
    }
}
