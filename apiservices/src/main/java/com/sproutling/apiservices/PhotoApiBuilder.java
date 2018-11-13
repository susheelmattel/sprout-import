package com.sproutling.apiservices;

import com.sproutling.pojos.CreatePhotoResponse;
import com.sproutling.pojos.GetPhotoResponse;

import okhttp3.MultipartBody;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Path;

/**
 * Created by subram13 on 3/6/17.
 */

public class PhotoApiBuilder extends ApiBuilder<PhotoApiBuilder.PhotoApi> {
    private static final String CREATE_PHOTO_URL = "identity/v1/children/{id}/photos";
    private static final String GET_PHOTO_URL = "identity/v1/photos/{id}";
    private static final String DOWNLOAD_PHOTO_URL = "identity{urlPath}";

    @Override
    protected Class<PhotoApi> getService() {
        return PhotoApi.class;
    }

    @Override
    protected PhotoApi getMockInstance() {
        return null;
    }

    public interface PhotoApi {
        @Multipart
        @POST(CREATE_PHOTO_URL)
        Call<CreatePhotoResponse> createPhoto(@Path("id") String childId,@Part MultipartBody.Part photofile);

        @GET(GET_PHOTO_URL)
        Call<GetPhotoResponse> getPhoto(@Path("id") String photoId);

        @GET(DOWNLOAD_PHOTO_URL)
        Call<ResponseBody> downloadPhoto(@Path("urlPath") String urlPath);
    }
}
