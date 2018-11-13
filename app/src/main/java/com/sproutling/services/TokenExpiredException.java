/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

/**
 * Created by bradylin on 3/17/17.
 */

public class TokenExpiredException extends SSException {

    public TokenExpiredException(SSError error) {
        super(error);
    }

    public TokenExpiredException(SSError error, int responseCode) {
        super(error, responseCode);
    }
}
