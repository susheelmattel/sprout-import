package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.internal.PipeLog;
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
import static org.mockito.Mockito.spy;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.mockito.PowerMockito.when;

/**
 * Created by allanshih on 2017/2/13.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConvertObjectToJsonTask.class, PipeLog.class, JsonParser.class})
public class ConvertObjectToJsonTaskTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        mockStatic(PipeLog.class, JsonParser.class);
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setRequestJson(new JSONObject());
        httpItem.setRequestObject(httpItem);

        ConvertObjectToJsonTask convertObjectToJsonTask = spy(ConvertObjectToJsonTask.class);
//        when(JsonParser.fromJSON(any(JSONObject.class), any(Class.class))).thenReturn(httpItem);
//        when(httpItem.isSuccessful()).thenReturn(true);

        //act
        convertObjectToJsonTask.process(httpItem);

        //assert
        verify(httpItem).getRequestObject();
        verify(httpItem).getRequestJson();
        verify(httpItem, times(1)).setRequestJson(any(JSONObject.class));
    }

    @Test
    public void runProcessWithRequestJsonNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setRequestJson(null);
        httpItem.setRequestObject(httpItem);

        ConvertObjectToJsonTask convertObjectToJsonTask = spy(ConvertObjectToJsonTask.class);
        when(JsonParser.toJSON(any(Object.class))).thenReturn(new JSONObject());

        //act
        convertObjectToJsonTask.process(httpItem);

        //assert
        verify(httpItem).getRequestObject();
        verify(httpItem).getRequestJson();
        verifyStatic();
        JsonParser.toJSON(any(Object.class));
        verify(httpItem, times(2)).setRequestJson(any(JSONObject.class));
    }

    @Test
    public void runProcessWithRequestJsonNullAndRequestObjectNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setRequestJson(null);
        httpItem.setRequestObject(null);

        ConvertObjectToJsonTask convertObjectToJsonTask = spy(ConvertObjectToJsonTask.class);

        //act
        convertObjectToJsonTask.process(httpItem);

        //assert
        verify(httpItem).getRequestObject();
        verify(httpItem).getRequestJson();
        verify(httpItem, times(1)).setRequestJson(any(JSONObject.class));
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        ConvertObjectToJsonTask convertObjectToJsonTask = spy(ConvertObjectToJsonTask.class);

        //act
        boolean isDone = convertObjectToJsonTask.isDone();

        //assert
        assertTrue(isDone);
    }
}
