package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.manager.HttpClientManager;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import okhttp3.MediaType;
import okhttp3.RequestBody;

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
@PrepareForTest({BuildOkHttpRequestBodyTask.class, PipeLog.class, HttpClientManager.class, RequestBody.class})
public class BuildOkHttpRequestBodyTaskTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        mockStatic(PipeLog.class);
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        mockStatic(RequestBody.class);
        JSONObject jsonObject = mock(JSONObject.class);
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setRequestJson(jsonObject);
        BuildOkHttpRequestBodyTask buildOkHttpClientTask = spy(BuildOkHttpRequestBodyTask.class);
        RequestBody requestBody = spy(RequestBody.class);

        when(RequestBody.create(any(MediaType.class), any(String.class))).thenReturn(requestBody);

        //act
        buildOkHttpClientTask.process(httpItem);

        //assert
        verifyStatic();
        RequestBody.create(any(MediaType.class), any(String.class));
        verify(httpItem).setOkHttpRequestBody(any(RequestBody.class));
    }

    @Test
    public void runProcessWithHttpItemNull() throws Exception {
        //arrange
        mockStatic(RequestBody.class);
        BuildOkHttpRequestBodyTask buildOkHttpClientTask = spy(BuildOkHttpRequestBodyTask.class);

        //act
        buildOkHttpClientTask.process(null);

        //assert
        verifyStatic(never());
        RequestBody.create(any(MediaType.class), any(String.class));
    }

    @Test
    public void runProcessWithJSONObjectNull() throws Exception {
        //arrange
        mockStatic(RequestBody.class);
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setRequestJson(null);
        BuildOkHttpRequestBodyTask buildOkHttpClientTask = spy(BuildOkHttpRequestBodyTask.class);

        //act
        buildOkHttpClientTask.process(httpItem);

        //assert
        verifyStatic(never());
        RequestBody.create(any(MediaType.class), any(String.class));
    }

    @Test
    public void runProcessWithMqttItem() throws Exception {
        //arrange
        mockStatic(RequestBody.class);
        BuildOkHttpRequestBodyTask buildOkHttpClientTask = spy(BuildOkHttpRequestBodyTask.class);

        //act
        buildOkHttpClientTask.process(mock(MqttItem.class));

        //assert
        verifyStatic(never());
        RequestBody.create(any(MediaType.class), any(String.class));
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        BuildOkHttpRequestBodyTask buildOkHttpClientTask = spy(BuildOkHttpRequestBodyTask.class);

        //act
        boolean isDone = buildOkHttpClientTask.isDone();

        //assert
        assertTrue(isDone);
    }
}