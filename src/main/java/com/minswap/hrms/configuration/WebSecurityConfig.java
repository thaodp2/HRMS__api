package com.minswap.hrms.configuration;

import com.minswap.hrms.service.oauth.OAuth2UserServiceImpl;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.util.CollectionUtils;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Collection;

@AllArgsConstructor
@Configuration
@EnableWebSecurity
public class WebSecurityConfig extends WebSecurityConfigurerAdapter {
    private final OAuth2UserServiceImpl oAuth2UserService;

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
                .csrf().disable();
        http.authorizeRequests().antMatchers("/**").permitAll().anyRequest().anonymous();
//        http
//            .authorizeRequests()
//            .antMatchers("*").permitAll()
//            .antMatchers("/").permitAll()
//            .antMatchers("/login").permitAll()
//            .antMatchers("/oauth2/**").permitAll()
//            .anyRequest().authenticated()
//            .and()
//            .oauth2Login()
//            .loginPage("/login")
//            .userInfoEndpoint()
//            .userService(oAuth2UserService)
//            .and()
//            .successHandler((request, response, authentication) -> {
//                try {
//                    OAuth2User user = (OAuth2User) authentication.getPrincipal();
//                    Collection<? extends GrantedAuthority> authorities = user.getAuthorities();
//                    if (CollectionUtils.isEmpty(authorities)) {
//                        authentication.setAuthenticated(false);
//                        response.sendRedirect("/login");
//                    }
//                    response.sendRedirect("/");
//                } catch (Exception e) {
//                    e.printStackTrace();
//                }
//            });
    }
}
