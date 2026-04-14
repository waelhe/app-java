package com.marketplace.shared.config;

import io.github.resilience4j.common.circuitbreaker.configuration.CircuitBreakerConfigCustomizer;
import io.github.resilience4j.common.ratelimiter.configuration.RateLimiterConfigCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.time.Duration;

@Configuration
public class ResilienceConfig {

    @Bean
    public CircuitBreakerConfigCustomizer defaultCircuitBreaker() {
        return CircuitBreakerConfigCustomizer.of("default", builder -> builder
            .failureRateThreshold(50)
            .slowCallRateThreshold(80)
            .waitDurationInOpenState(Duration.ofSeconds(30))
            .slowCallDurationThreshold(Duration.ofSeconds(5))
            .permittedNumberOfCallsInHalfOpenState(5)
            .minimumNumberOfCalls(10)
            .slidingWindowSize(20)
        );
    }

    @Bean
    public RateLimiterConfigCustomizer defaultRateLimiter() {
        return RateLimiterConfigCustomizer.of("default", builder -> builder
            .limitForPeriod(100)
            .limitRefreshPeriod(Duration.ofSeconds(1))
            .timeoutDuration(Duration.ofSeconds(5))
        );
    }
}
