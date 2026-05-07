package src.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {
    // V12: CORS configured exclusively in CorsConfig (no addCorsMappings override here).
    // V-Snyk68/69: springfox resource handler removed — springdoc auto-registers Swagger UI.
}
