package com.techbookstore.app.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;

/**
 * Security configuration.
 *
 * <p>WARNING: {@code anyRequest().permitAll()} together with disabled CSRF protection means every
 * endpoint (including the H2 console and Actuator) is reachable without authentication. This is an
 * intentional workshop baseline for the legacy application and must not be used in a real
 * deployment. The H2 console itself is only enabled under the {@code dev} profile
 * (see {@code application-dev.yml}).
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig extends WebSecurityConfigurerAdapter {

    @Override
    protected void configure(HttpSecurity http) throws Exception {
        http
            .authorizeRequests()
                .anyRequest().permitAll()
                .and()
            .csrf().disable()
            .headers().frameOptions().sameOrigin(); // For H2 Console
    }
}