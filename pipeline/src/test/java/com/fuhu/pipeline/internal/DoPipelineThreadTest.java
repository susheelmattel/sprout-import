package com.fuhu.pipeline.internal;

import android.os.Handler;

import com.fuhu.pipeline.component.BasePipeline;
import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeCallback;
import com.fuhu.pipeline.contract.IPipeItem;
import com.fuhu.pipeline.contract.IPipeTaskList;
import com.fuhu.pipeline.task.BuildOkHttpClientTask;
import com.fuhu.pipeline.task.BuildOkHttpRequestBodyTask;
import com.fuhu.pipeline.task.BuildOkHttpRequestTask;
import com.fuhu.pipeline.task.ConvertObjectToJsonTask;

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

import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Executor;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.doReturn;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.powermock.api.mockito.PowerMockito.verifyPrivate;

/**
 * Created by allanshih on 2017/2/2.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({DoPipelineThread.class, PipeLog.class, Executor.class})
public class DoPipelineThreadTest {
    @Mock
    private Handler mHandler;
    @Mock
    private IPipeItem pipeItem;
    @Mock
    private IPipeTaskList pipeTaskList;
    @Mock
    private IPipeCallback pipeCallback;
    @Mock
    private DoPipeTaskThread doPipeTaskThread;

    IPipeTaskList mockTaskList = new IPipeTaskList() {
        @Override
        public List<APipeTask> getTaskList(IPipeItem pipeItem) {
            List<APipeTask> taskList = new LinkedList<>();
            taskList.add(new BuildOkHttpClientTask());
            taskList.add(new ConvertObjectToJsonTask());
            taskList.add(new BuildOkHttpRequestBodyTask());
            taskList.add(new BuildOkHttpRequestTask());
            return taskList;
        }
    };

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class);
    }

    @Test
    public void runPipeLine() throws Exception {
        // arrange
        BasePipeline basePipeline = PowerMockito.spy(new BasePipeline(pipeTaskList, pipeItem, pipeCallback));

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(mHandler, basePipeline);
//        PowerMockito.doReturn(Mockito.anyListOf(APipeItem.class))
//                .when(doPipelineThread, "getTaskChain", pipeTaskList, pipeItem);
        doPipelineThread.run();

        // assert
//        verifyPrivate(doPipelineThread).invoke("getTaskChain", pipeTaskList, pipeItem);
        verify(mHandler).post((Runnable) any());
    }

    @Test
    public void runHandlerNull() throws Exception {
        // arrange
        BasePipeline basePipeline = new BasePipeline(pipeTaskList, pipeItem, pipeCallback);

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(null, basePipeline);
        doPipelineThread.run();

        // assert
        verify(mHandler, never()).post((Runnable) any());
    }

    @Test
    public void runPipelineNull() throws Exception {
        // arrange

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(mHandler, null);
        doPipelineThread.run();

        // assert
        verify(mHandler, never()).post((Runnable) any());
    }

    @Test
    public void runTaskListNull() throws Exception {
        // arrange
        BasePipeline basePipeline = new BasePipeline(null, pipeItem, pipeCallback);

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(mHandler, basePipeline);
        doPipelineThread.run();

        // assert
        verify(mHandler, never()).post((Runnable) any());
    }

    @Test
    public void runPipeItemNull() throws Exception {
        // arrange
        BasePipeline basePipeline = new BasePipeline(pipeTaskList, null, pipeCallback);

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(mHandler, basePipeline);
        doPipelineThread.run();

        // assert
        verify(mHandler, never()).post((Runnable) any());
    }

    @Test
    public void runCallbackNull() throws Exception {
        // arrange
        BasePipeline basePipeline = new BasePipeline(pipeTaskList, pipeItem, null);

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(mHandler, basePipeline);
        doPipelineThread.run();

        // assert
        verify(mHandler).post((Runnable) any());
    }

    @Test
    public void runGetTaskChain() throws Exception {
        // arrange
        BasePipeline basePipeline = PowerMockito.spy(new BasePipeline(mockTaskList, pipeItem, pipeCallback));

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(mHandler, basePipeline);
        List<APipeTask> result = Whitebox.invokeMethod(doPipelineThread, "getTaskChain", mockTaskList, pipeItem);

        // assert
        assertNotNull(result);
        verifyPrivate(doPipelineThread).invoke("getTaskChain", mockTaskList, pipeItem);
    }

    @Test
    public void runGetTaskChainNull() throws Exception {
        // arrange
        IPipeTaskList pipeTaskList = null;
        BasePipeline basePipeline = PowerMockito.spy(new BasePipeline(null, pipeItem, pipeCallback));

        // act
        DoPipelineThread doPipelineThread = new DoPipelineThread(mHandler, basePipeline);
        List<APipeTask> result = Whitebox.invokeMethod(doPipelineThread, "getTaskChain", (IPipeTaskList)null, pipeItem);

        // assert
        assertNull(result);
        verifyPrivate(doPipelineThread).invoke("getTaskChain", (IPipeTaskList)null, pipeItem);
    }

    @Test
    public void runExecutorSubmit() throws Exception {
        // arrange
        BasePipeline basePipeline = Mockito.spy(new BasePipeline(mockTaskList, pipeItem, pipeCallback));

        // act
        ExecutorService executor = Mockito.spy(Executors.newCachedThreadPool());
        DoPipelineThread doPipelineThread = Mockito.spy(new DoPipelineThread(mHandler, basePipeline, executor));
        doPipelineThread.run();

        // assert
        verify(executor, times(4)).submit(any(DoPipeTaskThread.class));
    }

    @Test
    public void runExecutorSubmitReturnTrue() throws Exception {
        // arrange
        BasePipeline basePipeline = Mockito.spy(new BasePipeline(mockTaskList, pipeItem, pipeCallback));

        // act
        ExecutorService executor = Mockito.spy(Executors.newCachedThreadPool());
        DoPipelineThread doPipelineThread = Mockito.spy(new DoPipelineThread(mHandler, basePipeline, executor));
        Future<Boolean> future = new Future<Boolean>() {
            @Override
            public boolean cancel(boolean mayInterruptIfRunning) {
                return false;
            }

            @Override
            public boolean isCancelled() {
                return false;
            }

            @Override
            public boolean isDone() {
                return false;
            }

            @Override
            public Boolean get() throws InterruptedException, ExecutionException {
                return true;
            }

            @Override
            public Boolean get(long timeout, TimeUnit unit) throws InterruptedException, ExecutionException, TimeoutException {
                return true;
            }
        };

        doReturn(future).when(executor).submit(any(DoPipeTaskThread.class));
        doPipelineThread.run();

        // assert
        assertTrue(future.get());
        verify(executor, times(4)).submit(any(DoPipeTaskThread.class));
    }
}
