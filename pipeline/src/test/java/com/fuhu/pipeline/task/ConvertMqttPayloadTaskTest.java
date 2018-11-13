package com.fuhu.pipeline.task;

import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.fuhu.pipeline.parser.JsonParser;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by allanshih on 2017/2/13.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConvertMqttPayloadTask.class, PipeLog.class, JsonParser.class})
public class ConvertMqttPayloadTaskTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        mockStatic(PipeLog.class, JsonParser.class);
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        ConvertMqttPayloadTask convertMqttPayloadTask = spy(ConvertMqttPayloadTask.class);

        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setRequestJson(new JSONObject());
        mqttItem.setRequestObject(mqttItem);
        mqttItem.setPayloadString("payload");
        mqttItem.setPayload("payload".getBytes());

        //act
        convertMqttPayloadTask.process(mqttItem);

        //assert
        verify(mqttItem).getRequestJson();
        verify(mqttItem).getRequestObject();
        verify(mqttItem).getPayloadString();
        verify(mqttItem).getPayload();
    }

    @Test
    public void runProcessWithPayloadNull() throws Exception {
        //arrange
        ConvertMqttPayloadTask convertMqttPayloadTask = spy(ConvertMqttPayloadTask.class);

        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setRequestJson(new JSONObject());
        mqttItem.setRequestObject(mqttItem);
        mqttItem.setPayloadString("payload");

        //act
        convertMqttPayloadTask.process(mqttItem);

        //assert
        verify(mqttItem).getRequestJson();
        verify(mqttItem).getRequestObject();
        verify(mqttItem).getPayloadString();
        verify(mqttItem).getPayload();
        verify(mqttItem).setPayload(any(byte[].class));
    }

    @Test
    public void runProcessWithRequestJson() throws Exception {
        //arrange
        ConvertMqttPayloadTask convertMqttPayloadTask = spy(ConvertMqttPayloadTask.class);

        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setRequestJson(new JSONObject());

        //act
        convertMqttPayloadTask.process(mqttItem);

        //assert
        verify(mqttItem).getRequestJson();
        verify(mqttItem).getRequestObject();
        verify(mqttItem).getPayloadString();
        verify(mqttItem).getPayload();
        verify(mqttItem).setPayload(any(byte[].class));
    }

    @Test
    public void runProcessWithRequestObject() throws Exception {
        //arrange
        ConvertMqttPayloadTask convertMqttPayloadTask = spy(ConvertMqttPayloadTask.class);

        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setRequestObject(mqttItem);

        when(JsonParser.toJSON(any(Object.class))).thenReturn(new JSONObject());

        //act
        convertMqttPayloadTask.process(mqttItem);

        //assert
        verify(mqttItem).getRequestJson();
        verify(mqttItem).getRequestObject();
        verify(mqttItem).getPayloadString();
        verify(mqttItem).getPayload();
        verify(mqttItem).setPayload(any(byte[].class));
    }

    @Test
    public void runProcessWithRequestObjectAndParseFailed() throws Exception {
        //arrange
        ConvertMqttPayloadTask convertMqttPayloadTask = spy(ConvertMqttPayloadTask.class);

        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setRequestObject(mqttItem);

        when(JsonParser.toJSON(any(Object.class))).thenReturn(null);

        //act
        convertMqttPayloadTask.process(mqttItem);

        //assert
        verify(mqttItem).getRequestJson();
        verify(mqttItem).getRequestObject();
        verify(mqttItem).getPayloadString();
        verify(mqttItem).getPayload();
        verify(mqttItem, never()).setPayload(any(byte[].class));
    }

    @Test
    public void runProcessWithPayloadString() throws Exception {
        //arrange
        ConvertMqttPayloadTask convertMqttPayloadTask = spy(ConvertMqttPayloadTask.class);

        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setPayloadString("payload");

        //act
        convertMqttPayloadTask.process(mqttItem);

        //assert
        verify(mqttItem).getRequestJson();
        verify(mqttItem).getRequestObject();
        verify(mqttItem).getPayloadString();
        verify(mqttItem).getPayload();
        verify(mqttItem).setPayload(any(byte[].class));
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        ConvertMqttPayloadTask convertMqttPayloadTask = spy(ConvertMqttPayloadTask.class);

        //act
        boolean isDone = convertMqttPayloadTask.isDone();

        //assert
        assertTrue(isDone);
    }
}
