package src.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig implements WebMvcConfigurer {
    // Security patch V03: removed attacker-controlled domains from the allowed origins list.
    // Only real frontend origins are listed. Use .env-var or Spring profile injection for
    // dev/staging/prod separation in a real deployment.
    private static final String[] ALLOWED_ORIGINS = {
            "http://localhost:3000",
            "http://localhost:5173",
            "https://legit-frontend.example.com"
    };

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/**")
                .allowedOrigins(ALLOWED_ORIGINS)
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                .allowedHeaders("Content-Type", "Authorization")
                // V-02: required for HttpOnly cookie delivery — only safe because ALLOWED_ORIGINS
                // is an explicit whitelist (allowCredentials is incompatible with wildcard origins)
                .allowCredentials(true)
                .maxAge(3600);
    }
}
