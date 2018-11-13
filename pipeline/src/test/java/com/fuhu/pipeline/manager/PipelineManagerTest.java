package com.fuhu.pipeline.manager;

import android.os.Handler;
import android.os.Looper;

import com.fuhu.pipeline.component.BasePipeline;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.contract.IPipeTaskList;
import com.fuhu.pipeline.contract.IPipeline;
import com.fuhu.pipeline.internal.DoPipelineThread;
import com.fuhu.pipeline.internal.PipeLog;
import com.fuhu.pipeline.parser.JsonObjWrapper;
import com.fuhu.pipeline.parser.JsonParser;
import com.fuhu.pipeline.util.ExecutorUtil;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;
import org.powermock.reflect.Whitebox;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.doNothing;
import static org.powermock.api.mockito.PowerMockito.mock;
import static org.powermock.api.mockito.PowerMockito.spy;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;
import static org.powermock.api.mockito.PowerMockito.verifyStatic;
import static org.powermock.api.support.membermodification.MemberMatcher.method;

/**
 * Created by allanshih on 2017/2/10.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PipelineManager.class, PipeLog.class, Looper.class, Executors.class,
        ExecutorUtil.class, HttpClientManager.class, MqttManager.class, JsonObjWrapper.class, JsonParser.class})
public class PipelineManagerTest {
    @Before
    public void setup() throws Exception {
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class, Looper.class);
    }

    @Test
    public void runGetInstance() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());

        //act
        Handler mHandler = Whitebox.getInternalState(pipelineManager, "mHandler");

        //assert
        assertNotNull(mHandler);
    }

    @Test
    public void runDoPipeline() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        doNothing().when(pipelineManager, method(PipelineManager.class, "process", IPipeline.class))
                .withArguments(any(IPipeline.class));

        //act
        pipelineManager.doPipeline(mock(IPipeTaskList.class), mock(IPipeItem.class), mock(IPipeCallback.class));

        //assert
        verifyPrivate(pipelineManager, times(1)).invoke("process", any(IPipeline.class));
    }

    @Test
    public void runDoPipelineWithIPipeTaskListNull() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        doNothing().when(pipelineManager, method(PipelineManager.class, "process", IPipeline.class))
                .withArguments(any(IPipeline.class));

        //act
        pipelineManager.doPipeline(null, mock(IPipeItem.class), mock(IPipeCallback.class));

        //assert
        verifyPrivate(pipelineManager, never()).invoke("process", any(IPipeline.class));
    }

    @Test
    public void runDoPipelineWithIPipeItemNull() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        doNothing().when(pipelineManager, method(PipelineManager.class, "process", IPipeline.class))
                .withArguments(any(IPipeline.class));

        //act
        pipelineManager.doPipeline(mock(IPipeTaskList.class), null, mock(IPipeCallback.class));

        //assert
        verifyPrivate(pipelineManager, never()).invoke("process", any(IPipeline.class));
    }

    @Test
    public void runDoPipelineWithCallbackNull() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        doNothing().when(pipelineManager, method(PipelineManager.class, "process", IPipeline.class))
                .withArguments(any(IPipeline.class));

        //act
        pipelineManager.doPipeline(mock(IPipeTaskList.class), mock(IPipeItem.class), null);

        //assert
        verifyPrivate(pipelineManager, times(1)).invoke("process", any(IPipeline.class));
    }

    @Test
    public void runDoPipelineWithPipeline() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        doNothing().when(pipelineManager, method(PipelineManager.class, "process", IPipeline.class))
                .withArguments(any(IPipeline.class));
        BasePipeline pipeline = new BasePipeline(mock(IPipeTaskList.class), mock(IPipeItem.class), mock(IPipeCallback.class));

        //act
        pipelineManager.doPipeline(pipeline);

        //assert
        verifyPrivate(pipelineManager, times(1)).invoke("process", any(IPipeline.class));
    }

    @Test
    public void runDoPipelineWithPipelineNull() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        doNothing().when(pipelineManager, method(PipelineManager.class, "process", IPipeline.class))
                .withArguments(any(IPipeline.class));

        //act
        pipelineManager.doPipeline((IPipeline)null);

        //assert
        verifyPrivate(pipelineManager, never()).invoke("process", any(IPipeline.class));
    }

    @Test
    public void runProcess() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        ExecutorService executor = Mockito.spy(Executors.newCachedThreadPool());
        Whitebox.setInternalState(pipelineManager, "pipelineExecutor", executor);
        BasePipeline pipeline = new BasePipeline(mock(IPipeTaskList.class), mock(IPipeItem.class), mock(IPipeCallback.class));
        doReturn(mock(Future.class)).when(executor).submit(any(DoPipelineThread.class));

        //act
        Whitebox.invokeMethod(pipelineManager, "process", pipeline);

        //assert
        verify(executor, times(1)).submit(any(DoPipelineThread.class));
    }

    @Test
    public void runProcessWithPipelineNull() throws Exception {
        //arrange
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        ExecutorService executor = Mockito.spy(Executors.newCachedThreadPool());
        Whitebox.setInternalState(pipelineManager, "pipelineExecutor", executor);
        doReturn(mock(Future.class)).when(executor).submit(any(DoPipelineThread.class));

        //act
        Whitebox.invokeMethod(pipelineManager, "process", null);

        //assert
        verify(executor, never()).submit(any(DoPipelineThread.class));
    }

    @Test
    public void runShutdown() throws Exception {
        //arrange
        PowerMockito.mock(ExecutorUtil.class);
        PipelineManager pipelineManager = spy(PipelineManager.getInstance());
        ExecutorService executor = Mockito.spy(Executors.newCachedThreadPool());
        Whitebox.setInternalState(pipelineManager, "pipelineExecutor", executor);

        Handler mHandler = mock(Handler.class);
        Whitebox.setInternalState(pipelineManager, "mHandler", mHandler);

        //act
        pipelineManager.shutdown();

        //assert
        verify(mHandler).removeCallbacksAndMessages(any());

        verifyStatic();
        ExecutorUtil.shutdownAndAwaitTermination(any(ExecutorService.class));
    }

    @Test
    public void runRelease() throws Exception {
        //arrange
        PipelineManager INSTANCE = mock(PipelineManager.class);
        Whitebox.setInternalState(PipelineManager.class, "INSTANCE", INSTANCE);

        //act
        PipelineManager.release();

        //assert
        verify(INSTANCE).shutdown();
    }
}
