package com.extendrent.security;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import src.core.config.CorsConfig;
import src.core.config.WebConfig;

import java.lang.reflect.Method;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security regression test for V12 — Duplicate CORS configuration (two conflicting beans).
 *
 * OWASP A05 – Security Misconfiguration | CWE-16 – Configuration
 *
 * Both CorsConfig and WebConfig implement WebMvcConfigurer and both register CORS for "/**".
 * Spring MVC merges WebMvcConfigurer beans: any future change to one file silently diverges
 * from the other, potentially re-introducing removed attacker domains.
 *
 * THIS TEST PASSES — it became green after V12 was patched: addCorsMappings() was removed from
 * WebConfig (leaving CorsConfig as the single, authoritative CORS configuration source).
 *
 * Behavioral CORS tests (preflight, whitelisted vs. attacker origins, ACAO header correctness)
 * are covered by {@link CorsSecurityTest}, which runs a full MockMvc context with both
 * {@link CorsConfig} and {@link WebConfig} imported.
 */
@DisplayName("V12 – Duplicate CORS configuration beans (OWASP A05 / CWE-16)")
class CorsConfigDuplicationSecurityTest {

    @Test
    @DisplayName("PATCHED V12: WebConfig must NOT override addCorsMappings – CORS belongs only in CorsConfig")
    void webConfig_mustNotOverrideAddCorsMappings() throws Exception {
        // After fix: WebConfig removes addCorsMappings(), so the method is resolved
        // from the WebMvcConfigurer interface (default no-op).
        // Before fix: WebConfig.class declares the method → getDeclaringClass() = WebConfig.class
        Method method = WebConfig.class.getMethod("addCorsMappings", CorsRegistry.class);
        Class<?> declaringClass = method.getDeclaringClass();

        assertThat(declaringClass)
                .as("addCorsMappings should NOT be declared in WebConfig. "
                    + "Found in: %s — WebConfig is adding a conflicting CORS registration for /**. "
                    + "Remove the method from WebConfig and keep CORS only in CorsConfig.",
                    declaringClass.getSimpleName())
                .isEqualTo(WebMvcConfigurer.class);
    }

    @Test
    @DisplayName("PASSES: CorsConfig must continue to declare addCorsMappings (authoritative source)")
    void corsConfig_mustDeclareAddCorsMappings() throws Exception {
        Method method = CorsConfig.class.getMethod("addCorsMappings", CorsRegistry.class);
        assertThat(method.getDeclaringClass())
                .as("CorsConfig must be the single source of CORS configuration")
                .isEqualTo(CorsConfig.class);
    }
}
