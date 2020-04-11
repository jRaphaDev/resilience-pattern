package br.com.pattern.resiliencia.circuitbreaker;

import br.com.pattern.resiliencia.Proxy;


import io.github.resilience4j.circuitbreaker.CircuitBreaker;

public class BackendServiceImpl implements BackendService {

    private static final String CIRCUIT_BREAKER_NAME = "backend-service";

    private CircuitBreaker circuitBreaker = CircuitBreakerConfiguration.init(CIRCUIT_BREAKER_NAME);
    private Proxy proxy;

    public BackendServiceImpl(Proxy proxy){
        this.proxy = proxy;
    }

    @Override
    public String consulta() {

        String response = circuitBreaker.executeSupplier(() -> proxy.doRequest());

        return response;
    }
}
