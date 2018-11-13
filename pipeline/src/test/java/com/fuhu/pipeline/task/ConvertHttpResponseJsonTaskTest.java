package com.fuhu.pipeline.task;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.parser.JsonParser;

import junit.framework.Assert;

import org.json.JSONArray;
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
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;

/**
 * Created by allanshih on 2017/2/13.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({ConvertHttpResponseJsonTask.class, PipeLog.class, JsonParser.class})
public class ConvertHttpResponseJsonTaskTest {
    private class TestEventItem extends APipeItem {
        String id, event_type, child_id, start_date, end_data, data, created_at, updated_at;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }

    private String testJson = "[{\"id\":\"104c179e-f965-437c-a83f-0bafcf68ee32\"," +
            "\"event_type\":\"learningPeriod\"," +
            "\"child_id\":\"c4a89fa8-ad63-4927-b377-f1e7e08ad250\"," +
            "\"start_date\":\"2017-03-09T08:39:15Z\"," +
            "\"end_date\":\"2017-03-22T10:00:48.433791Z\"," +
            "\"data\":null," +
            "\"created_at\":\"2017-03-09T08:39:19.820129Z\"," +
            "\"updated_at\":\"2017-03-22T10:00:48.434476Z\"}] ";

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
        httpItem.setResponseJson(new JSONObject());
        httpItem.setDataModel(HttpItem.class);

        ConvertHttpResponseJsonTask convertHttpResponseJsonTask = spy(ConvertHttpResponseJsonTask.class);
        when(JsonParser.fromJSON(any(JSONObject.class), any(Class.class))).thenReturn(httpItem);
        when(httpItem.isSuccessful()).thenReturn(true);

        //act
        convertHttpResponseJsonTask.process(httpItem);

        //assert
        verify(httpItem).setResponseObject(any());
    }

    @Test
    public void runProcessWithJsonArray() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setResponseJsonArray(new JSONArray(testJson));
        Assert.assertNotNull(httpItem.getResponseJsonArray());
        httpItem.setDataModel(TestEventItem.class);

        ConvertHttpResponseJsonTask convertHttpResponseJsonTask = spy(ConvertHttpResponseJsonTask.class);
//        when(JsonParser.fromJSON(any(JSONObject.class), any(Class.class))).thenReturn(httpItem);
        when(httpItem.isSuccessful()).thenReturn(true);

        //act
        convertHttpResponseJsonTask.process(httpItem);

        //assert
        verify(httpItem).setResponseArray(any(TestEventItem[].class));
    }

    @Test
    public void runProcessIsNotSuccessful() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setResponseJson(new JSONObject());
        httpItem.setDataModel(HttpItem.class);

        ConvertHttpResponseJsonTask convertHttpResponseJsonTask = spy(ConvertHttpResponseJsonTask.class);
        when(httpItem.isSuccessful()).thenReturn(false);

        //act
        convertHttpResponseJsonTask.process(httpItem);

        //assert
        verify(httpItem, never()).setResponseObject(any());
    }

    @Test
    public void runProcessWithResponseJsonNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setResponseJson(null);
        httpItem.setDataModel(HttpItem.class);

        ConvertHttpResponseJsonTask convertHttpResponseJsonTask = spy(ConvertHttpResponseJsonTask.class);
        when(httpItem.isSuccessful()).thenReturn(true);

        //act
        convertHttpResponseJsonTask.process(httpItem);

        //assert
        verify(httpItem, never()).setResponseObject(any());
    }

    @Test
    public void runProcessWithDataModelNull() throws Exception {
        //arrange
        HttpItem httpItem = spy(HttpItem.class);
        httpItem.setResponseJson(new JSONObject());
        httpItem.setDataModel(null);

        ConvertHttpResponseJsonTask convertHttpResponseJsonTask = spy(ConvertHttpResponseJsonTask.class);
        when(httpItem.isSuccessful()).thenReturn(true);

        //act
        convertHttpResponseJsonTask.process(httpItem);

        //assert
        verify(httpItem, never()).setResponseObject(any());
    }

    @Test
    public void runIsDone() throws Exception {
        //arrange
        ConvertHttpResponseJsonTask convertHttpResponseJsonTask = spy(ConvertHttpResponseJsonTask.class);

        //act
        boolean isDone = convertHttpResponseJsonTask.isDone();

        //assert
        assertTrue(isDone);
    }
}
