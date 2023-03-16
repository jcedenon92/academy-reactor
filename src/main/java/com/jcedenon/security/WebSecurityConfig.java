package com.jcedenon.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpStatus;
import org.springframework.security.authorization.AuthorizationDecision;
import org.springframework.security.config.annotation.method.configuration.EnableReactiveMethodSecurity;
import org.springframework.security.config.annotation.web.reactive.EnableWebFluxSecurity;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.web.server.SecurityWebFilterChain;
import reactor.core.publisher.Mono;

// Security class 7
@Configuration
@EnableWebFluxSecurity
@EnableReactiveMethodSecurity
@RequiredArgsConstructor
public class WebSecurityConfig {

    private final AuthenticationManager authenticationManager;

    private final SecurityContextRepository securityContextRepository;

    @Bean
    public BCryptPasswordEncoder passwordEncoder(){
        return new BCryptPasswordEncoder();
    }

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http) {
        return http
                .exceptionHandling()
                .authenticationEntryPoint((swe, e) -> Mono.fromRunnable(() -> {
                        swe.getResponse().setStatusCode(HttpStatus.UNAUTHORIZED);
                    })).accessDeniedHandler((swe, e) -> Mono.fromRunnable(() -> {
                        swe.getResponse().setStatusCode(HttpStatus.FORBIDDEN);
                    }))
                .and()
                .csrf().disable()
                .formLogin().disable()
                .httpBasic().disable()
                .authenticationManager(authenticationManager)
                .securityContextRepository(securityContextRepository)
                .authorizeExchange()
                //SWAGGER PARA SPRING SECURITY
                .pathMatchers("/swagger-resources/**").permitAll()
                .pathMatchers("/swagger-ui.html").permitAll()
                .pathMatchers("/webjars/**").permitAll()
                //SWAGGER PARA SPRING SECURITY
                .pathMatchers("/login").permitAll()
                .pathMatchers("/api/login").permitAll()
                .pathMatchers("/api/fun/**").authenticated()
//                .pathMatchers("api/fun/**").hasAnyAuthority("ADMIN")
//                .pathMatchers("/api/fun/**")
//                .access((mono, contesxt) -> mono
//                        .map(auth -> auth.getAuthorities()
//                                .stream()
//                                .filter(e -> e.getAuthority().equals("ADMIN"))
//                                .count() > 0)
//                        .map(AuthorizationDecision::new)
//                )
                .pathMatchers("/students/**").authenticated()
                .pathMatchers("/courses/**").authenticated()
                .pathMatchers("/enrollments/**").authenticated()
                .pathMatchers("/backpressure/**").permitAll()
                .pathMatchers("/users/**").permitAll()
                .pathMatchers("/menus/**").permitAll()
                .anyExchange().authenticated()
                .and().build();
    }

}
