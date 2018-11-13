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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.instanceOf;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;

/**
 * Created by allanshih on 2017/2/10.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({JsonObjWrapper.class, PipeLog.class})
public class JsonObjWrapperTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class);
    }

    @Test
    public void runWrapWithNull() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());

        // act
        Object object = jsonObjWrapper.wrap(null);

        // assert
        assertNotNull(object);
        assertEquals(object, JSONObject.NULL);
    }

    @Test
    public void runWrapWithJSONArray() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());

        // act
        Object object = jsonObjWrapper.wrap(mock(JSONArray.class));

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(JSONArray.class));
    }

    @Test
    public void runWrapWithJSONObject() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());

        // act
        Object object = jsonObjWrapper.wrap(mock(JSONObject.class));

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(JSONObject.class));
    }

    @Test
    public void runWrapWithJSONObjectNull() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());

        // act
        Object object = jsonObjWrapper.wrap(JSONObject.NULL);

        // assert
        assertNotNull(object);
        assertEquals(object, JSONObject.NULL);
    }

    @Test
    public void runWrapWithCollection() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());
        List<String> list = new ArrayList<>();

        // act
        Object object = jsonObjWrapper.wrap(list);

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(JSONArray.class));
    }

    @Test
    public void runWrapWithArray() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());
        String [] array = {"1"};

        // act
        Object object = jsonObjWrapper.wrap(array);

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(JSONArray.class));
    }

    @Test
    public void runWrapWithMap() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());
        Map<String, String> map = new HashMap<>();

        // act
        Object object = jsonObjWrapper.wrap(map);

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(JSONObject.class));
    }

    @Test
    public void runWrapWithPrimitiveTypeInt() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());

        // act
        Object object = jsonObjWrapper.wrap(123);

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(Integer.class));
    }

    @Test
    public void runWrapWithPrimitiveTypeFloat() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());

        // act
        Object object = jsonObjWrapper.wrap(123.2f);

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(Float.class));
    }

    @Test
    public void runWrapWithPrimitiveTypeByte() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());

        // act
        Object object = jsonObjWrapper.wrap(mock(Byte.class));

        // assert
        assertNotNull(object);
        assertThat(object, instanceOf(Byte.class));
    }

    @Test
    public void runToJSONArray() throws Exception {
        // arrange
        JsonObjWrapper jsonObjWrapper = spy(JsonObjWrapper.getInstance());
        String [] array = {"1", "2", "3"};

        // act
        JSONArray jsonArray = jsonObjWrapper.toJSONArray(array);

        // assert
        assertNotNull(jsonArray);
        assertEquals(jsonArray.length(), 3);
        verify(jsonObjWrapper, times(3)).wrap(any(Object.class));
    }
}
