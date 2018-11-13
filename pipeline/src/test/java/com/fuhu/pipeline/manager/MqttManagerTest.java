package com.fuhu.pipeline.manager;

import android.content.Context;

import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.mqtt.Subscription;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.stubbing.Answer;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.HashMap;
import java.util.Map;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doAnswer;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by allanshih on 2017/2/9.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({MqttManager.class, PipeLog.class})
public class MqttManagerTest {
    @Mock
    private Context context;
    @Mock
    private MqttCallback mqttCallback;

    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class);
    }

    @Test
    public void runInit() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.init(context, "serverUri", "clientId", mqttCallback);
        MqttAndroidClient mqttAndroidClient = Whitebox.getInternalState(mqttManager, "mqttAndroidClient");

        //assert
        assertNotNull(mqttAndroidClient);
    }

    @Test
    public void runInitWithContextNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.init(null, "serverUri", "clientId", mqttCallback);
        MqttAndroidClient mqttAndroidClient = Whitebox.getInternalState(mqttManager, "mqttAndroidClient");

        //assert
        assertNull(mqttAndroidClient);
    }

    @Test
    public void runInitWithServerUriNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.init(context, null, "clientId", mqttCallback);
        MqttAndroidClient mqttAndroidClient = Whitebox.getInternalState(mqttManager, "mqttAndroidClient");

        //assert
        assertNull(mqttAndroidClient);
    }

    @Test
    public void runInitWithClientIdNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.init(context, "serverUri", null, mqttCallback);
        MqttAndroidClient mqttAndroidClient = Whitebox.getInternalState(mqttManager, "mqttAndroidClient");

        //assert
        assertNull(mqttAndroidClient);
    }

    @Test
    public void runInitWithCallbackNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.init(context, "serverUri", "clientId", null);
        MqttAndroidClient mqttAndroidClient = Whitebox.getInternalState(mqttManager, "mqttAndroidClient");

        //assert
        assertNotNull(mqttAndroidClient);
    }

    @Test
    public void runIsConnectedReturnFalse() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        Whitebox.setInternalState(mqttManager, "mqttAndroidClient", mqttAndroidClient);

        //act
        boolean isConnected = mqttManager.isConnected();

        //assert
        assertNotNull(mqttAndroidClient);
        assertFalse(isConnected);
        verify(mqttAndroidClient).isConnected();
    }

    @Test
    public void runIsConnectedReturnTrue() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        when(mqttAndroidClient.isConnected()).thenReturn(true);
        Whitebox.setInternalState(mqttManager, "mqttAndroidClient", mqttAndroidClient);

        //act
        boolean isConnected = mqttManager.isConnected();

        //assert
        assertNotNull(mqttAndroidClient);
        assertTrue(isConnected);
        verify(mqttAndroidClient).isConnected();
    }


    @Test
    public void runClearSubscriptionMap() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);

        //act
        mqttManager.clearSubscriptionMap();

        //assert
        verify(mSubscriptionMap).clear();
    }

    @Test
    public void runAddSubscriptionWithTopicAndQos() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.addSubscription("topic", 1);

        //assert
        verify(mqttManager).addSubscription(any(Subscription.class));
    }

    @Test
    public void runAddSubscriptionWithTopicNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.addSubscription(null, 1);

        //assert
        verify(mqttManager, never()).addSubscription(any(Subscription.class));
    }

    @Test
    public void runAddSubscription() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);
        Subscription subscription = new Subscription("topic", 2);

        //act
        mqttManager.addSubscription(subscription);

        //assert
        verify(mSubscriptionMap).put(isA(String.class), any(Subscription.class));
        assertEquals(mSubscriptionMap.size(), 1);
    }

    @Test
    public void runAddSubscriptionWithSubscriptionNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);

        //act
        mqttManager.addSubscription(null);

        //assert
        verify(mSubscriptionMap, never()).put(anyString(), any(Subscription.class));
        assertEquals(mSubscriptionMap.size(), 0);
    }

    @Test
    public void runRemoveSubscriptionWithTopicAndQos() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Subscription subscription = new Subscription("topic", 2);

        //act
        mqttManager.removeSubscription(subscription);

        //assert
        verify(mqttManager).removeSubscription(any(String.class));
    }

    @Test
    public void runRemoveSubscriptionWithSubscriptionNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());

        //act
        mqttManager.removeSubscription((Subscription)null);

        //assert
        verify(mqttManager, never()).removeSubscription(any(String.class));
    }


    @Test
    public void runRemoveSubscriptionWithTopic() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);
        Subscription subscription = new Subscription("topic", 2);

        //act
        mqttManager.addSubscription(subscription);
        assertEquals(mSubscriptionMap.size(), 1);
        mqttManager.removeSubscription("topic");
        assertEquals(mSubscriptionMap.size(), 0);

        //assert
        verify(mSubscriptionMap).put(isA(String.class), any(Subscription.class));
        verify(mSubscriptionMap).remove(isA(String.class));
    }

    @Test
    public void runRemoveSubscriptionWithTopicNull() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);
        Subscription subscription = new Subscription("topic", 2);

        //act
        mqttManager.addSubscription(subscription);
        assertEquals(mSubscriptionMap.size(), 1);
        mqttManager.removeSubscription((String)null);
        assertEquals(mSubscriptionMap.size(), 1);

        //assert
        verify(mSubscriptionMap).put(isA(String.class), any(Subscription.class));
        verify(mSubscriptionMap, never()).remove(isA(String.class));
    }

    @Test
    public void runShutdown() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);

        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        Whitebox.setInternalState(mqttManager, "mqttAndroidClient", mqttAndroidClient);

        //act
        mqttManager.shutdown();

        //assert
        verify(mSubscriptionMap).clear();
        verify(mqttAndroidClient).isConnected();
        verify(mqttAndroidClient).close();
    }


    @Test
    public void runShutdownWithDisconnectSuccess() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);

        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        Whitebox.setInternalState(mqttManager, "mqttAndroidClient", mqttAndroidClient);
        when(mqttAndroidClient.isConnected()).thenReturn(true);

        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                IMqttActionListener mqttActionListener =
                        (IMqttActionListener) invocation.getArguments()[1];
                mqttActionListener.onSuccess(mock(IMqttToken.class));
                return null;
            }
        }).when(mqttAndroidClient).disconnect(any(Object.class), any(IMqttActionListener.class));

        //act
        mqttManager.shutdown();

        //assert
        verify(mSubscriptionMap).clear();
        verify(mqttAndroidClient, times(1)).isConnected();
        verify(mqttAndroidClient, times(1)).disconnect(any(Object.class), any(IMqttActionListener.class));
        verify(mqttAndroidClient).close();
    }

    @Test
    public void runShutdownWithDisconnectFailure() throws Exception {
        //arrange
        MqttManager mqttManager = PowerMockito.spy(MqttManager.getInstance());
        Map<String, Subscription> mSubscriptionMap = PowerMockito.spy(new HashMap<String, Subscription>());
        Whitebox.setInternalState(mqttManager, "mSubscriptionMap", mSubscriptionMap);

        MqttAndroidClient mqttAndroidClient = mock(MqttAndroidClient.class);
        Whitebox.setInternalState(mqttManager, "mqttAndroidClient", mqttAndroidClient);
        when(mqttAndroidClient.isConnected()).thenReturn(true);

        doAnswer(new Answer<Object>() {
            public Object answer(InvocationOnMock invocation) {
                IMqttActionListener mqttActionListener =
                        (IMqttActionListener) invocation.getArguments()[1];
                mqttActionListener.onFailure(mock(IMqttToken.class), mock(Throwable.class));
                return null;
            }
        }).when(mqttAndroidClient).disconnect(any(Object.class), any(IMqttActionListener.class));

        //act
        mqttManager.shutdown();

        //assert
        verify(mSubscriptionMap).clear();
        verify(mqttAndroidClient, times(1)).isConnected();
        verify(mqttAndroidClient, times(1)).disconnect(any(Object.class), any(IMqttActionListener.class));
        verify(mqttAndroidClient).close();
    }

    @Test
    public void runRelease() throws Exception {
        //arrange
        MqttManager INSTANCE = mock(MqttManager.class);
        Whitebox.setInternalState(MqttManager.class, "INSTANCE", INSTANCE);

        //act
        MqttManager.release();

        //assert
        verify(INSTANCE).shutdown();
    }
}
