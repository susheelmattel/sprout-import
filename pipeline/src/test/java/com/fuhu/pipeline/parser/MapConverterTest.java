package com.fuhu.pipeline.parser;

import com.fuhu.pipeline.internal.PipeLog;

import org.json.JSONArray;
import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.powermock.api.mockito.PowerMockito.mock;

/**
 * Created by allanshih on 2017/2/10.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({MapConverter.class, PipeLog.class})
public class MapConverterTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class);
    }

    @Test
    public void runToMap() throws Exception {
        //arrange

        //act
        Map<String, Object> map = MapConverter.toMap(mock(JSONObject.class));

        //assert
        assertNotNull(map);
    }

    @Test
    public void runToMapWithJsonObject() throws Exception {
        //arrange
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("testInt", 123);
        jsonObject.put("testFloat", 456.1);


        //act
        Map<String, Object> map = MapConverter.toMap(jsonObject);

        //assert
        assertNotNull(map);
        assertEquals(map.size(), 2);
        assertEquals(map.get("testInt"), 123);
        assertEquals(map.get("testFloat"), 456.1);
    }

    @Test
    public void runToMapWithJsonObjectValue() throws Exception {
        //arrange
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("testJson", mock(JSONObject.class));

        //act
        Map<String, Object> map = MapConverter.toMap(jsonObject);

        //assert
        assertNotNull(map);
        assertEquals(map.size(), 1);
        assertThat(map.get("testJson"), instanceOf(Map.class));
    }

    @Test
    public void runToMapWithJsonArrayValue() throws Exception {
        //arrange
        JSONObject jsonObject = new JSONObject();
        jsonObject.put("testJson", mock(JSONArray.class));

        //act
        Map<String, Object> map = MapConverter.toMap(jsonObject);

        //assert
        assertNotNull(map);
        assertEquals(map.size(), 1);
        assertThat(map.get("testJson"), instanceOf(List.class));
    }

    @Test
    public void runToMapWithJsonArray() throws Exception {
        //arrange
        JSONArray jsonArray = new JSONArray();
        jsonArray.put("test");
        jsonArray.put("array");

        //act
        ArrayList<Object> list = MapConverter.toMap(jsonArray);

        //assert
        assertNotNull(list);
        assertEquals(list.size(), 2);
    }

    @Test
    public void runToMapWithJsonArrayAndJSONObject() throws Exception {
        //arrange
        JSONArray jsonArray = new JSONArray();
        jsonArray.put(mock(JSONObject.class));
        jsonArray.put(mock(JSONObject.class));
        jsonArray.put(mock(JSONObject.class));

        //act
        ArrayList<Object> list = MapConverter.toMap(jsonArray);

        //assert
        assertNotNull(list);
        assertEquals(list.size(), 3);
        assertThat(list.get(0), instanceOf(Map.class));
    }

}
