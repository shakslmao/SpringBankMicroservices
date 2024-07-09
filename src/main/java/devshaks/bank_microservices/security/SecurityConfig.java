package devshaks.bank_microservices.security;

import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
@RequiredArgsConstructor
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfig {
    
    // Inject the custom JWT filter for authentication
    private final JwtFilter jwtAuthFilter;
    
    // Inject the custom authentication provider
    private final AuthenticationProvider authenticationProvider;

    /**
     * Configures the security filter chain.
     *
     * @param httpSecurity the HttpSecurity to modify
     * @return the configured SecurityFilterChain
     * @throws Exception if an error occurs during configuration
     */
    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity.cors(withDefaults()) // Enable CORS with default settings
            .csrf(AbstractHttpConfigurer::disable) // Disable CSRF protection
            .authorizeHttpRequests(request -> request.requestMatchers(
                "/auth/**", "/v2/api-docs",
                "/v3/api-docs", "/swagger-resources", "/swagger-resources/**",
                "/configuration/ui", "/configuration/security", "/swagger-ui/**",
                "/webjars/**", "/swagger-ui.html")
                .permitAll() // Allow unauthenticated access to these endpoints
                .anyRequest()
                .authenticated()) // Require authentication for all other requests
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)) // Set session management to stateless
            .authenticationProvider(authenticationProvider) // Set the custom authentication provider
            .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class); // Add the JWT filter before the UsernamePasswordAuthenticationFilter
        return httpSecurity.build(); // Build and return the SecurityFilterChain
    }
}

