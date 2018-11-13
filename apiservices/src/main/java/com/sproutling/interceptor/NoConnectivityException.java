package com.sproutling.interceptor;

import java.io.IOException;

public class NoConnectivityException extends IOException {
    public static final String CONNECTION_EXCEPTION="No connectivity exception";

    @Override
    public String getMessage() {
        return CONNECTION_EXCEPTION;
    }
}

