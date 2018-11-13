package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.HttpMethod;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

import okhttp3.Request;
import okhttp3.RequestBody;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by allanshih on 2017/2/13.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildOkHttpRequestTask.class, PipeLog.class, Request.class, Request.Builder.class})
public class BuildOkHttpRequestTaskTest {
    @Mock
    private Request.Builder builder;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        mockStatic(PipeLog.class);
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        Map<String, String> headers = new HashMap<>();
        headers.put("token", "test");

        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setMethod(HttpMethod.GET);
        httpItem.setHttpHeaders(headers);

        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());
        PowerMockito.doReturn(builder).when(buildOkHttpRequestTask, "get", any(HttpItem.class));
        when(builder.build()).thenReturn(mock(Request.class));

        //act
        buildOkHttpRequestTask.process(httpItem);

        //assert
        verify(httpItem).getMethod();
        verify(httpItem).getHttpHeaders();
        verify(builder).addHeader(any(String.class), any(String.class));
        verify(builder).build();
        verify(httpItem).setOkHttpRequest(any(Request.class));
    }

    @Test
    public void runProcessWithHttpItemNull() throws Exception {
        //arrange
        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());
        PowerMockito.doReturn(builder).when(buildOkHttpRequestTask, "get", any(HttpItem.class));
        when(builder.build()).thenReturn(mock(Request.class));

        //act
        buildOkHttpRequestTask.process(null);

        //assert
        verify(builder, never()).build();
    }

    @Test
    public void runProcessWithMqttItem() throws Exception {
        //arrange
        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());
        PowerMockito.doReturn(builder).when(buildOkHttpRequestTask, "get", any(HttpItem.class));
        when(builder.build()).thenReturn(mock(Request.class));

        //act
        buildOkHttpRequestTask.process(mock(MqttItem.class));

        //assert
        verify(builder, never()).build();
    }

    @Test
    public void runProcessWithHeaderHull() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setMethod(HttpMethod.GET);
        httpItem.setHttpHeaders(null);

        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());
        PowerMockito.doReturn(builder).when(buildOkHttpRequestTask, "get", any(HttpItem.class));
        when(builder.build()).thenReturn(mock(Request.class));

        //act
        buildOkHttpRequestTask.process(httpItem);

        //assert
        verify(httpItem).getMethod();
        verify(httpItem).getHttpHeaders();
        verify(builder, never()).addHeader(any(String.class), any(String.class));
        verify(builder).build();
        verify(httpItem).setOkHttpRequest(any(Request.class));
    }

    @Test
    public void runGet() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        httpItem.setUrl("http://test");

        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "get", httpItem);

        //assert
        assertNotNull(builder);
        verify(httpItem).getUrl();
    }

    @Test
    public void runGetWithUrlNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "get", httpItem);

        //assert
        assertNull(builder);
        verify(httpItem).getUrl();
    }

    @Test
    public void runPost() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        httpItem.setUrl("http://test");
        httpItem.setOkHttpRequestBody(mock(RequestBody.class));

        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "post", httpItem);

        //assert
        assertNotNull(builder);
        verify(httpItem).getUrl();
        verify(httpItem).getOkHttpRequestBody();
    }

    @Test
    public void runPostWithUrlNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "post", httpItem);

        //assert
        assertNull(builder);
        verify(httpItem).getUrl();
        verify(httpItem, never()).getOkHttpRequestBody();
    }

    @Test
    public void runPut() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        httpItem.setUrl("http://test");
        httpItem.setOkHttpRequestBody(mock(RequestBody.class));

        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "put", httpItem);

        //assert
        assertNotNull(builder);
        verify(httpItem).getUrl();
        verify(httpItem).getOkHttpRequestBody();
    }

    @Test
    public void runPutWithUrlNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "put", httpItem);

        //assert
        assertNull(builder);
        verify(httpItem).getUrl();
        verify(httpItem, never()).getOkHttpRequestBody();
    }

    @Test
    public void runDelete() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        httpItem.setUrl("http://test");

        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "delete", httpItem);

        //assert
        assertNotNull(builder);
        verify(httpItem).getUrl();
    }

    @Test
    public void runDeleteWithUrlNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(new HttpItem());
        BuildOkHttpRequestTask buildOkHttpRequestTask = PowerMockito.spy(new BuildOkHttpRequestTask());

        //act
        Request.Builder builder = Whitebox.invokeMethod(buildOkHttpRequestTask, "delete", httpItem);

        //assert
        assertNull(builder);
        verify(httpItem).getUrl();
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        BuildOkHttpRequestTask buildOkHttpClientTask = spy(BuildOkHttpRequestTask.class);

        //act
        boolean isDone = buildOkHttpClientTask.isDone();

        //assert
        assertTrue(isDone);
    }
}