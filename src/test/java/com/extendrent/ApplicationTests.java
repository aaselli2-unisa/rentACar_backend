package com.extendrent;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.test.context.ActiveProfiles;
import src.core.config.SeedDataConfig;
import src.core.utilities.aspect.logger.service.ServiceLogger;

@SpringBootTest
@ActiveProfiles("test")
class ApplicationTests {

    // Prevent SeedDataConfig (Cloudinary, email, complex DB ops) from running at startup
    @MockBean private SeedDataConfig seedDataConfig;
    // Prevent ServiceLogger aspect from failing in test context
    @MockBean private ServiceLogger serviceLogger;

    @Test
    void contextLoads() {
    }

}
