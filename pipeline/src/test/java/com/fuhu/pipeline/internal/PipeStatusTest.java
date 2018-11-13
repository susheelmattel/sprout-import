package com.fuhu.pipeline.internal;

import com.fuhu.pipeline.component.HttpItem;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

/**
 * Created by allanshih on 2017/2/2.
 */

@RunWith(PowerMockRunner.class)
@PrepareForTest({PipeStatus.class})
public class PipeStatusTest {
    @Test
    public void testDefaultStatus() throws Exception {
        //Execute
        boolean result = PipeStatus.isSuccess(PipeStatus.DEFAULT);

        //Verify
        assertFalse(result);
    }

    @Test
    public void testSuccessStatus() throws Exception {
        //Execute
        boolean result = PipeStatus.isSuccess(PipeStatus.SUCCESS);

        //Verify
        assertTrue(result);
    }

    @Test
    public void testUnknownErrorStatus() throws Exception {
        //Execute
        boolean result = PipeStatus.isSuccess(PipeStatus.UNKNOWN_ERROR);

        //Verify
        assertFalse(result);
    }

    @Test
    public void testIsErrorWithStatusCode() throws Exception {
        //Execute
        boolean result = PipeStatus.isError(PipeStatus.UNKNOWN_ERROR);

        //Verify
        assertTrue(result);
    }

    @Test
    public void testDefaultStatusWithPipeItem() throws Exception {
        // arrange
        HttpItem httpItem = new HttpItem();
        httpItem.setPipeStatus(PipeStatus.DEFAULT);

        //Execute
        boolean result = PipeStatus.isError(httpItem);

        //Verify
        assertFalse(result);
    }

    @Test
    public void testSuccessStatusWithPipeItem() throws Exception {
        // arrange
        HttpItem httpItem = new HttpItem();
        httpItem.setPipeStatus(PipeStatus.SUCCESS);

        //Execute
        boolean result = PipeStatus.isError(httpItem);

        //Verify
        assertFalse(result);
    }

    @Test
    public void testErrorStatusWithPipeItem() throws Exception {
        // arrange
        HttpItem httpItem = new HttpItem();
        httpItem.setPipeStatus(PipeStatus.UNKNOWN_ERROR);

        //Execute
        boolean result = PipeStatus.isError(httpItem);

        //Verify
        assertTrue(result);
    }
}
