package src.core.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    // Security patch V12: removed duplicate addCorsMappings override.
    // CORS is configured exclusively in CorsConfig and wired through
    // Spring Security via .cors(withDefaults()). Having two WebMvcConfigurer
    // beans registering "/**" creates undefined merge behaviour and risks
    // re-introducing attacker domains on future edits.

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/swagger-ui/**")
                .addResourceLocations("classpath:/META-INF/resources/webjars/springfox-swagger-ui/");
    }
}
