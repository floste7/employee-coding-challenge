package io.github.floste7.employee.analytics;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.StreamsBuilderFactoryBeanConfigurer;
import org.springframework.kafka.core.CleanupConfig;

/**
 * This configuration cleans up the embedded kafka's state store after each test.
 */
@Configuration
class TestConfig {

    @Bean
    StreamsBuilderFactoryBeanConfigurer config() {
        return fb -> fb.setCleanupConfig(new CleanupConfig(true, true));
    }

}
