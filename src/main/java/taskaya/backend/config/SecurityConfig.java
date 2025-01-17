package taskaya.backend.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .csrf(csrf -> csrf.disable()) // Use the new lambda style for disabling CSRF
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers("/api/signup", "/api/signup/verify").permitAll() // Public endpoints
                        .anyRequest().authenticated() // Secure all other endpoints
                );

        return http.build();
    }
}