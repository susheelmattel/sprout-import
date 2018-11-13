package com.fuhu.pipeline.internal;

import android.util.Log;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.powermock.api.mockito.PowerMockito;
import org.powermock.core.classloader.annotations.PrepareForTest;
import org.powermock.modules.junit4.PowerMockRunner;

import static org.powermock.api.mockito.PowerMockito.mockStatic;

@RunWith(PowerMockRunner.class)
@PrepareForTest({PipeLog.class, Log.class})
public class PipeLogTest {
    private static final String TAG = PipeLogTest.class.getSimpleName();
    @Test
    public void testVerbose() throws Exception {
        // Mock all the static methods in PipeLog class
        mockStatic(Log.class);

        //Execute
        PipeLog.v(TAG, "debug");

        //Verify
        PowerMockito.verifyStatic();
        Log.v(TAG, "debug"); //Now verifyStatic knows what to verify.
    }

    @Test
    public void testDebug() throws Exception {
        // Mock all the static methods in PipeLog class
        mockStatic(Log.class);

        //Execute
        PipeLog.d(TAG, "debug");

        //Verify
        PowerMockito.verifyStatic();
        Log.d(TAG, "debug"); //Now verifyStatic knows what to verify.
    }

    @Test
    public void testInfo() throws Exception {
        // Mock all the static methods in PipeLog class
        mockStatic(Log.class);

        //Execute
        PipeLog.i(TAG, "debug");

        //Verify
        PowerMockito.verifyStatic();
        Log.i(TAG, "debug"); //Now verifyStatic knows what to verify.
    }

    @Test
    public void testWarn() throws Exception {
        // Mock all the static methods in PipeLog class
        mockStatic(Log.class);

        //Execute
        PipeLog.w(TAG, "debug");

        //Verify
        PowerMockito.verifyStatic();
        Log.w(TAG, "debug"); //Now verifyStatic knows what to verify.
    }

    @Test
    public void testError() throws Exception {
        // Mock all the static methods in PipeLog class
        mockStatic(Log.class);

        //Execute
        PipeLog.e(TAG, "debug");

        //Verify
        PowerMockito.verifyStatic();
        Log.e(TAG, "debug"); //Now verifyStatic knows what to verify.
    }
}