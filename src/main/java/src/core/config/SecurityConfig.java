package src.core.config;

import lombok.AllArgsConstructor;
import org.springframework.boot.actuate.autoconfigure.security.servlet.EndpointRequest;
import org.springframework.boot.actuate.health.HealthEndpoint;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
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
@EnableMethodSecurity
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
                //.csrf(AbstractHttpConfigurer::disable)
                .formLogin(AbstractHttpConfigurer::disable)
                .httpBasic(AbstractHttpConfigurer::disable)
                .logout(AbstractHttpConfigurer::disable)
                .authorizeHttpRequests((req) -> req
                        // EndpointRequest handles actuator paths correctly in Spring Boot 3.x
                        // (MvcRequestMatcher doesn't match actuator endpoints registered outside MVC)
                        .requestMatchers(EndpointRequest.to(HealthEndpoint.class)).permitAll()
                        .requestMatchers(DEFAULT_WHITE_LIST_URLS).permitAll()

                        // V-13: Swagger restricted to ADMIN role — exposes full API map, not for end-users
                        .requestMatchers("/swagger-ui/**", "/swagger-ui.html",
                                "/v2/api-docs", "/v3/api-docs", "/v3/api-docs/**").hasRole("ADMIN")

                        // Public authentication/verification endpoints
                        // Security patch V01: isUserTrue converted to POST (password moved to body)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/signup", "/api/v1/auth/signin", "/api/v1/auth/isUserTrue").permitAll()
                        .requestMatchers("/api/v1/verify/**").permitAll()
                        .requestMatchers(HttpMethod.POST, "/api/v1/refresh-token/**").permitAll()
                        // V-04: logout needs to be authenticated (revokes tokens for the calling user)
                        .requestMatchers(HttpMethod.POST, "/api/v1/auth/logout").authenticated()

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

                        // Security patch V16: catalogue/lookup domains split by method.
                        // GET stays authenticated() (any logged-in user can browse).
                        // POST/PUT/DELETE restricted to ADMIN — a CUSTOMER must not be able
                        // to create, modify, or delete cars, brands, colors or any catalogue
                        // entity (CWE-284 / OWASP A01 Broken Access Control).
                        // Spring Security evaluates rules top-to-bottom: a GET request matches
                        // the first rule (authenticated) and stops; POST/PUT/DELETE fall through
                        // to the second rule (hasRole ADMIN).
                        .requestMatchers(HttpMethod.GET,
                                "/api/v1/cars/**", "/api/v1/brands/**", "/api/v1/colors/**",
                                "/api/v1/fuels/**", "/api/v1/gearshifts/**",
                                "/api/v1/vehicle-statuses/**", "/api/v1/carBodyTypes/**",
                                "/api/v1/carModels/**", "/api/v1/car-segments/**").authenticated()
                        .requestMatchers(
                                "/api/v1/cars/**", "/api/v1/brands/**", "/api/v1/colors/**",
                                "/api/v1/fuels/**", "/api/v1/gearshifts/**",
                                "/api/v1/vehicle-statuses/**", "/api/v1/carBodyTypes/**",
                                "/api/v1/carModels/**", "/api/v1/car-segments/**").hasRole("ADMIN")
                        // GET drivingLicenseType is needed by the public signup form (dropdown).
                        // Write ops restricted to ADMIN (V16).
                        .requestMatchers(HttpMethod.GET, "/api/v1/drivingLicenseType/**").permitAll()
                        .requestMatchers("/api/v1/drivingLicenseType/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/rentalStatuses/**").authenticated()

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
