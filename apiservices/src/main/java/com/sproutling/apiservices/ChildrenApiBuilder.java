package com.sproutling.apiservices;

import com.sproutling.pojos.Child;
import com.sproutling.pojos.CreateChildRequestBody;
import com.sproutling.pojos.CreateChildResponse;
import com.sproutling.pojos.UpdateChildRequestBody;
import com.sproutling.pojos.UpdateChildResponse;

import java.util.ArrayList;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;

/**
 * Created by subram13 on 3/6/17.
 */

public class ChildrenApiBuilder extends ApiBuilder<ChildrenApiBuilder.ChildrenApi> {
    private static final String CREATE_CHILD_URL = "identity/v1/children";
    private static final String UPDATE_CHILD_URL = "identity/v1/children/{child_id}";

    @Override
    protected Class<ChildrenApi> getService() {
        return ChildrenApi.class;
    }

    @Override
    protected ChildrenApi getMockInstance() {
        return null;
    }

    public interface ChildrenApi {
        @POST(CREATE_CHILD_URL)
        Call<CreateChildResponse> createChild(@Body CreateChildRequestBody createChildRequestBody);

        @PUT(UPDATE_CHILD_URL)
        Call<UpdateChildResponse> updateChild(@Path("child_id") String childID, @Body UpdateChildRequestBody updateChildRequestBody);

        @GET(CREATE_CHILD_URL)
        Call<ArrayList<Child>> getChildren();

    }
}
