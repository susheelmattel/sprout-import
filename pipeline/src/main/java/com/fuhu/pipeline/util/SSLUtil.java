package com.fuhu.pipeline.util;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

/**
 * Created by allanshih on 2017/3/1.
 */

public class SSLUtil {
    /**
     * Trusting all certificates over HTTPS.
     * @return SSLContext
     */
    public static SSLContext getAllTrustSSLContext() {
        SSLContext sslContext = null;
        try {
            sslContext = SSLContext.getInstance("TLS");
            sslContext.init(null, new TrustManager[]{getTrustManager()}, new SecureRandom());
        } catch (NoSuchAlgorithmException nae) {
            nae.printStackTrace();
        } catch (KeyManagementException kme) {
            kme.printStackTrace();
        }

        return sslContext;
    }

    /**
     * Default trust manager.
     * @return X509TrustManager
     */
    public static X509TrustManager getTrustManager() {
        X509TrustManager tm = new X509TrustManager() {
            public void checkClientTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public void checkServerTrusted(X509Certificate[] chain, String authType) throws CertificateException {
            }

            public X509Certificate[] getAcceptedIssuers() {
                return null;
            }
        };
        return tm;
    }
}
