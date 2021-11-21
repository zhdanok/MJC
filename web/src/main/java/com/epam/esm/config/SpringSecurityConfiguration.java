package com.epam.esm.config;

import com.epam.esm.security.CustomAuthenticationSuccessHandler;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityCustomizer;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
public class SpringSecurityConfiguration {

    private final CustomAuthenticationSuccessHandler customAuthenticationSuccessHandler;

    @Bean
    WebSecurityCustomizer webSecurityCustomizer() {
        return (web) -> web.ignoring().antMatchers("/webjars/**");
    }

    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeRequests()
                .antMatchers("/tags/**").hasAnyAuthority("SCOPE_USER", "SCOPE_ADMIN")
                .antMatchers("/users").hasAuthority("SCOPE_ADMIN")
                .antMatchers("/users/**").hasAnyAuthority("SCOPE_USER", "SCOPE_ADMIN")
                .antMatchers("/gifts/**").permitAll()
                .antMatchers("/**").permitAll()
                .anyRequest().permitAll()
                .and()
                .oauth2Login()
                .successHandler(customAuthenticationSuccessHandler);
        return http.build();
    }
}
