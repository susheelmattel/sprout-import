package com.sproutling.apiservices;

import com.sproutling.pojos.AppVersionResponseBody;

import retrofit2.Call;
import retrofit2.http.GET;

/**
 * Created by subram13 on 12/19/17.
 */

public class AppVersionApiBuilder extends ApiBuilder<AppVersionApiBuilder.AppVersionApi> {
    private static final String APP_VERSION_URL = "update/v1/versioninfo";

    @Override
    protected Class<AppVersionApi> getService() {
        return AppVersionApi.class;
    }

    @Override
    protected AppVersionApi getMockInstance() {
        return null;
    }

    public interface AppVersionApi {
        @GET(APP_VERSION_URL)
        Call<AppVersionResponseBody> getAppVersion();
    }
}
