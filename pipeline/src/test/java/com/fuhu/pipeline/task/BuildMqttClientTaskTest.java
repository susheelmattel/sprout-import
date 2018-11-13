package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.manager.MqttManager;
import com.fuhu.pipeline.mqtt.MqttItem;

import org.eclipse.paho.android.service.MqttAndroidClient;
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
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;

/**
 * Created by allanshih on 2017/2/10.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({BuildMqttClientTask.class, PipeLog.class, MqttManager.class})
public class BuildMqttClientTaskTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        mockStatic(PipeLog.class);
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        mockStatic(MqttManager.class);
        MqttManager mqttManager = mock(MqttManager.class);
        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        MqttItem mqttItem = spy(MqttItem.class);
        BuildMqttClientTask buildMqttClientTask = spy(BuildMqttClientTask.class);

        when(MqttManager.getInstance()).thenReturn(mqttManager);
        when(mqttManager.getMqttClient()).thenReturn(mqttAndroidClient);

        //act
        buildMqttClientTask.process(mqttItem);

        //assert
        verify(mqttItem).setMqttAndroidClient(any(MqttAndroidClient.class));
    }

    @Test
    public void runProcessWithMqttItemNull() throws Exception {
        //arrange
        mockStatic(MqttManager.class);
        BuildMqttClientTask buildMqttClientTask = spy(BuildMqttClientTask.class);

        //act
        buildMqttClientTask.process(null);

        //assert
        verifyStatic(never());
        MqttManager.getInstance();
    }

    @Test
    public void runProcessWithHttptem() throws Exception {
        //arrange
        mockStatic(MqttManager.class);
        BuildMqttClientTask buildMqttClientTask = spy(BuildMqttClientTask.class);

        //act
        buildMqttClientTask.process(mock(HttpItem.class));

        //assert
        verifyStatic(never());
        MqttManager.getInstance();
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        BuildMqttClientTask buildMqttClientTask = spy(BuildMqttClientTask.class);

        //act
        boolean isDone = buildMqttClientTask.isDone();

        //assert
        assertTrue(isDone);
    }
}
