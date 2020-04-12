package br.com.pattern.resiliencia.retry;

import br.com.pattern.resiliencia.BackendService;
import br.com.pattern.resiliencia.Proxy;

import io.github.resilience4j.retry.Retry;

import java.util.function.Supplier;

public class BackendServiceImpl implements BackendService {

    private static final String RETRY_NAME = "backend-service-retry";

    private Retry retry = RetryConfiguration.init(RETRY_NAME);
    private Proxy proxy;

    public BackendServiceImpl(Proxy proxy){
        this.proxy = proxy;
    }

    @Override
    public String consulta() {

        Supplier<String> response = Retry.decorateSupplier(retry, proxy::doRequest);

        return response.get();
    }
}
