package com.fuhu.pipeline.internal;

import com.fuhu.pipeline.contract.APipeTask;
import com.fuhu.pipeline.contract.IPipeItem;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;

/**
 * Created by allanshih on 2017/2/8.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({DoPipeTaskThread.class, PipeLog.class})
public class DoPipeTaskThreadTest {
    @Mock
    private APipeTask pipeTask;

    @Mock
    private IPipeItem pipeItem;

    @Before
    public void setup(){
        MockitoAnnotations.initMocks(this); // initialize all the @Mock objects
        // Setup other Static Mocks
        PowerMockito.mockStatic(PipeLog.class);
    }

    @Test
    public void runDoPipeTask() throws Exception {
        // arrange
        DoPipeTaskThread doPipeTaskThread = Mockito.spy(new DoPipeTaskThread(pipeTask, pipeItem, 1000L));

        // act
        doPipeTaskThread.call();

        // assert
        verify(pipeTask).process(pipeItem);
    }

    @Test
    public void runPipeTaskNull() throws Exception {
        // arrange
        DoPipeTaskThread doPipeTaskThread = Mockito.spy(new DoPipeTaskThread(null, pipeItem, 1000L));

        // act
        doPipeTaskThread.call();

        // assert
        verify(pipeTask, never()).process(pipeItem);
    }

    @Test
    public void runPipeItemNull() throws Exception {
        // arrange
        DoPipeTaskThread doPipeTaskThread = Mockito.spy(new DoPipeTaskThread(pipeTask, null, 1000L));

        // act
        doPipeTaskThread.call();

        // assert
        verify(pipeTask, never()).process(pipeItem);
    }

    @Test
    public void runDoPipeTaskIsDone() throws Exception {
        // arrange
        DoPipeTaskThread doPipeTaskThread = Mockito.spy(new DoPipeTaskThread(pipeTask, pipeItem, 1000L));
        Mockito.when(pipeTask.isDone()).thenReturn(true);

        // act
        Boolean result = doPipeTaskThread.call();

        // assert
        verify(pipeTask).process(pipeItem);
        verify(pipeTask).isDone();
        assertTrue(result);
    }

    @Test
    public void runDoPipeTaskIsProcessing() throws Exception {
        // arrange
        DoPipeTaskThread doPipeTaskThread = Mockito.spy(new DoPipeTaskThread(pipeTask, pipeItem, 1000L));
        Mockito.when(pipeTask.isDone()).thenReturn(false);

        // act
        Boolean result = doPipeTaskThread.call();

        // assert
        verify(pipeTask).process(pipeItem);
        verify(pipeTask).isDone();
        assertFalse(result);
    }
}
