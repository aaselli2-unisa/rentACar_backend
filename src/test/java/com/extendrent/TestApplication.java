package com.extendrent;

import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Minimal Spring Boot configuration for tests.
 * Required because the main Application class lives in package "src",
 * which is not a parent of "com.extendrent" — Spring cannot discover it automatically.
 */
@SpringBootApplication(scanBasePackages = "src")
public class TestApplication {
}
