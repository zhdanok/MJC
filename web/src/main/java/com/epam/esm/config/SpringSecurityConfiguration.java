package com.epam.esm.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.annotation.web.configurers.oauth2.server.resource.OAuth2ResourceServerConfigurer;

@Configuration
@EnableWebSecurity
public class SpringSecurityConfiguration extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http.authorizeRequests(authorizeRequests -> authorizeRequests
                        .antMatchers("/tags/**").hasAnyAuthority("SCOPE_USER", "SCOPE_ADMIN")
                        .antMatchers("/users").hasAuthority("SCOPE_ADMIN")
                        .antMatchers("/users/**").hasAnyAuthority("SCOPE_USER", "SCOPE_ADMIN")
                        .antMatchers("/gifts/**").permitAll()
                        .antMatchers("/**").permitAll()
                        .anyRequest().permitAll())
                .oauth2ResourceServer(OAuth2ResourceServerConfigurer::jwt);
    }
}
