package com.extendrent.security;

import org.junit.jupiter.api.Assumptions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static org.assertj.core.api.Assertions.assertThat;

/**
 * Security regression tests for V04 — Hardcoded credentials (OWASP A02 / CWE-798).
 *
 * The fix chosen for this project: application.properties is in .gitignore and never
 * committed, so credentials can be stored as direct values in the local file.
 * The security property that must hold is that the file is gitignored, not that it
 * uses .env-var placeholders.
 *
 * These tests verify:
 *  1. application.properties is listed in .gitignore (never committed)
 *  2. The original project credentials (from git history, commits by Gökhan Asiltürk)
 *     are NOT the ones being used (they are publicly known from git log)
 *  3. The JWT secret key has adequate entropy
 */
@DisplayName("V04 – Hardcoded credentials in application.properties (OWASP A02 / CWE-798)")
class HardcodedCredentialsSecurityTest {

    private static final Path PROJECT_ROOT = Path.of(System.getProperty("user.dir"));

    private static final Path PROPS_PATH =
            PROJECT_ROOT.resolve("src/main/resources/application.properties");

    private static final Path GITIGNORE_PATH =
            PROJECT_ROOT.resolve(".gitignore");

    @Test
    @DisplayName("application.properties must be listed in .gitignore (never committed to git)")
    void applicationProperties_mustBeGitignored() throws IOException {
        String gitignore = Files.readString(GITIGNORE_PATH);
        assertThat(gitignore)
                .as("application.properties must be in .gitignore to prevent credentials from being committed")
                .contains("src/main/resources/application.properties");
    }

    @Test
    @DisplayName("application.properties must not contain original project DB password from git history (082df8b)")
    void applicationProperties_mustNotContainOriginalDbPassword() throws IOException {
        Assumptions.assumeTrue(Files.exists(PROPS_PATH), "application.properties not present (gitignored on CI) — skipping");
        String content = Files.readString(PROPS_PATH);
        assertThat(content)
                .as("DB password '123asd123' is publicly known from git history (commit 082df8b) — do not reuse it")
                .doesNotContain("123asd123");
        assertThat(content)
                .as("DB password '14531453' is publicly known from git history (commit 082df8b) — do not reuse it")
                .doesNotContain("14531453");
    }

    @Test
    @DisplayName("application.properties must not contain original Cloudinary secret from git history (082df8b)")
    void applicationProperties_mustNotContainOriginalCloudinarySecret() throws IOException {
        Assumptions.assumeTrue(Files.exists(PROPS_PATH), "application.properties not present (gitignored on CI) — skipping");
        String content = Files.readString(PROPS_PATH);
        assertThat(content)
                .as("Cloudinary API secret 'Hm05tc_JHUTTJJSoD5eyQNU_zTA' is publicly known from git history — do not reuse it")
                .doesNotContain("Hm05tc_JHUTTJJSoD5eyQNU_zTA");
    }

    @Test
    @DisplayName("JWT secret key must be at least 64 hex characters (32 bytes minimum)")
    void jwtSecretKey_mustHaveAdequateEntropy() throws IOException {
        Assumptions.assumeTrue(Files.exists(PROPS_PATH), "application.properties not present (gitignored on CI) — skipping");
        String content = Files.readString(PROPS_PATH);
        Pattern hexPattern = Pattern.compile("jwt\\.secret-key=([0-9A-Fa-f]+)");
        Pattern placeholderPattern = Pattern.compile("jwt\\.secret-key=\\$\\{");
        Matcher hexMatcher = hexPattern.matcher(content);
        if (hexMatcher.find()) {
            String hexKey = hexMatcher.group(1);
            assertThat(hexKey.length())
                    .as("JWT secret key must be at least 64 hex chars (32 bytes). Current: %d chars", hexKey.length())
                    .isGreaterThanOrEqualTo(64);
        } else if (!placeholderPattern.matcher(content).find()) {
            // Key is neither valid hex nor an .env-var placeholder — reject it
            org.assertj.core.api.Assertions.fail(
                    "JWT secret key must be either a hex string (≥64 chars) or an .env-var placeholder "
                    + "(${...}). Current value does not match either format.");
        }
        // If value is a placeholder ${...}, the check is skipped (value resolved at runtime).
    }

    @Test
    @DisplayName("application.properties.example must exist and be committed (setup guide for new developers)")
    void applicationPropertiesExample_mustExist() {
        assertThat(PROJECT_ROOT.resolve("src/main/resources/application.properties.example").toFile())
                .as("application.properties.example must exist so new developers know how to configure the app")
                .exists();
    }
}
