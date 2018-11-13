package com.sproutling.object;

import java.security.PublicKey;

/**
 * Created by luppoldj on 10/25/17.
 */

public class PublicKeyEvent {
    private PublicKey  mPublicKey;

    public PublicKeyEvent(PublicKey publicKey) {
        mPublicKey = publicKey;
    }

    public PublicKey getPublicKey() {
        return mPublicKey;
    }
}
