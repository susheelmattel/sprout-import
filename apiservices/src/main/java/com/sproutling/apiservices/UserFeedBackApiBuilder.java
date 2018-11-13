package com.sproutling.apiservices;

import com.sproutling.pojos.UserStatusFeedbackRequestBody;
import com.sproutling.pojos.UserStatusFeedbackResponseBody;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.POST;

/**
 * Created by subram13 on 2/8/18.
 */

public class UserFeedBackApiBuilder extends ApiBuilder<UserFeedBackApiBuilder.UserFeedBackApi> {
    private static final String USER_STATUS_FEEDBACK_URL = "identity/v1/feedback/sleep_status";

    @Override
    protected Class<UserFeedBackApi> getService() {
        return UserFeedBackApi.class;
    }

    @Override
    protected UserFeedBackApi getMockInstance() {
        return null;
    }

    public interface UserFeedBackApi {
        @POST(USER_STATUS_FEEDBACK_URL)
        Call<UserStatusFeedbackResponseBody> sendUserStatusFeedback(@Body UserStatusFeedbackRequestBody userStatusFeedbackRequestBody);
    }
}
