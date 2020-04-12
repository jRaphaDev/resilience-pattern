package br.com.pattern.resiliencia.retry;

import io.github.resilience4j.retry.Retry;
import io.github.resilience4j.retry.RetryConfig;
import io.github.resilience4j.retry.RetryRegistry;

import java.time.Duration;

public class RetryConfiguration {


    public static Retry init(String retryName){

        RetryConfig retryConfig = RetryConfig.custom()
                .maxAttempts(3)
                .waitDuration(Duration.ofMillis(100))
                .retryExceptions(Exception.class, RuntimeException.class)
                .build();

        RetryRegistry retryRegistry = RetryRegistry.of(retryConfig);

        Retry retry = retryRegistry.retry(retryName);

        return retry;
    }
}
