package src.core.config;

import com.fasterxml.jackson.core.StreamReadConstraints;
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Snyk #72 — Allocation of Resources Without Limits (spring-core / CWE-770).
 * Jackson by default has no cap on nesting depth or string length, allowing a
 * deeply-nested JSON payload to exhaust stack/heap. These constraints reject
 * malformed input at the parser level before it reaches any service code.
 */
@Configuration
public class JacksonConfig {

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer jacksonStreamConstraints() {
        return builder -> builder.postConfigurer(mapper ->
                mapper.getFactory().setStreamReadConstraints(
                        StreamReadConstraints.builder()
                                .maxNestingDepth(100)
                                .maxStringLength(5_000_000)
                                .maxNumberLength(1_000)
                                .build()));
    }
}
