package com.sproutling.apitest.services;

import android.os.Build;

import com.sproutling.apitest.BuildConfig;
import com.sproutling.apitest.Utils;
import com.sproutling.apitest.http.Http;
import com.sproutling.apitest.http.HttpContent;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Sproutservices
 * Created by bradylin on 11/18/16.
 */

public class SSManagement {

    private static final String TAG = "SSManagement";

    // Temporary iOS client id
//    private static final String CLIENT_ID = "c3c04c54bd0a9f3f40517f1670c3c327c17d8d19425b416bc7169593e2563380"; //iOS
    private static final String CLIENT_ID = "4677a7fc91373cbbb035ad4f43c1ebaddfaa8f95344fd37c9efe3ea7afaca08e";

    private static final String AUTH_PREFIX = "Bearer ";
    private static final String PROTOCOL_SECURE = "https://";
    private static final String PROTOCOL = "http://";
    private static final String VERSION = "/v1";

    /**
     * New Endpoint
     */
//    private static final String ENDPOINT = PROTOCOL_SECURE + "api-dev.us.sproutlingcloud.com";
    private static final String ENDPOINT = PROTOCOL + "api-dev-us.sproutlingcloud.com";
    private static final String SERVICE_IDENTITY = "/identity";
    private static final String SERVICE_DEVICE = "/device";
    private static final String SERVICE_TIMELINE = "/timeline";
    private static final String SERVICE_UPDATE = "/update";

    /**
     * OLD Endpoint
     */
//    private static final String ENDPOINT = PROTOCOL + "35.163.179.218:8090";


    /**
     * NEW API
     */
    /**
     * IDENTITY
     */
    private static final String OAUTH = ENDPOINT + SERVICE_IDENTITY + VERSION + "/oauth2/token";
    private static final String TOKENINFO = ENDPOINT + SERVICE_IDENTITY + VERSION + "/oauth2/token/info";
    private static final String LOGOUT = ENDPOINT + SERVICE_IDENTITY + VERSION + "/oauth2/logout";
    private static final String USERS = ENDPOINT + SERVICE_IDENTITY + VERSION + "/users";
    private static final String USER = ENDPOINT + SERVICE_IDENTITY + VERSION + "/users/{user_id}"; // replace id
    //    private static final String ALERTS = ENDPOINT +
    private static final String INVITATIONS = ENDPOINT + SERVICE_IDENTITY + VERSION + "/accounts/{account_id}/invitations"; // replace account_id
    private static final String CAREGIVERS = ENDPOINT + SERVICE_IDENTITY + VERSION + "/accounts/{account_id}/caregivers"; // replace account_id
    private static final String CAREGIVER = ENDPOINT + SERVICE_IDENTITY + VERSION + "/accounts/{account_id}/caregivers/{caregiver_id}"; // replace account_id, caregiver_id
    private static final String CHILDREN = ENDPOINT + SERVICE_IDENTITY + VERSION + "/children";
    private static final String CHILD = ENDPOINT + SERVICE_IDENTITY + VERSION + "/children/{child_id}"; // replace child_id
    private static final String PHOTO = ENDPOINT + SERVICE_IDENTITY + VERSION + "/children/{child_id}/photos"; // replace child_id
    private static final String HANDHELDS = ENDPOINT + SERVICE_IDENTITY + VERSION + "/handhelds";
    private static final String HANDHELD = ENDPOINT + SERVICE_IDENTITY + VERSION + "/handhelds/{handheld_id}"; // replace id
    private static final String PIN = ENDPOINT + SERVICE_IDENTITY + VERSION + "/passwords/reset";
    private static final String VALIDATE_PIN = ENDPOINT + SERVICE_IDENTITY + VERSION + "/passwords/reset/validate_pin";
    private static final String RESET_PASSWORD = ENDPOINT + SERVICE_IDENTITY + VERSION + "/passwords/reset/pin";

    /**
     * TIMELINE
     */
    private static final String CHILDREN_EVENTS = ENDPOINT + SERVICE_TIMELINE + VERSION + "/children/{child_id}/events"; // ?event_type=&week_of=2017-02-20T19%3A19%3A05.234410974Z
    private static final String EVENTS = ENDPOINT + SERVICE_TIMELINE + VERSION + "/events";
    private static final String EVENT = ENDPOINT + SERVICE_TIMELINE + VERSION + "/events/{event_id}"; // replace event_id
    private static final String ARTICLES = ENDPOINT + SERVICE_TIMELINE + VERSION + "/articles"; // ?start_age_day=&end_age_day=
    private static final String ARTICLE = ENDPOINT + SERVICE_TIMELINE + VERSION + "/articles/{article_id}";

    /**
     * DEVICE
     */
    private static final String DEVICES = ENDPOINT + SERVICE_DEVICE + VERSION + "/devices";
    private static final String DEVICE = ENDPOINT + SERVICE_DEVICE + VERSION + "/devices/{serial}"; // replace serial

    /**
     * UPDATE
     */
    private static final String UPDATE = ENDPOINT + SERVICE_UPDATE + VERSION + "/versioninfo";


    /**
     * OLD API
     */
//    private static final String USERS = ENDPOINT + "/v1/users";
//    private static final String USER = ENDPOINT + "/v1/users/{id}"; // replace id
//    private static final String HANDHELDS = ENDPOINT + "/v1/handhelds";
//    private static final String HANDHELD = ENDPOINT + "/v1/handhelds/{id}"; // replace id
//    private static final String CHILDREN = ENDPOINT + "/v1/children";
//    private static final String CHILD = ENDPOINT + "/v1/children/{id}"; // replace id
//    private static final String DEVICES = ENDPOINT + "/v1/devices";
//    private static final String DEVICE = ENDPOINT + "/v1/devices?serial={serial}"; // replace serial
//    private static final String EVENTS = ENDPOINT + "/v1/events/child";
//    private static final String EVENT = ENDPOINT + "/v1/events"; // replace id // "/v1/events?event_id={id}"
//    private static final String OAUTH = ENDPOINT + "/v1/oauth2/token";
//    private static final String PASSWORD = ENDPOINT + "/v1/passwords";
//    private static final String STATUS = ENDPOINT + "/v1/status";
//    private static final String TOKENINFO = ENDPOINT + "/v1/oauth2/token/info";
//
//    private static final String MQTTTOKEN = "/v1/devices/tokens";

    /**
     * Insertions
     */
    private static final String SERIAL = "serial";
    private static final String EVENT_ID = "event_id";
    private static final String CHILD_ID = "child_id";
    private static final String WEEK_OF = "week_of";
    private static final String EVENT_TYPE = "event_type";
    private static final String START_AGE_DAY = "start_age_day";
    private static final String END_AGE_DAY = "end_age_day";

    private static final String REPLACE_USER_ID = "{user_id}";
    private static final String REPLACE_ACCOUNT_ID = "{account_id}";
    private static final String REPLACE_CAREGIVER_ID = "{caregiver_id}";
    private static final String REPLACE_SERIAL = "{serial}";
    private static final String REPLACE_HANDHELD_ID = "{handheld_id}";
    private static final String REPLACE_CHILD_ID = "{child_id}";
    private static final String REPLACE_WEEK = "{week_of}";
    private static final String REPLACE_EVENT_ID = "{event_id}";
    private static final String REPLACE_ARTICLE_ID = "{article_id}";

    /**
     * User role
     */
    public static final String TYPE_GUARDIAN = "Guardian";
    public static final String TYPE_CAREGIVER = "Caregiver";


    private SSManagement() {
    }

    static String getUserAgent() {
        return "Sproutling/" + BuildConfig.VERSION_NAME + " " + "(Linux; U; Android " + Build.VERSION.RELEASE + ")";
    }

    static Http getHttpUrl(String url) {
        return Http.url(url)
                .accept(Http.APPLICATION_JSON)
                .contentType(Http.APPLICATION_JSON)
                .header("User-Agent", getUserAgent())
                .header("X-TIMEZONE", Utils.getTimeZoneInShort())
                .header("Accept-Language", Utils.getLanguageWithCountry())
                .timeout(20000);
    }

    static Http getHttpUrlWithToken(String url, String token) {
        return getHttpUrl(url).authorization(AUTH_PREFIX + token);
    }

    static User loginByPassword(String username, String password) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrl(OAUTH)
                .post(new JSONObject()
                        .put("username", username)
                        .put("password", password)
                        .put("grant_type", "password")
                        .put("client_id", CLIENT_ID)
                        .put("scope", null));

        checkResponseForError(httpContent);
        return new User(httpContent.json());
    }

    static User refreshToken(String accessToken, String refreshToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(OAUTH, accessToken)
                .post(new JSONObject()
                        .put("refresh_token", refreshToken)
                        .put("grant_type", "refresh_token")
                        .put("client_id", CLIENT_ID)
                        .put("scope", null));

        checkResponseForError(httpContent);
        return new User(httpContent.json());
    }

    static User getTokenInfo(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(TOKENINFO, accessToken)
                .get();

        checkResponseForError(httpContent);
        return new User(httpContent.json());
    }

    static boolean logout(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(LOGOUT, accessToken)
                .post(new JSONObject());

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static UserAccountInfo createUser(String email, String firstName, String lastName, String type, String password, String phone, String inviteToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrl(USERS)
                .post(new JSONObject()
                        .put("email", email)
                        .put("first_name", firstName)
                        .put("last_name", lastName)
                        .put("role", type)
                        .put("password", password)
                        .put("password_confirmation", password)
                        .put("phone_number", phone)
                        .put("invite_token", inviteToken)
                );

        checkResponseForError(httpContent);
        return new UserAccountInfo(httpContent.json());
    }

    static UserAccountInfo getUserById(String accessToken, String userId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(USER.replace(REPLACE_USER_ID, userId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new UserAccountInfo(httpContent.json());
    }

    static UserAccountInfo updateUserById(String accessToken, String userId, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(USER.replace(REPLACE_USER_ID, userId), accessToken)
                .put(body);

        checkResponseForError(httpContent);
        return new UserAccountInfo(httpContent.json());
    }

    static boolean deleteUserById(String accessToken, String userId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(USER.replace(REPLACE_USER_ID, userId), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static String invitations(String accessToken, String accountId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(INVITATIONS.replace(REPLACE_ACCOUNT_ID, accountId), accessToken)
                .post(new JSONObject());

        checkResponseForError(httpContent);
        return checkResponseForInviteToken(httpContent);
    }

    static List<UserAccountInfo> listCaregivers(String accessToken, String accountId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CAREGIVERS.replace(REPLACE_ACCOUNT_ID, accountId), accessToken)
                .get();

        checkResponseForError(httpContent);

        JSONArray jsonArray = httpContent.jsonArray();
        List<UserAccountInfo> caregiverList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            caregiverList.add(new UserAccountInfo((JSONObject) jsonArray.get(i)));
        }
        return caregiverList;
    }

    static boolean removeCaregiver(String accessToken, String accountId, String caregiverId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CAREGIVER.replace(REPLACE_ACCOUNT_ID, accountId).replace(REPLACE_CAREGIVER_ID, caregiverId), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static Child createChild(String accessToken, String firstName, String lastName, String gender, String birthDate, String dueDate) throws JSONException, IOException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CHILDREN, accessToken)
                .post(new JSONObject()
                        .put("first_name", firstName)
                        .put("last_name", lastName)
                        .put("gender", gender)
                        .put("birth_date", birthDate)
                        .put("due_date", dueDate)
                );

        checkResponseForError(httpContent);
        return new Child(httpContent.json());
    }

    static List<Child> listChildren(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CHILDREN, accessToken)
                .get();

        checkResponseForError(httpContent);

        JSONArray jsonArray = httpContent.jsonArray();
        List<Child> childrenList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            childrenList.add(new Child((JSONObject) jsonArray.get(i)));
        }
        return childrenList;
    }

    static Child getChildById(String accessToken, String childId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CHILD.replace(REPLACE_CHILD_ID, childId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new Child(httpContent.json());
    }

    static Child updateChildById(String accessToken, String childId, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CHILD.replace(REPLACE_CHILD_ID, childId), accessToken)
                .put(body);

        checkResponseForError(httpContent);
        return new Child(httpContent.json());
    }

    static boolean deleteChildById(String accessToken, String childId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CHILD.replace(REPLACE_CHILD_ID, childId), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static void uploadChildPhoto(String accessToken, String childId, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(PHOTO.replace(REPLACE_CHILD_ID, childId), accessToken)
                .post(body);

        checkResponseForError(httpContent);
    }

    static void downloadChildPhoto(String accessToken, String childId, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(PHOTO.replace(REPLACE_CHILD_ID, childId), accessToken)
                .post(body);

        checkResponseForError(httpContent);
    }

    static boolean requestPin(String phoneNumber) throws JSONException, IOException, SSException {
        HttpContent httpContent = getHttpUrl(PIN)
                .post(new JSONObject()
                        .put("phone_number", phoneNumber)
                );

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static boolean validatePin(String phoneNumber, String pin) throws JSONException, IOException, SSException {
        HttpContent httpContent = getHttpUrl(VALIDATE_PIN)
                .post(new JSONObject()
                        .put("phone_number", phoneNumber)
                        .put("pin", pin)
                );

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static boolean resetPassword(String phoneNumber, String pin, String password, String passwordConfirmation) throws JSONException, IOException, SSException {
        HttpContent httpContent = getHttpUrl(RESET_PASSWORD)
                .put(new JSONObject()
                        .put("phone_number", phoneNumber)
                        .put("pin", pin)
                        .put("password", password)
                        .put("password_confirmation", passwordConfirmation)
                );

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static DeviceResponse createDevice(String accessToken, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(DEVICES, accessToken)
                .post(body);

        checkResponseForError(httpContent);
        return new DeviceResponse(httpContent.json());
    }

    static List<DeviceResponse> listDevices(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(DEVICES, accessToken)
                .get();

        checkResponseForError(httpContent);
        JSONArray jsonArray = httpContent.jsonArray();
        List<DeviceResponse> deviceList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            deviceList.add(new DeviceResponse((JSONObject) jsonArray.get(i)));
        }
        return deviceList;
    }

    static DeviceResponse getDeviceBySerial(String accessToken, String serial) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(DEVICE.replace(REPLACE_SERIAL, serial), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new DeviceResponse(httpContent.json());
    }

    static DeviceResponse updateDevice(String accessToken, String serial, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(DEVICE.replace(REPLACE_SERIAL, serial), accessToken)
                .put(body);

        checkResponseForError(httpContent);
        return new DeviceResponse(httpContent.json());
    }

    static boolean deleteDeviceBySerial(String accessToken, String serial) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(DEVICE.replace(REPLACE_SERIAL, serial), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static SSEvent createEvent(String accessToken, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(EVENTS, accessToken)
                .post(body);

        checkResponseForError(httpContent);
        return new SSEvent(httpContent.json());
    }

    static List<SSEvent> listEventsByChild(String accessToken, String childId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CHILDREN_EVENTS.replace(REPLACE_CHILD_ID, childId), accessToken)
                .get();

        checkResponseForError(httpContent);
        JSONArray jsonArray = httpContent.jsonArray();
        List<SSEvent> eventList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            eventList.add(new SSEvent((JSONObject) jsonArray.get(i)));
        }
        // TODO: parse JSON response
//        return TimeLineUtils.getEventListData(httpContent.json());
        return eventList;
    }

    static SSEvent getEventById(String accessToken, String eventId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(EVENT.replace(REPLACE_EVENT_ID, eventId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new SSEvent(httpContent.json());
    }

    static SSEvent updateEventById(String accessToken, String eventId, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(EVENT.replace(REPLACE_EVENT_ID, eventId), accessToken)
//                .keepParametersQuery()
                .put(body);

        checkResponseForError(httpContent);
        return new SSEvent(httpContent.json());
    }

    static List<SSEvent> getEventsByWeek(String accessToken, String childId, String eventType, String weekOf) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(CHILDREN_EVENTS.replace(REPLACE_CHILD_ID, childId), accessToken)
                .parameter(EVENT_TYPE, eventType)
                .parameter(WEEK_OF, weekOf)
                .get();

        checkResponseForError(httpContent);
        JSONArray jsonArray = httpContent.jsonArray();
        List<SSEvent> eventList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            eventList.add(new SSEvent((JSONObject) jsonArray.get(i)));
        }
        return eventList;
    }

    static List<SSArticle> listArticles(String accessToken, int startAgeDay, int endAgeDay) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(ARTICLES, accessToken)
                .parameter(START_AGE_DAY, startAgeDay)
                .parameter(END_AGE_DAY, endAgeDay)
                .get();

        checkResponseForError(httpContent);
        JSONArray jsonArray = httpContent.jsonArray();
        List<SSArticle> articles = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            articles.add(new SSArticle((JSONObject) jsonArray.get(i)));
        }
        return articles;
    }

    static SSArticle getArticleById(String accessToken, String articleId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(ARTICLE.replace(REPLACE_ARTICLE_ID, articleId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new SSArticle(httpContent.json());
    }

    static void createHandheld(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(HANDHELDS, accessToken)
                .post(new JSONObject());

        checkResponseForError(httpContent);
    }

    static void listHandheld(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(HANDHELDS, accessToken)
                .get();

        checkResponseForError(httpContent);
    }

    static void getHandheldById(String accessToken, String handheldId) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(HANDHELD.replace(REPLACE_HANDHELD_ID, handheldId), accessToken)
                .get();

        checkResponseForError(httpContent);
    }

    static void updateHandheldById(String accessToken, String handheldId, JSONObject body) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(HANDHELD.replace(REPLACE_HANDHELD_ID, handheldId), accessToken)
                .put(body);

        checkResponseForError(httpContent);
    }

    static void deleteHandheldById(String accessToken, String handheldId) throws IOException, JSONException, SSException {
        HttpContent httpContent= getHttpUrlWithToken(HANDHELD.replace(REPLACE_HANDHELD_ID, handheldId), accessToken)
                .delete();

        checkResponseForError(httpContent);
    }

    static UpdateInfo getUpdateInfo() throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrl(UPDATE)
                .get();

        checkResponseForError(httpContent);
        return new UpdateInfo(httpContent.json());
    }

    static void checkResponseForError(HttpContent httpContent) throws JSONException, SSException {
        int responseCode = httpContent.responseCode();
        if (httpContent.string().contains("errors")) {
            JSONArray errors = httpContent.json().getJSONArray("errors");
            if (errors.length() > 0)
                throw new SSException(new SSError(errors.getJSONObject(0)), responseCode);
            else
                throw new SSException(new SSError(), responseCode);
        } else if (httpContent.string().contains("error")) {
            throw new SSException(new SSError(httpContent.json()), responseCode);
        }
    }

    static boolean checkResponseForStatus(HttpContent httpContent) throws JSONException, SSException {
        if (SSStatus.hasStatus(httpContent.string())) {
            SSStatus status = new SSStatus(httpContent.json());
            return status.isSuccess();
        }
        return false;
    }

    static String checkResponseForInviteToken(HttpContent httpContent) throws JSONException, SSException {
        if (SSStatus.hasStatus(httpContent.string())) {
            SSStatus status = new SSStatus(httpContent.json());
            return status.isSuccess() ? httpContent.json().getString("token") : null;
        }
        return null;
    }

    public static class DeviceResponse {
        private String id;
        private String ownerId;
        private String accountId;
        private String name;
        private String serial;
        private String firmwareVersion;
        private String createdAt;
        private String updatedAt;
        private String ownerType;

        public DeviceResponse() {
        }

        public DeviceResponse(JSONObject jsonObject) throws JSONException {
            id = jsonObject.getString("id");
            accountId = jsonObject.getString("account_id");
            ownerId = jsonObject.getString("owner_id");
            ownerType = jsonObject.getString("owner_type");
            name = jsonObject.getString("name");
            serial = jsonObject.getString("serial");
            firmwareVersion = jsonObject.getString("firmware_version");
            createdAt = jsonObject.getString("created_at");
            updatedAt = jsonObject.getString("updated_at");
        }

        public String getOwnerId() {
            return ownerId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public String getOwnerType() {
            return ownerType;
        }

        public String getId() {
            return id;
        }

        public String getAccountId() {
            return accountId;
        }

        public String getName() {
            return name;
        }

        public String getSerial() {
            return serial;
        }

        public String getFirmwareVersion() {
            return firmwareVersion;
        }

        public String getCreate_at() {
            return createdAt;
        }

        public String getUpdated_at() {
            return updatedAt;
        }

        public void setId(String id) {
            this.id = id;
        }

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public void setName(String name) {
            this.name = name;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public void setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public void setOwnerType(String ownerType) {
            this.ownerType = ownerType;
        }
    }

    // Processing

    // Processed Objects
    public static class UserAccountInfo {
        public String id;
        public String accountId;
        public String firstName;
        public String lastName;
        public String email;
        public String phone;
        public String type;
        public String createdAt;
        public String updatedAt;
        public String alert;
        public String jsonString;

        public UserAccountInfo() {
        }

        public UserAccountInfo(JSONObject jsonObject) throws JSONException {
            id = jsonObject.getString("id");
            accountId = jsonObject.getString("account_id");
            firstName = jsonObject.getString("first_name");
            lastName = jsonObject.getString("last_name");
            email = jsonObject.getString("email");
            phone = jsonObject.getString("phone_number");
            type = jsonObject.getString("role");
            createdAt = jsonObject.getString("created_at");
            updatedAt = jsonObject.getString("updated_at");
            alert = jsonObject.getString("push_notification_alert_settings");
            jsonString = jsonObject.toString();
        }

        public String toJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("account_id", accountId);
            jsonObject.put("first_name", firstName);
            jsonObject.put("last_name", lastName);
            jsonObject.put("email", email);
            jsonObject.put("phone_number", phone);
            jsonObject.put("role", type);
            jsonObject.put("created_at", createdAt);
            jsonObject.put("updated_at", updatedAt);
            jsonObject.put("push_notification_alert_settings", alert);
            return jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class User {
        public String scope;
        public String resourceOwnerId;
        public String createdAt;
        public String tokenType;
        public int expiresIn;
        public String refreshToken;
        public String accessToken;
        public String jsonString;

        public User() {
        }

        public User(JSONObject jsonObject) throws JSONException {
            scope = jsonObject.optString("scope");
            resourceOwnerId = jsonObject.getString("resource_owner_id");
            createdAt = jsonObject.getString("created_at");
            tokenType = jsonObject.getString("token_type");
            expiresIn = jsonObject.getInt("expires_in");
            refreshToken = jsonObject.optString("refresh_token");
            accessToken = jsonObject.getString("access_token");
            jsonString = jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class Child {
        public String id;
        public String accountId;
        public String firstName;
        public String lastName;
        public String gender;
        public String birthDate;
        public String dueDate;
        public String createdAt;
        public String updatedAt;
        public String twinId;
        public String photoUrl;
        public String jsonString;

        public Child() {
        }

        public Child(JSONObject jsonObject) throws JSONException {
            id = jsonObject.optString("id");
            accountId = jsonObject.getString("account_id");
            firstName = jsonObject.getString("first_name");
            lastName = jsonObject.getString("last_name");
            gender = jsonObject.getString("gender");
            birthDate = jsonObject.optString("birth_date");
            dueDate = jsonObject.getString("due_date");
            createdAt = jsonObject.getString("created_at");
            updatedAt = jsonObject.getString("updated_at");
            twinId = jsonObject.optString("twin_id");
            photoUrl = jsonObject.getString("photo_url");
            jsonString = jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class SSEvent {
        public String id;
        public String childId;
        public String eventType;
        public String startDate;
        public String endDate;
        public String createdAt;
        public String updatedAt;
        public String data;
        public String jsonString;

        public SSEvent() {}

        public SSEvent(JSONObject jsonObject) throws JSONException {
            id = jsonObject.getString("id");
            childId = jsonObject.getString("child_id");
            eventType = jsonObject.getString("event_type");
            startDate = jsonObject.getString("start_date");
            endDate = jsonObject.getString("end_date");
            createdAt = jsonObject.getString("created_at");
            updatedAt = jsonObject.getString("updated_at");
            data = jsonObject.getString("data");
            jsonString = jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class SSArticle {
        public String id;
        public String language;
        public int childAge;
        public String title;
        public String url;
        public String imageThumbnailUrl;
        public String imageSmallUrl;
        public String imageMediumUrl;
        public String imageLargeUrl;
        public String createdAt;
        public String jsonString;

        public SSArticle() {}

        public SSArticle(JSONObject jsonObject) throws JSONException {
            id = jsonObject.getString("ID");
            language = jsonObject.getString("language");
            childAge = jsonObject.getInt("child_age");
            title = jsonObject.getString("title");
            url = jsonObject.getString("url");
            imageThumbnailUrl = jsonObject.getString("image_thumbnail");
            imageSmallUrl = jsonObject.getString("image_small");
            imageMediumUrl = jsonObject.getString("image_medium");
            imageLargeUrl = jsonObject.getString("image_large");
            createdAt = jsonObject.getString("created_at");
            jsonString = jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class UpdateInfo {
        public ApiInfo apiInfo;
        public IosInfo iosInfo;
        public AndroidInfo androidInfo;
        public String jsonString;

        public UpdateInfo() {
        }

        public UpdateInfo(JSONObject jsonObject) throws JSONException {
            apiInfo = new ApiInfo(jsonObject.getJSONObject("api"));
            iosInfo = new IosInfo(jsonObject.getJSONObject("ios"));
            androidInfo = new AndroidInfo(jsonObject.getJSONObject("android"));
            jsonString = jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }

        class Info {
            public String currentVersion;
            public String minRequiredVersion;

            public Info(JSONObject jsonObject) throws JSONException {
                currentVersion = jsonObject.getString("current_version");
                minRequiredVersion = jsonObject.getString("min_required_version");
            }
        }

        class ApiInfo extends Info {
            public ApiInfo(JSONObject jsonObject) throws JSONException {
                super(jsonObject);
            }
        }

        class IosInfo extends Info {
            public String updateUrl;

            public IosInfo(JSONObject jsonObject) throws JSONException {
                super(jsonObject);
                updateUrl = jsonObject.getString("update_url");
            }
        }

        class AndroidInfo extends Info {
            public String updateUrl;

            public AndroidInfo(JSONObject jsonObject) throws JSONException {
                super(jsonObject);
                updateUrl = jsonObject.getString("update_url");
            }
        }
    }
}


