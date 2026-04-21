package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import src.core.config.SecurityConfig;

import java.lang.reflect.Field;
import java.util.Arrays;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security regression test for V13 — Swagger UI unrestricted in production whitelist.
 *
 * OWASP A05 – Security Misconfiguration | CWE-16 – Configuration
 *
 * SecurityConfig includes /swagger-ui/**, /v2/api-docs, /v3/api-docs/** in the
 * DEFAULT_WHITE_LIST_URLS regardless of the active Spring profile. In production
 * this exposes the full API contract (all endpoints, schemas, auth flows) without
 * any authentication, enabling rapid enumeration and targeted exploitation.
 *
 * THIS TEST PASSES — it became green after V13 was patched. Swagger paths are now:
 *   (a) moved behind hasRole("ADMIN") / authenticated() in SecurityConfig, or
 *   (b) OpenApiConfig / SwaggerConfig annotated with @Profile("!prod") so the
 *       beans don't register in production and the paths return 404.
 */
@DisplayName("V13 – Swagger UI unrestricted in production (OWASP A05 / CWE-16)")
class SwaggerProdAccessSecurityTest {

    @Test
    @DisplayName("PATCHED V13: DEFAULT_WHITE_LIST_URLS must NOT contain Swagger UI paths")
    void defaultWhitelist_mustNotContainSwaggerPaths() throws Exception {
        // Access the private static field via reflection
        Field field = SecurityConfig.class.getDeclaredField("DEFAULT_WHITE_LIST_URLS");
        field.setAccessible(true);
        String[] whitelist = (String[]) field.get(null);

        List<String> swaggerPaths = List.of(
                "/swagger-ui/**",
                "/swagger-ui.html",
                "/v2/api-docs",
                "/v3/api-docs",
                "/v3/api-docs/**"
        );

        List<String> actualWhitelist = Arrays.asList(whitelist);

        for (String swaggerPath : swaggerPaths) {
            if (actualWhitelist.contains(swaggerPath)) {
                assertThat(actualWhitelist)
                        .as("Swagger path '%s' is in the public whitelist. "
                            + "In production, Swagger must require at least ADMIN authentication. "
                            + "Fix: move Swagger paths to hasRole(\"ADMIN\") or annotate config with @Profile(\"!prod\").",
                            swaggerPath)
                        .doesNotContain(swaggerPath);
            }
        }
    }

    @Test
    @DisplayName("PATCHED V13: DEFAULT_WHITE_LIST_URLS must NOT contain the mysterious Azure URL entry")
    void defaultWhitelist_mustNotContainAzureUrlEntry() throws Exception {
        Field field = SecurityConfig.class.getDeclaredField("DEFAULT_WHITE_LIST_URLS");
        field.setAccessible(true);
        String[] whitelist = (String[]) field.get(null);

        assertThat(Arrays.asList(whitelist))
                .as("The path '/extendrent.azurewebsites.net/api/v1/**' has no clear purpose. "
                    + "Its intent should be documented or it should be removed.")
                .doesNotContain("/extendrent.azurewebsites.net/api/v1/**");
    }
}
