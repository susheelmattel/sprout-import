package com.fuhu.pipeline.task;

import android.content.Context;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.manager.HttpClientManager;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import okhttp3.OkHttpClient;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Created by allanshih on 2017/2/13.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildOkHttpClientTask.class, PipeLog.class, HttpClientManager.class})
public class BuildOkHttpClientTaskTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        mockStatic(PipeLog.class);
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        mockStatic(HttpClientManager.class);
        HttpClientManager httpClientManager = mock(HttpClientManager.class);
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        Context context = mock(Context.class);

        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setContext(context);
        BuildOkHttpClientTask buildOkHttpClientTask = spy(BuildOkHttpClientTask.class);

        when(HttpClientManager.getInstance()).thenReturn(httpClientManager);
        when(httpClientManager.createOkHttpClient(any(Context.class))).thenReturn(okHttpClient);

        //act
        buildOkHttpClientTask.process(httpItem);

        //assert
        verify(httpItem).setOkHttpClient(any(OkHttpClient.class));
    }

    @Test
    public void runProcessWithHttpItemNull() throws Exception {
        //arrange
        mockStatic(HttpClientManager.class);
        HttpClientManager httpClientManager = mock(HttpClientManager.class);
        OkHttpClient okHttpClient = mock(OkHttpClient.class);
        BuildOkHttpClientTask buildOkHttpClientTask = spy(BuildOkHttpClientTask.class);

        //act
        buildOkHttpClientTask.process(null);

        //assert
        verifyStatic(never());
        HttpClientManager.getInstance();
    }

    @Test
    public void runProcessWithContextNull() throws Exception {
        //arrange
        mockStatic(HttpClientManager.class);
        HttpItem httpItem = spy(HttpItem.class);
        BuildOkHttpClientTask buildOkHttpClientTask = spy(BuildOkHttpClientTask.class);

        //act
        buildOkHttpClientTask.process(httpItem);

        //assert
        verifyStatic(never());
        HttpClientManager.getInstance();
    }

    @Test
    public void runProcessWithMqtttem() throws Exception {
        //arrange
        mockStatic(HttpClientManager.class);
        BuildOkHttpClientTask buildOkHttpClientTask = spy(BuildOkHttpClientTask.class);

        //act
        buildOkHttpClientTask.process(mock(MqttItem.class));

        //assert
        verifyStatic(never());
        HttpClientManager.getInstance();
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        BuildOkHttpClientTask buildOkHttpClientTask = spy(BuildOkHttpClientTask.class);

        //act
        boolean isDone = buildOkHttpClientTask.isDone();

        //assert
        assertTrue(isDone);
    }
}
