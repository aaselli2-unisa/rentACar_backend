package src.core.config;

import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configurers.AbstractHttpConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.HttpStatusEntryPoint;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import src.core.security.JwtAuthFilter;
import src.core.security.RateLimitFilter;

import static org.springframework.http.HttpStatus.FORBIDDEN;
import static org.springframework.http.HttpStatus.UNAUTHORIZED;
import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@AllArgsConstructor
public class SecurityConfig {

    // Security patch V13: Swagger and API-docs paths removed from the public whitelist.
    // They are placed under authenticated() below so any logged-in user can use them.
    // In production, consider restricting further to hasRole("ADMIN") or disabling entirely
    // via @Profile("!prod") on OpenApiConfig/SwaggerConfig.
    // The Azure URL entry (/extendrent.azurewebsites.net/...) has also been removed — its
    // purpose was undocumented and it created an unusual literal path match.
    private static final String[] DEFAULT_WHITE_LIST_URLS = {
            "/api/auth/**"
    };


    private final JwtAuthFilter jwtAuthFilter;
    private final RateLimitFilter rateLimitFilter;
    private final PasswordEncoder passwordEncoder;
    private final UserDetailsService userService;

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                .cors(withDefaults())
                .csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((req) -> req
                        .requestMatchers(DEFAULT_WHITE_LIST_URLS).permitAll()

                        // Security patch V13: Swagger / OpenAPI paths require authentication.
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**").authenticated()

                        // Public authentication/verification endpoints
                        // Security patch V01: isUserTrue converted to POST (password moved to body)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/signup", "/api/v1/auth/signin", "/api/v1/auth/isUserTrue").permitAll()
                        .requestMatchers("/api/v1/verify/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/refresh-token/**").permitAll()

                        // Security-sensitive areas protected by role
                        .requestMatchers("/api/v1/admins/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/employees/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/rentals/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/discounts/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/paymentDetails/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/paymentTypes/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/customers/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/images/**").hasRole("ADMIN")

                        // Domain endpoints kept authenticated by default
                        .requestMatchers("/api/v1/brands/**").authenticated()
                        .requestMatchers("/api/v1/colors/**").authenticated()
                        .requestMatchers("/api/v1/carBodyTypes/**").authenticated()
                        .requestMatchers("/api/v1/carModels/**").authenticated()
                        .requestMatchers("/api/v1/cars/**").authenticated()
                        .requestMatchers("/api/v1/fuels/**").authenticated()
                        .requestMatchers("/api/v1/gearshifts/**").authenticated()
                        .requestMatchers("/api/v1/vehicle-statuses/**").authenticated()
                        .requestMatchers("/api/v1/drivingLicenseType/**").authenticated()
                        .requestMatchers("/api/v1/rentalStatuses/**").authenticated()
                        .requestMatchers("/api/v1/car-segments/**").authenticated()

                        .anyRequest().authenticated()
                )
                .exceptionHandling(ex -> ex
                        .authenticationEntryPoint(new HttpStatusEntryPoint(UNAUTHORIZED))
                        .accessDeniedHandler((request, response, accessDeniedException) ->
                                response.sendError(FORBIDDEN.value(), FORBIDDEN.getReasonPhrase()))
                )
                .authenticationProvider(authenticationProvider())
                // Security patch V06: rate limiting runs before JWT auth on all auth endpoints
                .addFilterBefore(rateLimitFilter, UsernamePasswordAuthenticationFilter.class)
                .addFilterBefore(jwtAuthFilter, UsernamePasswordAuthenticationFilter.class)
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // Security patch V14: add Content-Security-Policy header (OWASP A05 / CWE-693).
                // Restricts resource loading to same origin; frame-ancestors 'none' prevents clickjacking.
                .headers(headers -> headers
                        .contentSecurityPolicy(csp -> csp.policyDirectives(
                                "default-src 'self'; frame-ancestors 'none'"))
                );
        return http.build();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        DaoAuthenticationProvider authenticationProvider = new DaoAuthenticationProvider();
        authenticationProvider.setPasswordEncoder(passwordEncoder);
        authenticationProvider.setUserDetailsService(userService);
        return authenticationProvider;
    }

    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }
}
