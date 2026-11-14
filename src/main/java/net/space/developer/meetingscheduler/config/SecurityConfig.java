package net.space.developer.meetingscheduler.config;

import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import tools.jackson.databind.json.JsonMapper;

import java.util.List;

/**
 * @author Lazaro Noel Guerra Medina
 * @since 2026-11-14
 * 
 * @brief Stateless HTTP Basic security: reads are public, every write needs an authenticated user.
 */
@Configuration
@EnableConfigurationProperties(SecurityProperties.class)
public class SecurityConfig {

    private static final String[] PUBLIC_PATHS = {
            "/actuator/health/**", "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html", "/error"
    };

    /**
     * Creates a {@link SecurityFilterChain} bean that configures the security for the application.
     * 
     * @param http the {@link HttpSecurity} object to configure
     * @param entryPoint the {@link AuthenticationEntryPoint} to handle authentication failures
     * @return a {@link SecurityFilterChain} bean
     * @throws Exception if an error occurs while configuring the security
     */
    @Bean
    SecurityFilterChain securityFilterChain(HttpSecurity http, AuthenticationEntryPoint entryPoint) {
        return http
                .csrf(AbstractHttpConfigurer::disable)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                .authorizeHttpRequests(auth -> auth
                        .requestMatchers(PUBLIC_PATHS).permitAll()
                        .requestMatchers(HttpMethod.GET, "/api/**").permitAll()
                        .anyRequest().authenticated())
                .httpBasic(basic -> basic.authenticationEntryPoint(entryPoint))
                .exceptionHandling(errors -> errors.authenticationEntryPoint(entryPoint))
                .build();
    }

    /**
     * Creates a {@link ProblemDetailAuthenticationEntryPoint} bean that handles authentication failures by returning a ProblemDetail response.
     * 
     * @param jsonMapper the JSON mapper used to serialize the ProblemDetail body
     * @return a {@link AuthenticationEntryPoint} bean
     */
    @Bean
    AuthenticationEntryPoint problemDetailEntryPoint(JsonMapper jsonMapper) {
        return new ProblemDetailAuthenticationEntryPoint(jsonMapper);
    }

    /**
     * Creates a {@link PasswordEncoder} bean that uses the BCrypt hashing algorithm to encode passwords.
     * 
     * @return a {@link PasswordEncoder} bean
     */
    @Bean
    PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * Creates a {@link UserDetailsService} bean that provides user details for authentication.
     * 
     * @param properties the security properties containing user credentials
     * @param encoder the password encoder used to encode user passwords
     * @return a {@link UserDetailsService} bean
     */
    @Bean
    UserDetailsService userDetailsService(SecurityProperties properties, PasswordEncoder encoder) {
        List<UserDetails> users = properties.users().stream()
                .map(user -> User.withUsername(user.username())
                        .password(encoder.encode(user.password()))
                        .roles("USER")
                        .build())
                .toList();
        return new InMemoryUserDetailsManager(users);
    }
}
