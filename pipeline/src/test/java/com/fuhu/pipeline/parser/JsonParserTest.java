package com.fuhu.pipeline.parser;

import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.mqtt.MqttItem;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.lang.reflect.Type;

import static com.fuhu.pipeline.parser.JsonParser.fromJSON;
import static com.fuhu.pipeline.parser.JsonParser.getGson;
import static com.fuhu.pipeline.parser.JsonParser.toJSON;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyString;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import static org.powermock.api.mockito.PowerMockito.mockStatic;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created by allanshih on 2017/2/10.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({JsonParser.class, PipeLog.class, Gson.class})
public class JsonParserTest {
    private class TestEventItem {
        String id, event_type, child_id, start_date, end_data, data, created_at, updated_at;
        public void setId(String id) {
            this.id = id;
        }
        public String getId() {
            return id;
        }
    }

    private String testString = "[{\"id\":\"104c179e-f965-437c-a83f-0bafcf68ee32\"," +
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
        mockStatic(PipeLog.class);
    }

    @Test
    public void runGetGson() throws Exception {
        //arrange

        //act
        Gson gson = spy(getGson());

        //assert
        assertNotNull(gson);
    }

    @Test
    public void runFromJSONWithJSONObject() throws Exception {
        //arrange
        Gson gson = mock(Gson.class);
        Whitebox.setInternalState(JsonParser.class, "mGson", gson);

        //act
        fromJSON(mock(JSONObject.class), MqttItem.class);

        //assert
        verify(gson).fromJson(anyString(), any(Class.class));
    }

    @Test
    public void runFromJSONWithJSONString() throws Exception {
        //arrange
        Gson gson = mock(Gson.class);
        Whitebox.setInternalState(JsonParser.class, "mGson", gson);

        //act
        fromJSON("test", MqttItem.class);

        //assert
        verify(gson).fromJson(anyString(), any(Class.class));
    }

    @Test
    public void runToJSONWithObject() throws Exception {
        //arrange
        Gson gson = mock(Gson.class);
        Whitebox.setInternalState(JsonParser.class, "mGson", gson);
        when(gson.toJson(any(TestEventItem.class))).thenReturn("{\"id\":123}");

        //act
        JSONObject jsonObject = toJSON(spy(new TestEventItem()));

        //assert
        verify(gson).toJson(any(TestEventItem.class));
        assertNotNull(jsonObject);
    }

    @Test
    public void runFromJSONWithJSONArray() throws Exception {
        //arrange
        Gson gson = spy(new Gson());
        JSONArray jsonArray = new JSONArray(testString);
        Whitebox.setInternalState(JsonParser.class, "mGson", gson);
        assertNotNull(jsonArray);

        //act
        TestEventItem [] testEventItems = JsonParser.fromJSON(jsonArray, TestEventItem[].class);

        //assert
        verify(gson).fromJson(anyString(), any(Type.class));
        assertNotNull(testEventItems);
        assertEquals(testEventItems.length, 1);
    }
}
