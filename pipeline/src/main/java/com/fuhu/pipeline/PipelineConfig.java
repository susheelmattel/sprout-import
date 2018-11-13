package com.fuhu.pipeline;

import okhttp3.MediaType;

public final class PipelineConfig {
    public static final boolean DEBUG = true;
    public static final String APPLICATION_ID = "com.fuhu.pipeline";
    public static final String BUILD_TYPE = "release";
    public static final String FLAVOR = "";
    public static final int VERSION_CODE = -1;
    public static final String VERSION_NAME = "";
    public static final long TIMEOUT_PROCESS = 60000L;

    // Http
    public static final String CHARSET_NAME = "UTF-8";
    public static final long SIZE_OF_CACHE = 10 * 1024 * 1024; // 10 MB
    public static final long TIMEOUT_CONNECTION = 200000L;
    public static final long TIMEOUT_SOCKET = 300000L;
    public static final long TIMEOUT_READ = 300000L;
    public static final MediaType JSON_TYPE = MediaType.parse("application/json; charset=utf-8");
}
