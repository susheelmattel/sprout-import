package com.fuhu.pipeline.internal;

import android.util.Log;

import com.fuhu.pipeline.component.HttpItem;
import com.fuhu.pipeline.contract.APipeItem;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.parser.JsonParser;

import org.json.JSONObject;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PipeLog.class, Log.class, JsonParser.class, JSONObject.class})
public class PipeCallbackThreadTest {
    @Mock
    private IPipeCallback callback;

    private String responseString = "{\"access_token\":\"eyJ0eXA\",\"refresh_token\":\"ca589dd043\"}";

    private class TestModel extends APipeItem {
        private String access_token;
        private String refresh_token;

        public String getAccessToken() {
            return access_token;
        }

        public String getRefreshToken() {
            return refresh_token;
        }
    }

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class);
    }

    @Test
    public void runCallbackNull() throws Exception {
        // arrange

        // act
        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(null, null);
        pipeCallbackThread.run();

        // assert
        PowerMockito.verifyStatic();
        PipeLog.w(Mockito.anyString(), Mockito.anyString());
    }

    @Test
    public void runPipeItemNull() throws Exception {
        //arrange

        //act
        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(null, callback);
        pipeCallbackThread.run();

        //assert
        Mockito.verify(callback).onError(PipeStatus.PIPEITEM_NULL, null);
    }

    @Test
    public void runDefaultStatus() throws Exception {
        //arrange
        PowerMockito.mockStatic(Log.class);

        //act
        HttpItem pipeItem = new HttpItem();
        pipeItem.setPipeStatus(PipeStatus.DEFAULT);

        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(pipeItem, callback);
        pipeCallbackThread.run();

        //assert
        Mockito.verify(callback).onError(PipeStatus.DEFAULT, pipeItem);
    }

    @Test
    public void runSuccess() throws Exception {
        //arrange
        PowerMockito.mockStatic(Log.class);

        //act
        HttpItem pipeItem = new HttpItem();
        pipeItem.setPipeStatus(PipeStatus.SUCCESS);

        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(pipeItem, callback);
        pipeCallbackThread.run();

        //assert
        Mockito.verify(callback).onResult(pipeItem);
    }

    @Test
    public void runJsonParseFailed() throws Exception {
        //arrange
        PowerMockito.mockStatic(Log.class);

        //act
        HttpItem pipeItem = new HttpItem();
        pipeItem.setPipeStatus(PipeStatus.JSON_PARSE_FAILED);

        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(pipeItem, callback);
        pipeCallbackThread.run();

        //assert
        Mockito.verify(callback).onError(pipeItem.getPipeStatus(), pipeItem);
    }

    @Test
    public void runUnknownError() throws Exception {
        //arrange
        PowerMockito.mockStatic(Log.class);

        //act
        HttpItem pipeItem = new HttpItem();
        pipeItem.setPipeStatus(PipeStatus.UNKNOWN_ERROR);

        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(pipeItem, callback);
        pipeCallbackThread.run();

        //assert
        Mockito.verify(callback).onError(pipeItem.getPipeStatus(), pipeItem);
    }

    @Test
    public void runReturnNullObject() throws Exception {
        //arrange
        PowerMockito.mockStatic(JsonParser.class);

        //act
        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(null, callback);
        Object result = Whitebox.invokeMethod(pipeCallbackThread, "convertHttpResponse", (Object)null);

        //assert
        assertNull(result);
        verifyPrivate(pipeCallbackThread).invoke("convertHttpResponse", (Object)null);
    }

    @Test
    public void runReturnNotNullObject() throws Exception {
        //arrange
        PowerMockito.mockStatic(JsonParser.class);

        //act
        Object responseObject = Mockito.mock(Object.class);

        HttpItem pipeItem = new HttpItem();
        pipeItem.setResponseObject(responseObject);

        PipeCallbackThread pipeCallbackThread = new PipeCallbackThread(pipeItem, callback);
        Object result = Whitebox.invokeMethod(pipeCallbackThread, "convertHttpResponse", pipeItem);

        //assert
        assertNotNull(result);
        verifyPrivate(pipeCallbackThread).invoke("convertHttpResponse", pipeItem);
    }
}