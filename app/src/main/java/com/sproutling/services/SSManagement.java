/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.services;

import android.os.Build;
import android.util.Log;

import com.sproutling.BuildConfig;
import com.sproutling.http.Http;
import com.sproutling.http.HttpContent;
import com.sproutling.utils.DateTimeUtils;
import com.sproutling.utils.Utils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.Serializable;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

/**
 * Sproutservices
 * Created by bradylin on 11/18/16.
 */

public class SSManagement {

    /**
     * User role
     */
    public static final String TYPE_GUARDIAN = "Guardian";
    public static final String TYPE_CAREGIVER = "Caregiver";
    private static final String TAG = "SSManagement";
    // Temporary iOS client id
//    private static final String CLIENT_ID = "c3c04c54bd0a9f3f40517f1670c3c327c17d8d19425b416bc7169593e2563380"; //iOS
    private static final String CLIENT_ID = "4677a7fc91373cbbb035ad4f43c1ebaddfaa8f95344fd37c9efe3ea7afaca08e";
    private static final String AUTH_PREFIX = "Bearer ";
    private static final String PROTOCOL_SECURE = "https://";
    private static final String PROTOCOL = "http://";
//    private static final String ENDPOINT = PROTOCOL + "api-dev-"+BuildConfig.RELEASE_TARGET+".sproutlingcloud.com";
private static final String VERSION = "/v1";
    private static final String SERVICE_IDENTITY = "identity";
    private static final String SERVICE_DEVICE = "device";
    private static final String SERVICE_TIMELINE = "timeline";

    // OLD Endpoint
//    private static final String ENDPOINT = PROTOCOL + "35.163.179.218:8090";


    // NEW API
    private static final String SERVICE_UPDATE = "update";
    /**
     * IDENTITY
     */
    private static final String OAUTH = SERVICE_IDENTITY + VERSION + "/oauth2/token";
    private static final String TOKENINFO = SERVICE_IDENTITY + VERSION + "/oauth2/token/info";
    private static final String LOGOUT = SERVICE_IDENTITY + VERSION + "/oauth2/logout";
    private static final String USERS = SERVICE_IDENTITY + VERSION + "/users";
    private static final String USER = SERVICE_IDENTITY + VERSION + "/users/{user_id}"; // replace id
    private static final String ALERTS = USER + "/alerts"; // replace user id
    private static final String INVITATIONS = SERVICE_IDENTITY + VERSION + "/accounts/{account_id}/invitations"; // replace account_id
    private static final String CAREGIVERS = SERVICE_IDENTITY + VERSION + "/accounts/{account_id}/caregivers"; // replace account_id
    private static final String CAREGIVER = SERVICE_IDENTITY + VERSION + "/accounts/{account_id}/caregivers/{caregiver_id}"; // replace account_id, caregiver_id
    private static final String CHILDREN = SERVICE_IDENTITY + VERSION + "/children";
    private static final String CHILD = SERVICE_IDENTITY + VERSION + "/children/{child_id}"; // replace child_id
    private static final String PHOTO = SERVICE_IDENTITY + VERSION + "/children/{child_id}/photos"; // replace child_id
    private static final String HANDHELDS = SERVICE_IDENTITY + VERSION + "/handhelds";
    private static final String HANDHELD = SERVICE_IDENTITY + VERSION + "/handhelds/{handheld_id}"; // replace id
    private static final String PIN = SERVICE_IDENTITY + VERSION + "/passwords/reset";
    private static final String VALIDATE_PIN = SERVICE_IDENTITY + VERSION + "/passwords/reset/validate_pin";
    private static final String RESET_PASSWORD = SERVICE_IDENTITY + VERSION + "/passwords/reset/pin";
    /**
     * TIMELINE
     */
    private static final String CHILDREN_EVENTS = SERVICE_TIMELINE + VERSION + "/children/{child_id}/events"; // ?event_type=&week_of=2017-02-20T19%3A19%3A05.234410974Z
    private static final String EVENTS = SERVICE_TIMELINE + VERSION + "/events";
    private static final String EVENT = SERVICE_TIMELINE + VERSION + "/events/{event_id}"; // replace event_id
    private static final String ARTICLES = SERVICE_TIMELINE + VERSION + "/articles"; // ?start_age_day=&end_age_day=
    private static final String ARTICLE = SERVICE_TIMELINE + VERSION + "/articles/{article_id}";
    /**
     * DEVICE
     */
    private static final String DEVICES = SERVICE_DEVICE + VERSION + "/devices";
    private static final String DEVICE = SERVICE_DEVICE + VERSION + "/devices/{serial}"; // replace serial
    /**
     * UPDATE
     */
    private static final String UPDATE = SERVICE_UPDATE + VERSION + "/versioninfo";
    /**
     * EOL
     */
    private static final String EOL = SERVICE_UPDATE + VERSION + "/eol";
    /**
     * Insertions
     */
    private static final String SERIAL = "serial";


    // OLD API
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
     * New Endpoint
     */
//    private static final String ENDPOINT = PROTOCOL_SECURE + "api-dev.us.sproutlingcloud.com";
//    private static final String ENDPOINT = PROTOCOL + "api-dev-us.sproutlingcloud.com";
//    private static final String ENDPOINT = PROTOCOL + "api-dev-cn.sproutlingcloud.com";
    public static String ENDPOINT = BuildConfig.SERVER_URL;

    private SSManagement() {
    }

    public static String getChildrenEventsUrl(String childId) {
        return CHILDREN_EVENTS.replace(REPLACE_CHILD_ID, childId);
    }

    public static String getTimelineEventsUrl() {
        return EVENTS;
    }

    private static String getUserAgent() {
        return "Sproutling/" + BuildConfig.VERSION_NAME + " " + "(Linux; U; Android " + Build.VERSION.RELEASE + ")";
    }

    private static Http getHttpUrl(String url) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "HTTP Request: " + url);
        }

        return Http.url(url)
                .accept(Http.APPLICATION_JSON)
                .contentType(Http.APPLICATION_JSON)
                .header("User-Agent", getUserAgent())
                .header("X-TIMEZONE", Utils.getTimeZoneInShort())
                .header("Accept-Language", Utils.getLanguageWithCountry())
                .timeout(20000);
    }

    private static Http getHttpUrlWithToken(String url, String token) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "HTTP Request: " + url);
            Log.d(TAG, "Access Token: " + token);
        }
        return getHttpUrl(url).authorization(AUTH_PREFIX + token);
    }

    static User loginByPassword(String username, String password) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrl(ENDPOINT + OAUTH)
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
        HttpContent httpContent = getHttpUrlWithToken(ENDPOINT + OAUTH, accessToken)
                .post(new JSONObject()
                        .put("refresh_token", refreshToken)
                        .put("grant_type", "refresh_token")
                        .put("client_id", CLIENT_ID)
                        .put("scope", null));

        checkResponseForError(httpContent);
        return new User(httpContent.json());
    }

    static User getTokenInfo(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(ENDPOINT + TOKENINFO, accessToken)
                .get();

        checkResponseForError(httpContent);
        return new User(httpContent.json());
    }

    static boolean logout(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(ENDPOINT + LOGOUT, accessToken)
                .post(new JSONObject());

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static UserAccountInfo createUser(String email, String firstName, String lastName, String type, String password, String phone, String inviteToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrl(ENDPOINT + USERS)
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
        String userUrl = ENDPOINT + USER;
        HttpContent httpContent = getHttpUrlWithToken(userUrl.replace(REPLACE_USER_ID, userId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new UserAccountInfo(httpContent.json());
    }

    static UserAccountInfo updateUserById(String accessToken, String userId, JSONObject body) throws IOException, JSONException, SSException {
        String userUrl = ENDPOINT + USER;
        HttpContent httpContent = getHttpUrlWithToken(userUrl.replace(REPLACE_USER_ID, userId), accessToken)
                .put(body);

        checkResponseForError(httpContent);
        return new UserAccountInfo(httpContent.json());
    }

    static boolean deleteUserById(String accessToken, String userId) throws IOException, JSONException, SSException {
        String userUrl = ENDPOINT + USER;
        HttpContent httpContent = getHttpUrlWithToken(userUrl.replace(REPLACE_USER_ID, userId), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static UserAccountInfo updateAlert(String accessToken, String userId, JSONObject body) throws IOException, JSONException, SSException {
        String alertUrl = ENDPOINT + ALERTS;

        HttpContent httpContent = getHttpUrlWithToken(alertUrl.replace(REPLACE_USER_ID, userId), accessToken)
                .put(body);

        checkResponseForError(httpContent);
        return new UserAccountInfo(httpContent.json());
    }

    static String invitations(String accessToken, String accountId) throws IOException, JSONException, SSException {
        String invitationUrl = ENDPOINT + INVITATIONS;
        HttpContent httpContent = getHttpUrlWithToken(invitationUrl.replace(REPLACE_ACCOUNT_ID, accountId), accessToken)
                .post(new JSONObject());

        checkResponseForError(httpContent);
        return checkResponseForInviteToken(httpContent);
    }

    static List<UserAccountInfo> listCaregivers(String accessToken, String accountId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + CAREGIVERS;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_ACCOUNT_ID, accountId), accessToken)
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
        String url = ENDPOINT + CAREGIVER;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_ACCOUNT_ID, accountId).replace(REPLACE_CAREGIVER_ID, caregiverId), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static Child createChild(String accessToken, String firstName, String lastName, String gender, String birthDate, String dueDate) throws JSONException, IOException, SSException {
        String url = ENDPOINT + CHILDREN;
        HttpContent httpContent = getHttpUrlWithToken(url, accessToken)
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
        String url = ENDPOINT + CHILDREN;
        HttpContent httpContent = getHttpUrlWithToken(url, accessToken).get();

        checkResponseForError(httpContent);

        JSONArray jsonArray = httpContent.jsonArray();
        List<Child> childrenList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            childrenList.add(new Child((JSONObject) jsonArray.get(i)));
        }
        return childrenList;
    }

    static Child getChildById(String accessToken, String childId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + CHILD;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new Child(httpContent.json());
    }

    static Child updateChildById(String accessToken, String childId, JSONObject body) throws IOException, JSONException, SSException {
        String url = ENDPOINT + CHILD;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .put(body);
        Log.d(TAG, "updateChildById BODY : " + body.toString(2));
        checkResponseForError(httpContent);
        return new Child(httpContent.json());
    }

    static boolean deleteChildById(String accessToken, String childId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + CHILD;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static void uploadChildPhoto(String accessToken, String childId, JSONObject body) throws IOException, JSONException, SSException {
        String url = ENDPOINT + PHOTO;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .post(body);

        checkResponseForError(httpContent);
    }

    static void downloadChildPhoto(String accessToken, String childId, JSONObject body) throws IOException, JSONException, SSException {
        String url = ENDPOINT + PHOTO;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .post(body);

        checkResponseForError(httpContent);
    }

    static boolean requestPin(String phoneNumber) throws JSONException, IOException, SSException {
        HttpContent httpContent = getHttpUrl(ENDPOINT + PIN)
                .post(new JSONObject()
                        .put("phone_number", phoneNumber)
                );

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static boolean validatePin(String phoneNumber, String pin) throws JSONException, IOException, SSException {
        HttpContent httpContent = getHttpUrl(ENDPOINT + VALIDATE_PIN)
                .post(new JSONObject()
                        .put("phone_number", phoneNumber)
                        .put("pin", pin)
                );

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static boolean resetPassword(String phoneNumber, String pin, String password, String passwordConfirmation) throws JSONException, IOException, SSException {
        HttpContent httpContent = getHttpUrl(ENDPOINT + RESET_PASSWORD)
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
        HttpContent httpContent = getHttpUrlWithToken(ENDPOINT + DEVICES, accessToken)
                .post(body);

        checkResponseForError(httpContent);
        return new DeviceResponse(httpContent.json());
    }

    static List<DeviceResponse> listDevices(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(ENDPOINT + DEVICES, accessToken)
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
        String url = ENDPOINT + DEVICE;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_SERIAL, serial), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new DeviceResponse(httpContent.json());
    }

    static DeviceResponse updateDevice(String accessToken, String serial, JSONObject body) throws IOException, JSONException, SSException {
        String url = ENDPOINT + DEVICE;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_SERIAL, serial), accessToken)
                .put(body);

        checkResponseForError(httpContent);
        return new DeviceResponse(httpContent.json());
    }

    static boolean deleteDeviceBySerial(String accessToken, String serial) throws IOException, JSONException, SSException {
        String url = ENDPOINT + DEVICE;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_SERIAL, serial), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return checkResponseForStatus(httpContent);
    }

    static SSEvent createEvent(String accessToken, JSONObject body) throws IOException, JSONException, SSException {
        String url = ENDPOINT + EVENTS;
        HttpContent httpContent = getHttpUrlWithToken(url, accessToken)
                .post(body);

        checkResponseForError(httpContent);
        return new SSEvent(httpContent.json());
    }

    static List<SSEvent> listEventsByChild(String accessToken, String childId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + CHILDREN_EVENTS;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .get();

        checkResponseForError(httpContent);
        JSONArray jsonArray = httpContent.jsonArray();
        List<SSEvent> eventList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            eventList.add(SSEvent.parseEvent((JSONObject) jsonArray.get(i)));
//            eventList.add(new SSEvent((JSONObject) jsonArray.get(i)));
        }
        return eventList;
    }

    static List<SSEvent> listEventsByChild(String accessToken, String childId, String eventType) throws IOException, JSONException, SSException {
        String url = ENDPOINT + CHILDREN_EVENTS;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .parameter("event_type", eventType)
                .get();
        checkResponseForError(httpContent);
        JSONArray jsonArray = httpContent.jsonArray();
        List<SSEvent> eventList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            eventList.add(SSEvent.parseEvent((JSONObject) jsonArray.get(i)));
//            eventList.add(new SSEvent((JSONObject) jsonArray.get(i)));
        }
        return eventList;
    }

    static SSEvent getEventById(String accessToken, String eventId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + EVENT;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_EVENT_ID, eventId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return SSEvent.parseEvent(httpContent.json());
//        return new SSEvent(httpContent.json());
    }

    static SSEvent updateEventById(String accessToken, String eventId, JSONObject body) throws IOException, JSONException, SSException {
        String url = ENDPOINT + EVENT;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_EVENT_ID, eventId), accessToken)
//                .keepParametersQuery()
                .put(body);

        checkResponseForError(httpContent);
        return SSEvent.parseEvent(httpContent.json());
//        return new SSEvent(httpContent.json());
    }

    static List<SSEvent> getEventsByWeek(String accessToken, String childId, String eventType, String weekOf) throws IOException, JSONException, SSException {
        String url = ENDPOINT + CHILDREN_EVENTS;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_CHILD_ID, childId), accessToken)
                .parameter(EVENT_TYPE, eventType)
                .parameter(WEEK_OF, weekOf)
                .get();

        checkResponseForError(httpContent);
        JSONArray jsonArray = httpContent.jsonArray();
        List<SSEvent> eventList = new ArrayList<>();
        for (int i = 0; i < jsonArray.length(); i++) {
            eventList.add(SSEvent.parseEvent((JSONObject) jsonArray.get(i)));
//            eventList.add(new SSEvent((JSONObject) jsonArray.get(i)));
        }
        return eventList;
    }

    static boolean deleteEventById(String accessToken, String eventId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + EVENT;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_EVENT_ID, eventId), accessToken)
                .delete();

        checkResponseForError(httpContent);
        return true;
    }

    static List<SSArticle> listArticles(String accessToken, int startAgeDay, int endAgeDay) throws IOException, JSONException, SSException {
        String url = ENDPOINT + ARTICLES;
        HttpContent httpContent = getHttpUrlWithToken(url, accessToken)
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
        String url = ENDPOINT + ARTICLE;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_ARTICLE_ID, articleId), accessToken)
                .get();

        checkResponseForError(httpContent);
        return new SSArticle(httpContent.json());
    }

    static void createHandheld(String accessToken) throws IOException, JSONException, SSException {
        String url = ENDPOINT + HANDHELDS;
        HttpContent httpContent = getHttpUrlWithToken(url, accessToken)
                .post(new JSONObject());

        // Required fields

//        \"uuid\": \"cbb58a2a-8f05-4221-bc5a-315ef7579f6f\",
//        \"token\": \"eb76e3fe19c2760c6f8d590047d9188fc860c2401a38018f5b6d1294b07013f8\",
//        \"name\": \"Lisa\'s iPhone 6\",
//        \"locale\": \"en_US\",
//        \"language\": \"en-US\",
//        \"timezone\": \"PST\",

        checkResponseForError(httpContent);
    }

    static void listHandheld(String accessToken) throws IOException, JSONException, SSException {
        HttpContent httpContent = getHttpUrlWithToken(ENDPOINT + HANDHELDS, accessToken)
                .get();

        checkResponseForError(httpContent);
    }

    static void getHandheldById(String accessToken, String handheldId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + HANDHELD;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_HANDHELD_ID, handheldId), accessToken)
                .get();

        checkResponseForError(httpContent);
    }

    static void updateHandheldById(String accessToken, String handheldId, JSONObject body) throws IOException, JSONException, SSException {
        String url = ENDPOINT + HANDHELD;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_HANDHELD_ID, handheldId), accessToken)
                .put(body);

        checkResponseForError(httpContent);
    }

    static void deleteHandheldById(String accessToken, String handheldId) throws IOException, JSONException, SSException {
        String url = ENDPOINT + HANDHELD;
        HttpContent httpContent = getHttpUrlWithToken(url.replace(REPLACE_HANDHELD_ID, handheldId), accessToken)
                .delete();

        checkResponseForError(httpContent);
    }

    static UpdateInfo getUpdateInfo() throws IOException, JSONException, SSException {

        HttpContent httpContent = getHttpUrl(ENDPOINT + UPDATE)
                .get();

        checkResponseForError(httpContent);
        return new UpdateInfo(httpContent.json());
    }

    static EOLData getEOLData() throws IOException, JSONException, SSException {
        //TODO remove this once we have the final API for Sproutling EOL :(
        HttpContent httpContent = getHttpUrl("https://s3-us-west-1.amazonaws.com/sproutling-app/eol.json")
                .get();

        checkResponseForError(httpContent);
        return new EOLData(httpContent.json());
    }

    private static void checkResponseForError(HttpContent httpContent) throws JSONException, SSException {
        if (BuildConfig.DEBUG) {
            Log.e(TAG, "HTTP Response(" + httpContent.responseCode() + "): " + httpContent.string());
        }
        int responseCode = httpContent.responseCode();
        if (responseCode >= 400 && responseCode <= 404) {
            String content = httpContent.string();
            if (content != null && content.contains("error"))
                throw new SSException(new SSError(httpContent.json()), responseCode);
            throw new SSRequestException(new SSError(), responseCode);
        } else if (responseCode >= 500 && responseCode <= 503) {
            throw new SSServerException(new SSError(), responseCode);
        } else if (httpContent.string() != null && httpContent.string().contains("error")) {
            throw new SSException(new SSError(httpContent.json()), responseCode);
        }
    }

    private static boolean checkResponseForStatus(HttpContent httpContent) throws JSONException, SSException {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "HTTP Response(" + httpContent.responseCode() + "): " + httpContent.string());
        }
        if (SSStatus.hasStatus(httpContent.string())) {
            SSStatus status = new SSStatus(httpContent.json());
            return status.isSuccess();
        }
        return false;
    }

    private static String checkResponseForInviteToken(HttpContent httpContent) throws JSONException, SSException {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "HTTP Response(" + httpContent.responseCode() + "): " + httpContent.string());
        }
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

        public void setOwnerId(String ownerId) {
            this.ownerId = ownerId;
        }

        public String getCreatedAt() {
            return createdAt;
        }

        public void setCreatedAt(String createdAt) {
            this.createdAt = createdAt;
        }

        public String getUpdatedAt() {
            return updatedAt;
        }

        public void setUpdatedAt(String updatedAt) {
            this.updatedAt = updatedAt;
        }

        public String getOwnerType() {
            return ownerType;
        }

        public void setOwnerType(String ownerType) {
            this.ownerType = ownerType;
        }

        public String getId() {
            return id;
        }

        public void setId(String id) {
            this.id = id;
        }

        public String getAccountId() {
            return accountId;
        }

        public void setAccountId(String accountId) {
            this.accountId = accountId;
        }

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public String getSerial() {
            return serial;
        }

        public void setSerial(String serial) {
            this.serial = serial;
        }

        public String getFirmwareVersion() {
            return firmwareVersion;
        }

        public void setFirmwareVersion(String firmwareVersion) {
            this.firmwareVersion = firmwareVersion;
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
        public String inviteToken;
        public String type;
        public String createdAt;
        public String updatedAt;
        public PushNotification alert;
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
            inviteToken = jsonObject.optString("invite_token");
            type = jsonObject.getString("role");
            createdAt = jsonObject.getString("created_at");
            updatedAt = jsonObject.getString("updated_at");
            JSONObject pnSettingsJSONObject = jsonObject.optJSONObject("push_notification_alert_settings");
            alert = pnSettingsJSONObject != null ? new PushNotification(pnSettingsJSONObject) : null;
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
            jsonObject.put("invite_token", inviteToken);
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

    public static class PushNotification {
        public static final String ENABLED = "0";
        public static final String DISABLED = "1";

        public static final String DEFAULT_NOTIFICATIONS_DISABLED = "0";
        public static final String DEFAULT_ROLLEDOVER_DISABLED = "0";
        public static final String DEFAULT_UNUSUAL_HEARTBEAT_DISABLED = "0";
        public static final String DEFAULT_AWAKE_DISABLED = "0";
        public static final String DEFAULT_ASLEEP_DISABLED = "1";
        //        public static final String DEFAULT_STIRRING_DISABLED = "1";
        public static final String DEFAULT_WEARABLE_FELL_OFF_DISABLED = "0";
        public static final String DEFAULT_WEARABLE_NOT_FOUND_DISABLED = "1";
        public static final String DEFAULT_WEARABLE_TOO_FAR_AWAY_DISABLED = "1";
        public static final String DEFAULT_BATTERY_DISABLED = "1";
        public static final String DEFAULT_OUT_OF_BATTERY_DISABLED = "1";
        public static final String DEFAULT_HUB_OFFLINE_DISABLED = "1";

        public String notificationsDisabled;
        public String rolledOverDisabled;
        public String unusualHeartbeatDisabled;
        public String awakeDisabled;
        public String asleepDisabled;
        //        public String stirringDisabled;
        public String wearableFellOffDisabled;
        public String wearableNotFoundDisabled;
        public String wearableTooFarAwayDisabled;
        public String batteryDisabled;
        public String outOfBatteryDisabled;
        public String hubOfflineDisabled;
        public String roomTemperatureDisabled;
        public String roomHumidityDisabled;
        public String roomNoiseLevelDisabled;
        public String roomLightLevelDisabled;
        public String jsonString;

        public PushNotification() {
        }

        public PushNotification(JSONObject jsonObject) throws JSONException {
            notificationsDisabled = jsonObject.optString("NotificationsDisabled", "0");
            rolledOverDisabled = jsonObject.optString("RolledoverDisabled", "0");
            unusualHeartbeatDisabled = jsonObject.optString("UnusualHeartbeatDisabled", "0");
            awakeDisabled = jsonObject.optString("AwakeDisabled", "0");
            asleepDisabled = jsonObject.optString("AsleepDisabled", "0");
//            stirringDisabled = jsonObject.optString("StirringDisabled", "0");
            wearableFellOffDisabled = jsonObject.optString("WearableFellOffDisabled", "0");
            wearableNotFoundDisabled = jsonObject.optString("WearableNotFoundDisabled", "0");
            wearableTooFarAwayDisabled = jsonObject.optString("WearableTooFarAwayDisabled", "0");
            batteryDisabled = jsonObject.optString("BatteryDisabled", "0");
            outOfBatteryDisabled = jsonObject.optString("OutOfBatteryDisabled", "0");
            hubOfflineDisabled = jsonObject.optString("HubOfflineDisabled", "0");
            roomTemperatureDisabled = jsonObject.optString("RoomTemperatureDisabled", "0");
            roomHumidityDisabled = jsonObject.optString("RoomHumidityDisabled", "0");
            roomLightLevelDisabled = jsonObject.optString("RoomLightLevelDisabled", "0");
            roomNoiseLevelDisabled = jsonObject.optString("RoomNoiseLevelDisabled", "0");
            jsonString = jsonObject.toString();
        }

        public static PushNotification getDefaultSettings() {
            PushNotification settings = new PushNotification();
            settings.notificationsDisabled = DEFAULT_NOTIFICATIONS_DISABLED;
            settings.rolledOverDisabled = DEFAULT_ROLLEDOVER_DISABLED;
            settings.unusualHeartbeatDisabled = DEFAULT_UNUSUAL_HEARTBEAT_DISABLED;
            settings.awakeDisabled = DEFAULT_AWAKE_DISABLED;
            settings.asleepDisabled = DEFAULT_ASLEEP_DISABLED;
//            settings.stirringDisabled = DEFAULT_STIRRING_DISABLED;
            settings.wearableFellOffDisabled = DEFAULT_WEARABLE_FELL_OFF_DISABLED;
            settings.wearableNotFoundDisabled = DEFAULT_WEARABLE_NOT_FOUND_DISABLED;
            settings.wearableTooFarAwayDisabled = DEFAULT_WEARABLE_TOO_FAR_AWAY_DISABLED;
            settings.batteryDisabled = DEFAULT_BATTERY_DISABLED;
            settings.outOfBatteryDisabled = DEFAULT_OUT_OF_BATTERY_DISABLED;
            settings.hubOfflineDisabled = DEFAULT_HUB_OFFLINE_DISABLED;
            return settings;
        }

        public static JSONObject getDefaultSettingsJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("NotificationsDisabled", DEFAULT_NOTIFICATIONS_DISABLED);
            jsonObject.put("RolledoverDisabled", DEFAULT_ROLLEDOVER_DISABLED);
            jsonObject.put("UnusualHeartbeatDisabled", DEFAULT_UNUSUAL_HEARTBEAT_DISABLED);
            jsonObject.put("AwakeDisabled", DEFAULT_AWAKE_DISABLED);
            jsonObject.put("AsleepDisabled", DEFAULT_ASLEEP_DISABLED);
//            jsonObject.put("StirringDisabled", DEFAULT_STIRRING_DISABLED);
            jsonObject.put("WearableFellOffDisabled", DEFAULT_WEARABLE_FELL_OFF_DISABLED);
            jsonObject.put("WearableNotFoundDisabled", DEFAULT_WEARABLE_NOT_FOUND_DISABLED);
            jsonObject.put("WearableTooFarAwayDisabled", DEFAULT_WEARABLE_TOO_FAR_AWAY_DISABLED);
            jsonObject.put("BatteryDisabled", DEFAULT_BATTERY_DISABLED);
            jsonObject.put("OutOfBatteryDisabled", DEFAULT_OUT_OF_BATTERY_DISABLED);
            jsonObject.put("HubOfflineDisabled", DEFAULT_HUB_OFFLINE_DISABLED);
            return jsonObject;
        }

        public static JSONObject getDefaultSettingsAPIJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("push_notification_alert_settings", getDefaultSettingsJSON());
            return jsonObject;
        }

        public JSONObject getSettingsJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("NotificationsDisabled", notificationsDisabled);
            jsonObject.put("RolledoverDisabled", rolledOverDisabled);
            jsonObject.put("UnusualHeartbeatDisabled", unusualHeartbeatDisabled);
            jsonObject.put("AwakeDisabled", awakeDisabled);
            jsonObject.put("AsleepDisabled", asleepDisabled);
//            jsonObject.put("StirringDisabled", stirringDisabled);
            jsonObject.put("WearableFellOffDisabled", wearableFellOffDisabled);
            jsonObject.put("WearableNotFoundDisabled", wearableNotFoundDisabled);
            jsonObject.put("WearableTooFarAwayDisabled", wearableTooFarAwayDisabled);
            jsonObject.put("BatteryDisabled", batteryDisabled);
            jsonObject.put("OutOfBatteryDisabled", outOfBatteryDisabled);
            jsonObject.put("HubOfflineDisabled", hubOfflineDisabled);
            return jsonObject;
        }

        public JSONObject getSettingsAPIJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("push_notification_alert_settings", getSettingsJSON());
            return jsonObject;
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
        public static final String GENDER_GIRL = "F";
        public static final String GENDER_BOY = "M";
        public static final String GENDER_UNKNOWN = "U";

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

        public JSONObject toJSON() throws JSONException {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("id", id);
            jsonObject.put("account_id", accountId);
            jsonObject.put("first_name", firstName);
            jsonObject.put("last_name", lastName);
            jsonObject.put("gender", gender);
            jsonObject.put("birth_date", birthDate);
            jsonObject.put("due_date", dueDate);
            jsonObject.put("created_at", createdAt);
            jsonObject.put("updated_at", updatedAt);
            jsonObject.put("twin_id", twinId);
            jsonObject.put("photo_url", photoUrl);
            return jsonObject;
        }

        public boolean isMale() {
            return GENDER_BOY.equalsIgnoreCase(gender);
        }

        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class SSEvent {
        public static final String TYPE_LEARNING_PERIOD = "learningPeriod";
        public static final String TYPE_NAP = "nap";
        public static final String TYPE_HEART_RATE = "heartRate";

        public String id;
        public String childId;
        public String eventType;
        public String startDate;
        public String endDate;
        public String createdAt;
        public String updatedAt;
        //        public String data;
        public String jsonString;

        public SSEvent() {
        }

        public SSEvent(JSONObject jsonObject) throws JSONException {
            id = jsonObject.getString("id");
            childId = jsonObject.getString("child_id");
            eventType = jsonObject.getString("event_type");
            startDate = jsonObject.getString("start_date");
            endDate = jsonObject.getString("end_date");
            createdAt = jsonObject.getString("created_at");
            updatedAt = jsonObject.getString("updated_at");
//            data = jsonObject.getString("data");
            jsonString = jsonObject.toString();
        }

        public static SSEvent parseEvent(JSONObject jsonObject) throws JSONException {
            return isNapEvent(jsonObject) ? new SSNap(jsonObject) : new SSEvent(jsonObject);
        }

        static boolean isNapEvent(JSONObject data) {
            return data.toString().contains(TYPE_NAP);
        }


        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class SSNap extends SSEvent implements Serializable {

        public List<Spell> spells;

        public SSNap() {
            super();
        }

        public SSNap(JSONObject jsonObject) throws JSONException {
            super(jsonObject);
            parseData(jsonObject.optJSONObject("data"));
        }

        void parseData(JSONObject data) throws JSONException {
            if (data != null) {
                spells = new ArrayList<>();
                JSONArray spellsJSONArray = data.getJSONArray("spells");
                for (int i = 0; i < spellsJSONArray.length(); i++) {
                    spells.add(new Spell((JSONObject) spellsJSONArray.get(i)));
                }
            }
        }

        public List<Spell> getSpells() {
            return spells;
        }

        public int getNumOfWakings() {
            int count = 0;
            for (Spell spell : spells) {
                count = spell.isWake() ? ++count : count;
            }
            return count;
        }

        public long getTimeAwake() {
            long time = 0;
            for (Spell spell : spells) {
                time = spell.isWake() ? time + spell.getDuration() : time;
            }
            return time;
        }

        public long getTimeAsleep() {
            long time = 0;
            for (Spell spell : spells) {
                time = spell.isSleep() ? time + spell.getDuration() : time;
            }
            return time;
        }

        public long getTimeStirring() {
            long time = 0;
            for (Spell spell : spells) {
                time = spell.isStirring() ? time + spell.getDuration() : time;
            }
            return time;
        }

        public static class Spell implements Serializable {
            public static final int TYPE_SLEEP = 0;
            public static final int TYPE_STIRRING = 1;
            public static final int TYPE_WAKE = 2;

            TimeZone tz = TimeZone.getTimeZone("PDT");
            Calendar startTime = Calendar.getInstance();
            Calendar endTime = Calendar.getInstance();
            int type;

            public Spell() {
            }

            public Spell(JSONObject jsonObject) throws JSONException {
                try {
                    startTime.setTime(DateTimeUtils.parseSpellDate(jsonObject.getString("start_ts")));
                    endTime.setTime(DateTimeUtils.parseSpellDate(jsonObject.getString("end_ts")));
                    startTime.setTimeZone(tz);
                    endTime.setTimeZone(tz);
                    type = jsonObject.getInt("swstatus");
                } catch (ParseException e) {
                    e.printStackTrace();
                }
            }

            public Calendar getStartTime() {
                return startTime;
            }

            public void setStartTime(Calendar calendar) {
                startTime = calendar;
            }

            public Calendar getEndTime() {
                return endTime;
            }

            public void setEndTime(Calendar calendar) {
                endTime = calendar;
            }

            public int getType() {
                return type;
            }

            public void setType(int type) {
                this.type = type;
            }

            public long getDuration() {
                return endTime.getTimeInMillis() - startTime.getTimeInMillis();
            }

            public boolean isSleep() {
                return type == TYPE_SLEEP;
            }

            public boolean isStirring() {
                return type == TYPE_STIRRING;
            }

            public boolean isWake() {
                return type == TYPE_WAKE;
            }
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

        public SSArticle() {
        }

        private SSArticle(JSONObject jsonObject) throws JSONException {
            id = jsonObject.getString("id");
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

    public static class EOLData {
        public String popUpText;
        public String settingsPopUpTxt;
        public String settingsPopUpEmail;
        public String settingsPopUpSupportWebsite;
        public String postEOLPopUpText;
        public String isEOL;
        public String jsonString;


        public EOLData() {
        }

        private EOLData(JSONObject jsonObject) throws JSONException {
            popUpText = jsonObject.getString("app_launch_popup_notice_text");
            settingsPopUpTxt = jsonObject.getString("settings_popup_text");
            settingsPopUpEmail = jsonObject.getString("settings_popup_support_email");
            settingsPopUpSupportWebsite = jsonObject.getString("settings_popup_support_website");
            postEOLPopUpText = jsonObject.getString("post_eol_app_launch_popup_notice_text");
            isEOL = jsonObject.getString("is_eol");

            jsonString = jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }
    }

    public static class UpdateInfo {
        //        private ApiInfo apiInfo;
//        private IosInfo iosInfo;
        public AndroidInfo androidInfo;
        private String jsonString;

        public UpdateInfo() {
        }

        private UpdateInfo(JSONObject jsonObject) throws JSONException {
//            apiInfo = new ApiInfo(jsonObject.getJSONObject("api"));
//            iosInfo = new IosInfo(jsonObject.getJSONObject("ios"));
            androidInfo = new AndroidInfo(jsonObject.getJSONObject("android"));
            jsonString = jsonObject.toString();
        }

        @Override
        public String toString() {
            return jsonString;
        }

//        private class ApiInfo {
//            private String currentVersion;
//            public String minRequiredVersion;
//
//            private ApiInfo(JSONObject jsonObject) throws JSONException {
//                currentVersion = jsonObject.getString("current_version");
//                minRequiredVersion = jsonObject.getString("min_required_version");
//            }
//        }

//        private class IosInfo {
//            private String currentVersion;
//            public String minRequiredVersion;
//            private String updateUrl;
//
//            private IosInfo(JSONObject jsonObject) throws JSONException {
//                currentVersion = jsonObject.getString("current_version");
//                minRequiredVersion = jsonObject.getString("min_required_version");
//                updateUrl = jsonObject.getString("update_url");
//            }
//        }

        public class AndroidInfo {
            public int minRequiredVersion;
            public String updateUrl;
            private int currentVersion;

            private AndroidInfo(JSONObject jsonObject) throws JSONException {
                currentVersion = jsonObject.getInt("current_version");
                minRequiredVersion = jsonObject.getInt("min_required_version");
                updateUrl = jsonObject.getString("update_url");
            }
        }
    }
}
