package br.com.pattern.resiliencia.retry;

import br.com.pattern.resiliencia.Proxy;
import br.com.pattern.resiliencia.BackendService;

import org.junit.Before;
import org.junit.Test;

import java.util.concurrent.TimeoutException;

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
    public void shouldRetry3times() {
        when(this.proxy.doRequest()).thenThrow(new RuntimeException("ops... we have a problem. =( "));

        callService();

        verify(proxy, times(3)).doRequest();
    }


    @Test
    public void shouldReturnSuccess() {
        when(this.proxy.doRequest()).thenReturn("it's working :)");

        callService();

        verify(proxy, times(1)).doRequest();
    }

    @Test
    public void shouldReturnThrowDiffExceptionsOfConfig() throws TimeoutException {

        when(this.proxy.doRequest()).thenThrow(new UnsupportedClassVersionError());


        try {
            backendService.consulta();
        } catch (UnsupportedClassVersionError ex) {}


        verify(proxy, times(1)).doRequest();
    }

    private void callService() {
        try { backendService.consulta(); }catch (Exception ex) {}
    }

}
