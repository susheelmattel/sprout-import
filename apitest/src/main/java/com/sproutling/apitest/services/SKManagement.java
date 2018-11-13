package com.sproutling.apitest.services;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.List;

/**
 * Created by bradylin on 11/28/16.
 */

public class SKManagement {

    private static final String TAG = "SKManagement";

    public static SSManagement.UserAccountInfo createUser(String email, String firstName, String lastName, String type, String password, String phone, String inviteToken) throws IOException, JSONException, SSException {
        return SSManagement.createUser(email, firstName, lastName, type, password, phone, inviteToken);
    }

    public static SSManagement.User loginByPassword(String email, String password) throws IOException, JSONException, SSException {
        return SSManagement.loginByPassword(email, password);
    }

    public static SSManagement.User refreshToken(String accessToken, String refreshToken) throws IOException, JSONException, SSException {
        return SSManagement.refreshToken(accessToken, refreshToken);
    }

    public static SSManagement.User getTokenInfo(String accessToken) throws IOException, JSONException, SSException {
        return SSManagement.getTokenInfo(accessToken);
    }

    public static boolean logout(String accessToken) throws IOException, JSONException, SSException {
        return SSManagement.logout(accessToken);
    }

    public static SSManagement.UserAccountInfo getUserById(String token, String userId) throws IOException, JSONException, SSException {
        return SSManagement.getUserById(token, userId);
    }

    public static SSManagement.UserAccountInfo updateUserById(String token, String userId, JSONObject body) throws IOException, JSONException, SSException {
        return SSManagement.updateUserById(token, userId, body);
    }

    public static boolean deleteUserById(String token, String userId) throws IOException, JSONException, SSException {
        return SSManagement.deleteUserById(token, userId);
    }

    public static SSManagement.Child createChild(String token, String firstName, String lastName, String gender, String birthDate, String dueDate) throws IOException, JSONException, SSException {
        return SSManagement.createChild(token, firstName, lastName, gender, birthDate, dueDate);
    }

    public static List<SSManagement.Child> listChildren(String token) throws IOException, JSONException, SSException {
        return SSManagement.listChildren(token);
    }

    public static SSManagement.Child getChildById(String token, String childId) throws IOException, JSONException, SSException {
        return SSManagement.getChildById(token, childId);
    }

    public static SSManagement.Child updateChildById(String token, String childId, JSONObject body) throws IOException, JSONException, SSException {
        return SSManagement.updateChildById(token, childId, body);
    }

    public static boolean deleteChildById(String token, String childId) throws IOException, JSONException, SSException {
        return SSManagement.deleteChildById(token, childId);
    }

    public static SSManagement.SSEvent createEvent(String token, JSONObject body) throws IOException, JSONException, SSException {
        return SSManagement.createEvent(token, body);
    }

    public static List<SSManagement.SSEvent> listEventsByChild(String token, String childId) throws IOException, JSONException, SSException {
        return SSManagement.listEventsByChild(token, childId);
    }

    public static SSManagement.SSEvent getEventById(String token, String eventId) throws IOException, JSONException, SSException {
        return SSManagement.getEventById(token, eventId);
    }

    public static SSManagement.SSEvent updateEventById(String token, String eventId, JSONObject body) throws IOException, JSONException, SSException {
        return SSManagement.updateEventById(token, eventId, body);
    }

    public static List<SSManagement.SSEvent> getEventsByWeek(String token, String childId, String eventType, String weekOf) throws IOException, JSONException, SSException {
        return SSManagement.getEventsByWeek(token, childId, eventType, weekOf);
    }

    public static List<SSManagement.SSArticle> listArticles(String accessToken, int startAgeDay, int endAgeDay) throws IOException, JSONException, SSException {
        return SSManagement.listArticles(accessToken, startAgeDay, endAgeDay);
    }

    public static SSManagement.SSArticle getArticle(String accessToken, String articleId) throws IOException, JSONException, SSException {
        return SSManagement.getArticleById(accessToken, articleId);
    }

    public static SSManagement.DeviceResponse createDevice(String accessToken, JSONObject body) throws IOException, JSONException, SSException {
        return SSManagement.createDevice(accessToken, body);
    }

    public static List<SSManagement.DeviceResponse> listDevices(String accessToken) throws IOException, JSONException, SSException {
        return SSManagement.listDevices(accessToken);
    }

    public static SSManagement.DeviceResponse getDeviceBySerial(String accessToken, String serial) throws IOException, JSONException, SSException {
        return SSManagement.getDeviceBySerial(accessToken, serial);
    }

    public static SSManagement.DeviceResponse updateDevice(String accessToken, String serial, JSONObject body) throws IOException, JSONException, SSException {
        return SSManagement.updateDevice(accessToken, serial, body);
    }

    public static boolean deleteDeviceBySerial(String accessToken, String serial) throws IOException, JSONException, SSException {
        return SSManagement.deleteDeviceBySerial(accessToken, serial);
    }

    public static boolean requestPin(String phoneNumber) throws JSONException, IOException, SSException {
        return SSManagement.requestPin(phoneNumber);
    }

    public static boolean validatePin(String phoneNumber, String pin) throws JSONException, IOException, SSException {
        return SSManagement.validatePin(phoneNumber, pin);
    }

    public static boolean resetPassword(String phoneNumber, String pin, String password, String passwordConfirmation) throws JSONException, IOException, SSException {
        return SSManagement.resetPassword(phoneNumber, pin, password, passwordConfirmation);
    }

    public static String invitations(String accessToken, String accoundId) throws IOException, JSONException, SSException {
        return SSManagement.invitations(accessToken, accoundId);
    }

    public static List<SSManagement.UserAccountInfo> listCaregivers(String accessToken, String accountId) throws IOException, JSONException, SSException {
        return SSManagement.listCaregivers(accessToken, accountId);
    }

    public static boolean removeCaregiver(String accessToken, String accountId, String caregiverId) throws IOException, JSONException, SSException {
        return SSManagement.removeCaregiver(accessToken, accountId, caregiverId);
    }

    public static SSManagement.UpdateInfo getUpdateInfo() throws IOException, JSONException, SSException {
        return SSManagement.getUpdateInfo();
    }
}