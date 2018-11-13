package com.fuhu.pipeline.task;

import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.internal.PipeStatus;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by allanshih on 2017/2/13.
 */


@RunWith(PowerMockRunner.class)
@PrepareForTest({ConnectMqttTask.class, PipeLog.class})
public class ConnectMqttTaskTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        mockStatic(PipeLog.class);
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        MqttConnectOptions options = spy(MqttConnectOptions.class);
        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setMqttAndroidClient(mqttAndroidClient);
        mqttItem.setMqttConnectOptions(options);
        ConnectMqttTask connectMqttTask = spy(ConnectMqttTask.class);

        when(mqttAndroidClient.isConnected()).thenReturn(true);

        //act
        connectMqttTask.process(mqttItem);

        //assert
        verify(mqttItem).setPipeStatus(any(Integer.class));
        assertEquals(mqttItem.getPipeStatus(), PipeStatus.SUCCESS);
    }

    @Test
    public void runProcessWithClientNull() throws Exception {
        //arrange
        MqttConnectOptions options = spy(MqttConnectOptions.class);
        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setMqttConnectOptions(options);
        ConnectMqttTask connectMqttTask = spy(ConnectMqttTask.class);

        //act
        connectMqttTask.process(mqttItem);

        //assert
        verify(mqttItem).setPipeStatus(any(Integer.class));
        assertEquals(mqttItem.getPipeStatus(), PipeStatus.MQTT_CLIENT_NULL);
    }

    @Test
    public void runProcessWithConnecting() throws Exception {
        //arrange
        MqttConnectOptions options = spy(MqttConnectOptions.class);
        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setMqttAndroidClient(mqttAndroidClient);
        mqttItem.setMqttConnectOptions(options);
        ConnectMqttTask connectMqttTask = spy(ConnectMqttTask.class);

        when(mqttAndroidClient.isConnected()).thenReturn(false);
        doAnswer(new Answer<IMqttToken>() {
            public IMqttToken answer(InvocationOnMock invocation) {
                return mock(IMqttToken.class);
            }
        }).when(mqttAndroidClient).connect(any(MqttConnectOptions.class));

        //act
        connectMqttTask.process(mqttItem);

        //assert
        verify(mqttAndroidClient).isConnected();
        verify(mqttAndroidClient).connect(any(MqttConnectOptions.class));
        verify(mqttItem).setMqttToken(any(IMqttToken.class));
    }

    @Test
    public void runProcessWithConnectingNoOptions() throws Exception {
        //arrange
        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        MqttItem mqttItem = spy(MqttItem.class);
        mqttItem.setMqttAndroidClient(mqttAndroidClient);
        mqttItem.setMqttConnectOptions(null);
        ConnectMqttTask connectMqttTask = spy(ConnectMqttTask.class);

        when(mqttAndroidClient.isConnected()).thenReturn(false);
        doAnswer(new Answer<IMqttToken>() {
            public IMqttToken answer(InvocationOnMock invocation) {
                return mock(IMqttToken.class);
            }
        }).when(mqttAndroidClient).connect();

        //act
        connectMqttTask.process(mqttItem);

        //assert
        verify(mqttAndroidClient).isConnected();
        verify(mqttAndroidClient).connect();
        verify(mqttItem).setMqttToken(any(IMqttToken.class));
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        ConnectMqttTask connectMqttTask = spy(ConnectMqttTask.class);

        //act
        boolean isDone = connectMqttTask.isDone();

        //assert
        assertTrue(isDone);
    }
}
