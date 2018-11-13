package com.sproutling.apiservices;

import com.sproutling.pojos.CreateHandheldRequestBody;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.pojos.GetHandHeldResponse;
import com.sproutling.pojos.UpdateHandheldRequestBody;
import com.sproutling.pojos.UpdateHandheldResponse;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.DELETE;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by subram13 on 3/15/17.
 */

public class HandheldApiBuilder extends ApiBuilder<HandheldApiBuilder.HandheldApi> {
    private static final String CREATE_HANDHELD_URL = "identity/v1/handhelds";
    private static final String HANDHELD_URL = "identity/v1/handhelds/{id}";

    @Override
    protected Class<HandheldApi> getService() {
        return HandheldApi.class;
    }

    @Override
    protected HandheldApi getMockInstance() {
        return null;
    }

    public interface HandheldApi {
        @POST(CREATE_HANDHELD_URL)
        Call<CreateHandheldResponse> createHandheld(@Body CreateHandheldRequestBody createHandheldRequestBody);

        @GET(HANDHELD_URL)
        Call<GetHandHeldResponse> getHandheld(@Path("id") String id);

        @PUT(HANDHELD_URL)
        Call<UpdateHandheldResponse> updateHandheld(@Path("id") String id, @Body UpdateHandheldRequestBody updateHandheldRequestBody);

        @DELETE(HANDHELD_URL)
        Call<ResponseBody> deleteHandheld(@Path("id") String id);

    }
}
