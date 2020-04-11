package br.com.pattern.resiliencia.circuitbreaker;

import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerConfig;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;

import io.github.resilience4j.micrometer.tagged.TaggedCircuitBreakerMetrics;
import io.micrometer.core.instrument.MeterRegistry;
import io.micrometer.core.instrument.simple.SimpleMeterRegistry;

import java.time.Duration;

public class CircuitBreakerConfiguration {

    private static CircuitBreakerRegistry circuitBreakerRegistry;

    public static CircuitBreaker init(String circuitBreakerName){

        CircuitBreakerConfig circuitBreakerConfig = CircuitBreakerConfig.custom()
                .slidingWindowSize(10)
                .minimumNumberOfCalls(5)
                .permittedNumberOfCallsInHalfOpenState(3)
                .automaticTransitionFromOpenToHalfOpenEnabled(true)
                .waitDurationInOpenState(Duration.ofMillis(1000))
                .failureRateThreshold(50)
                .slowCallRateThreshold(100)
                .build();

        circuitBreakerRegistry = CircuitBreakerRegistry.of(circuitBreakerConfig);

        configMetrics(circuitBreakerRegistry);

        CircuitBreaker circuitBreaker = circuitBreakerRegistry.circuitBreaker(circuitBreakerName);

        return circuitBreaker;
    }



    public static CircuitBreaker getCircuitBreaker(String circuitBrakerName){
        return circuitBreakerRegistry.circuitBreaker(circuitBrakerName);
    }

    private static void configMetrics(CircuitBreakerRegistry circuitBreakerRegistry){

        MeterRegistry meterRegistry = new SimpleMeterRegistry();

        TaggedCircuitBreakerMetrics.ofCircuitBreakerRegistry(circuitBreakerRegistry)
                .bindTo(meterRegistry);
    }
}
