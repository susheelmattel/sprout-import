package com.fuhu.pipeline.manager;

import android.content.Context;

import com.fuhu.pipeline.PipelineConfig;

import java.io.File;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.X509TrustManager;

import okhttp3.Cache;
import okhttp3.OkHttpClient;

import static com.fuhu.pipeline.PipelineConfig.SIZE_OF_CACHE;
import static com.fuhu.pipeline.PipelineConfig.TIMEOUT_READ;
import static com.fuhu.pipeline.PipelineConfig.TIMEOUT_SOCKET;

public class HttpClientManager {
    private static final String TAG = HttpClientManager.class.getSimpleName();
    private static final String CACHE_NAME= "okhttp3-cache";

    private static HttpClientManager INSTANCE;
    private OkHttpClient mOkHttpClient;
    private OkHttpClient mOkHttpsClient;

    /*
     * A private Constructor prevents any other
     * class from instantiating.
     */
    private HttpClientManager() {

    }

    /**
     * Get an instance of PipelineManager.
     * @return
     */
    public static HttpClientManager getInstance() {
        if (INSTANCE == null) {
            synchronized(HttpClientManager.class) {
                if (INSTANCE == null) {
                    INSTANCE = new HttpClientManager();
                }
            }
        }
        return INSTANCE;
    }

    /**
     * Create an instance of the OkHttpClient for HTTP.
     * @param context
     * @return
     */
    public OkHttpClient createOkHttpClient(final Context context) {
        if (mOkHttpClient == null) {
            // Create a shared instance with custom settings.
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .connectTimeout(PipelineConfig.TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS)
                    .readTimeout(TIMEOUT_READ, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT_SOCKET, TimeUnit.MILLISECONDS);

            if (context != null) {
                // Install an HTTP cache in the application cache directory.
                File cacheDir = new File(context.getApplicationContext().getCacheDir(), CACHE_NAME);
                Cache cache = new Cache(cacheDir, SIZE_OF_CACHE);
                builder.cache(cache);
            }
            mOkHttpClient = builder.build();
        }
        return mOkHttpClient;
    }

    /**
     * Create an instance of the OkHttpClient for HTTPS.
     * @param context
     * @return
     */
    public OkHttpClient createOkHttpsClient(final Context context, final SSLSocketFactory sslSocketFactory,
                                            final X509TrustManager x509TrustManager) {
        if (mOkHttpsClient == null) {
            // Create a shared instance with custom settings.
            OkHttpClient.Builder builder = new OkHttpClient().newBuilder()
                    .connectTimeout(PipelineConfig.TIMEOUT_CONNECTION, TimeUnit.MILLISECONDS)
                    .readTimeout(TIMEOUT_READ, TimeUnit.MILLISECONDS)
                    .writeTimeout(TIMEOUT_SOCKET, TimeUnit.MILLISECONDS);

            // Install an HTTPS cache in the application cache directory.
            if (context != null) {
                File cacheDir = new File(context.getApplicationContext().getCacheDir(), CACHE_NAME);
                Cache cache = new Cache(cacheDir, SIZE_OF_CACHE);
                builder.cache(cache);
            }

            if (sslSocketFactory != null && x509TrustManager != null) {
                builder.sslSocketFactory(sslSocketFactory, x509TrustManager);
            }

            mOkHttpsClient = builder.build();
        }

        return mOkHttpsClient;
    }

    /**
     * Close the OkHttpClient.
     * @param okHttpClient
     * @throws IOException
     */
    public void close(final OkHttpClient okHttpClient) {
        if (okHttpClient != null) {
            // Cancel all calls currently enqueued or executing.
            okHttpClient.dispatcher().cancelAll();

            // Clear the connection pool
            okHttpClient.connectionPool().evictAll();

            // Close the cache of the OkHttpClient();
            try {
                okHttpClient.cache().close();
            } catch (IOException ie) {
                ie.printStackTrace();
            }
        }
    }

    /**
     * Close all HttpClient of this HttpClientManager.
     */
    public void closeAllClient() {
        if (mOkHttpClient != null) {
            close(mOkHttpClient);
        }
        if (mOkHttpsClient != null) {
            close(mOkHttpsClient);
        }
    }

    /**
     * Destroy object.
     */
    public static void release() {
        if (INSTANCE != null) {
            INSTANCE.closeAllClient();
        }
        INSTANCE = null;
    }
}
