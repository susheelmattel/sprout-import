package com.sproutling.apiservices;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.sproutling.api.SproutlingApi;
import com.sproutling.interceptor.ConnectivityInterceptor;
import com.sproutling.pojos.LoginResponse;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.Buffer;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by subram13 on 3/6/17.
 */

public abstract class ApiBuilder<T> {
    private static final String MEDIA_CONTENT_TYPE = "multipart/form-data";
    private static final String MULTI_PART = "multipart";
    private static final String JSON_CONTENT_TYPE = "application/json";
    private static final String AUTH_PREFIX = "Bearer ";
    private static final String TAG = "ApiBuilder";
    private static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssZZZZZ";
    //public static String SERVER_URL = "https://api-dev-us.sproutlingcloud.com/"; 
    public static String SERVER_URL = "https://api-frog.sproutlingcloud.com/";
    public static Context apicontext;
    private static Gson sGson = new GsonBuilder().setDateFormat(DATE_FORMAT)
            .create();

    private static String bodyToString(final Request request) {

        try {
            final Request copy = request.newBuilder().build();
            final Buffer buffer = new Buffer();
            copy.body().writeTo(buffer); // TODO: look out for exception from body()
            return buffer.readUtf8();
        } catch (final Exception e) {
            return "body failed";
        }
    }

    protected abstract Class<T> getService();

    public T createApi() {
        return createApi(getGson(), SERVER_URL);
    }

    public T createApi(Gson gson, String serverUrl) {
        return getRestAdapter(gson, serverUrl).create(getService());
    }

    protected abstract T getMockInstance();

    protected Retrofit getRestAdapter(Gson gson,
                                      String serverUrl) {
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(serverUrl)
                .client(getHttpClient())
                .addConverterFactory(GsonConverterFactory.create(gson))
                .build();

        return retrofit;
    }

    private OkHttpClient getHttpClient() {
        OkHttpClient.Builder httpClient = new OkHttpClient.Builder();
        httpClient.addInterceptor(new ConnectivityInterceptor(apicontext));
        httpClient.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request original = chain.request();

                // Customize the request
                Request.Builder requestbuilder = original.newBuilder()
                        .method(original.method(), original.body());

                if (!isMediaContent(original.body())) {
                    requestbuilder.addHeader("Content-Type", JSON_CONTENT_TYPE);
                    requestbuilder.addHeader("Accept", JSON_CONTENT_TYPE);
                }
                //TODO:get Auth token from correct impl
                String authToken = SproutlingApi.getAuthToken();
                if (!TextUtils.isEmpty(authToken)) {
                    requestbuilder.header("Authorization", AUTH_PREFIX + authToken);
                }

                logRequest(requestbuilder.build());
                Response response = chain.proceed(requestbuilder.build());
                logResponse(response);
                // Customize or return the response
                return response;
            }
        });

        OkHttpClient client = httpClient.build();
        return client;
    }

    private void setAccessToken(Response response) {
        if (response.request().url().toString().contains(OAuth2ApiBuilder.LOGIN_URL)) {
            LoginResponse loginResponse = toObjectFromJson(LoginResponse.class, convertByteStreamToString(response.body().byteStream()));
            SproutlingApi.setAuthToken(loginResponse.getAccessToken());
        }
    }

    private <T> T toObjectFromJson(Class<T> className, String jsonString) {
        Gson gson = new Gson();
        return gson.fromJson(jsonString, className);
    }

    private String convertByteStreamToString(InputStream inputStream) {
        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        return sb.toString();
    }

    private boolean isMediaContent(okhttp3.RequestBody requestBody) {
        if ((requestBody != null) && (requestBody.contentType() != null) && (requestBody.contentType().type().equals(MULTI_PART))) {
            Log.d(TAG, requestBody.contentType().type());
            return true;
        } else
            return false;
    }

    protected Gson getGson() {
        return sGson;
    }

    private void logRequest(okhttp3.Request request) {
        Log.d(TAG, "logRequest: URL :" + request.url().toString());
        Log.d(TAG, "logRequest: Headers :" + request.headers());
        Log.d(TAG, "logRequest: body :" + bodyToString(request));
    }

    private void logResponse(Response response) {
        Log.d(TAG, "logResponse: URL :" + response.request().url().toString());
        Log.d(TAG, "logResponse: Status code :" + response.code());
//        try {
//            Log.d(TAG, "logResponse: body :" + response.body().string());
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }

}
