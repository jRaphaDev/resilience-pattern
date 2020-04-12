package br.com.pattern.resiliencia.circuitbreaker;

import br.com.pattern.resiliencia.BackendService;
import br.com.pattern.resiliencia.Proxy;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;

import io.vavr.collection.Stream;
import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;
import static org.mockito.Mockito.*;

public class ApplicationTest {

    private BackendService backendService;
    private Proxy proxy;

    @Before
    public void setup() {
        proxy = mock(Proxy.class);

        backendService = new BackendServiceImpl(proxy);
    }

    @Test
    public void should_OpenChannel_When_HalfPercentCallsThrowException() {
        mockException();
        Stream.rangeClosed(1, 20).forEach((count) -> doRequest());

        assertEquals(getState(), CircuitBreaker.State.OPEN);

        verify(proxy, times(5)).doRequest();
    }

    @Test
    public void should_KeefChannelClose_And_ExceAllCalls() {
        mockSuccess();
        Stream.rangeClosed(1, 20).forEach((count) -> doRequest());

        assertEquals(getState(), CircuitBreaker.State.CLOSED);

        verify(proxy, times(20)).doRequest();
    }

    @Test
    public void should_BackToChannelHalfOpen() throws InterruptedException {
        mockException();
        Stream.rangeClosed(1, 20).forEach((count) -> doRequest());

        assertEquals(getState(), CircuitBreaker.State.OPEN);
        verify(proxy, times(5)).doRequest();

        Thread.sleep(1000);
        assertEquals(getState(), CircuitBreaker.State.HALF_OPEN);
    }

    private CircuitBreaker.State getState() {
        CircuitBreaker circuitBreaker = CircuitBreakerConfiguration
                .getCircuitBreaker("backend-service");

        return circuitBreaker.getState();
    }

    private void doRequest() {
        try {
            backendService.consulta();
        } catch (Exception ignore) {}
    }

    private void mockSuccess(){
        when(this.proxy.doRequest()).thenReturn("success");
    }

    private void mockException(){
        when(this.proxy.doRequest()).thenThrow(new RuntimeException("ops... sorry, we have a problem. =[ "));
    }


}
