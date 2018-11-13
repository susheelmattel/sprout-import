package com.fuhu.pipeline.internal;

import android.util.Log;

import com.fuhu.pipeline.PipelineConfig;

public class PipeLog {
	private static final int ENABLE_VERBOSE 	= 4;
    private static final int ENABLE_DEBUG 		= 3;
    private static final int ENABLE_INFO 		= 2;
    private static final int ENABLE_WARN 		= 1;
    private static final int ENABLE_ERROR 		= 0;
    private static final int ENABLE_ALL 		= 5;

    private static final int sLogLevel = ENABLE_ALL;

	public static void v(String tag, String msg){
		if(!PipelineConfig.DEBUG || sLogLevel < ENABLE_VERBOSE)
			return;
        Log.v(tag, msg);
    }
	
	public static void d(String tag, String msg){
		if(!PipelineConfig.DEBUG || sLogLevel < ENABLE_DEBUG)
			return;
        Log.d(tag, msg);
    }
	
	public static void i(String tag, String msg){
		if(!PipelineConfig.DEBUG || sLogLevel < ENABLE_INFO)
			return;
        Log.i(tag, msg);
    }
	
	public static void w(String tag, String msg){
		if(!PipelineConfig.DEBUG || sLogLevel < ENABLE_WARN)
			return;
        Log.w(tag, msg);
    }
	
	public static void e(String tag, String msg){
		if(!PipelineConfig.DEBUG || sLogLevel <= ENABLE_ERROR)
			return;
        Log.e(tag, msg);
    }
}