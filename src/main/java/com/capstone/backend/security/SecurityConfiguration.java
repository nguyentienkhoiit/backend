package com.capstone.backend.security;

import com.capstone.backend.security.jwt.JwtAuthenticationEntryPoint;
import com.capstone.backend.security.jwt.JwtAuthenticationFilter;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import static com.capstone.backend.utils.Constants.LIST_PERMIT_ALL;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class SecurityConfiguration {
    AuthenticationProvider authenticationProvider;
    JwtAuthenticationFilter jwtAuthenticationFilter;
    JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests(
                        request -> request.requestMatchers(LIST_PERMIT_ALL)
                                .permitAll()
                                .anyRequest()
                                .authenticated()
                )
                .exceptionHandling(ex -> ex.authenticationEntryPoint(jwtAuthenticationEntryPoint))
                .oauth2Login(Customizer.withDefaults())
                .sessionManagement(
                        session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                )
                .authenticationProvider(authenticationProvider)
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);
        return http.build();
    }

    // @Bean
    // public WebMvcConfigurer corsConfigurer() {
    //     return new WebMvcConfigurer() {
    //         @Override
    //         public void addCorsMappings(CorsRegistry registry) {
    //             registry.addMapping("/**").allowedOrigins("*");
    //         }
    //     };
    // }
}

