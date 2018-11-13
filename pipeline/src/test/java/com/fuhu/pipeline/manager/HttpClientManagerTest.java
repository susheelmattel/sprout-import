package com.fuhu.pipeline.manager;

import android.content.Context;
import android.test.mock.MockContext;

import com.fuhu.pipeline.internal.PipeLog;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PowerMockIgnore;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.io.File;

import okhttp3.Cache;
import okhttp3.ConnectionPool;
import okhttp3.Dispatcher;
import okhttp3.OkHttpClient;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by allanshih on 2017/2/8.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({HttpClientManager.class, PipeLog.class, OkHttpClient.class,
        OkHttpClient.Builder.class, Dispatcher.class, ConnectionPool.class, Cache.class})
@PowerMockIgnore("javax.net.ssl.*")
public class HttpClientManagerTest {
    @Mock
    private Context context;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class);
    }

    @Test
    public void runCreateOkHttpClient() throws Exception {
        //arrange
        HttpClientManager httpClientManager = HttpClientManager.getInstance();
        MockContext mockContext = Mockito.mock(MockContext.class);
        when(context.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getCacheDir()).thenReturn(new File("/cached"));

        //act
        OkHttpClient okHttpClient = httpClientManager.createOkHttpClient(context);

        //assert
        assertNotNull(okHttpClient);
    }

    @Test
    public void runCreateOkHttpClientNoContext() throws Exception {
        //arrange
        HttpClientManager httpClientManager = HttpClientManager.getInstance();

        //act
        OkHttpClient okHttpClient = httpClientManager.createOkHttpClient(null);

        //assert
        assertNotNull(okHttpClient);
    }

    @Test
    public void runCreateOkHttpsClient() throws Exception {
        //arrange
        HttpClientManager httpClientManager = HttpClientManager.getInstance();
        MockContext mockContext = Mockito.mock(MockContext.class);
        when(context.getApplicationContext()).thenReturn(mockContext);
        when(mockContext.getCacheDir()).thenReturn(new File("/cached"));

        //act
        OkHttpClient okHttpsClient = httpClientManager.createOkHttpsClient(context, null, null);

        //assert
        assertNotNull(okHttpsClient);
    }

    @Test
    public void runCreateOkHttpsClientNoContext() throws Exception {
        //arrange;;
        HttpClientManager httpClientManager = HttpClientManager.getInstance();

        //act
        OkHttpClient okHttpsClient = httpClientManager.createOkHttpsClient(null, null, null);

        //assert
        assertNotNull(okHttpsClient);
    }

    @Test
    public void runCloseOkHttpsClient() throws Exception {
        //arrange;;
        PowerMockito.mockStatic(Dispatcher.class, ConnectionPool.class, Cache.class);
        HttpClientManager httpClientManager = HttpClientManager.getInstance();
        OkHttpClient okHttpsClient = PowerMockito.mock(OkHttpClient.class);

        Dispatcher dispatcher = PowerMockito.mock(Dispatcher.class);
        when(okHttpsClient.dispatcher()).thenReturn(dispatcher);

        ConnectionPool connectionPool = PowerMockito.mock(ConnectionPool.class);
        when(okHttpsClient.connectionPool()).thenReturn(connectionPool);

        Cache cache = PowerMockito.mock(Cache.class);
        when(okHttpsClient.cache()).thenReturn(cache);

        //act
        httpClientManager.close(okHttpsClient);

        //assert
        verify(dispatcher).cancelAll();
        verify(connectionPool).evictAll();
        verify(cache).close();
    }

    @Test
    public void runCloseAllOkHttpsClient() throws Exception {
        //arrange
        HttpClientManager httpClientManager = PowerMockito.spy(HttpClientManager.getInstance());

        //act
        httpClientManager.closeAllClient();

        //assert
        verify(httpClientManager, times(2)).close(any(OkHttpClient.class));
    }

    @Test
    public void runRelease() throws Exception {
        //arrange
        PowerMockito.mockStatic(HttpClientManager.class);

        //act
        HttpClientManager.release();

        //assert
        PowerMockito.verifyStatic();
        HttpClientManager.release();
    }
}
