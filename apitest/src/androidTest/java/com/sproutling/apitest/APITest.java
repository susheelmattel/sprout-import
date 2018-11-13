package com.sproutling.apitest;

import android.support.test.runner.AndroidJUnit4;

import org.junit.runner.RunWith;

/**
 * Created by bradylin on 11/30/16.
 */

@RunWith(AndroidJUnit4.class)
public class APITest {

    static final String TAG = "APITest";

    /**
     *
     * User
     *
     * */

//    @Test
//    public void testCreateUser() {
//        try {
//            SSManagement.UserAccountInfo userAccount = SKManagement.createUser("abcd@mattel.com", "first", "last", SSManagement.TYPE_GUARDIAN, "12345678", "+18054037038", "");
//            Log.d(TAG, userAccount.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testLogin() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            Log.d(TAG, user.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testRefreshToken() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            user = SKManagement.refreshToken(user.accessToken, user.refreshToken);
//            Log.d(TAG, user.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testGetTokenInfo() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.User user2 = SKManagement.getTokenInfo(user.accessToken);
//            Log.d(TAG, user2.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testLogOut() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            boolean loggedOut = SKManagement.logout(user.accessToken);
//            Log.d(TAG, "Logged Out: " + loggedOut);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testGetUser() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.UserAccountInfo account = SKManagement.getUserById(user.accessToken, user.resourceOwnerId);
//            Log.d(TAG, account.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }
//
//    @Test
//    public void testUpdateUser() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            JSONObject body = new JSONObject();
//            body.put("password", "12345678");
//            body.put("password_confirmation", "12345678");
//            SSManagement.UserAccountInfo account = SKManagement.updateUserById(user.accessToken, user.resourceOwnerId, body);
//            Log.d(TAG, account.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }
//
//    @Test
//    public void testDeleteUser() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            SKManagement.deleteUserById(user.accessToken, user.resourceOwnerId);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

    /**
     *
     * Child
     *
     * */

//    @Test
//    public void testCreateChild() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            SSManagement.Child child = SKManagement.createChild(user.accessToken, "Brady Jr", "Lin", "M", "2014-08-08", "2014-08-08");
//            Log.d(TAG, child.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testListChildren() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            List<SSManagement.Child> children = SKManagement.listChildren(user.accessToken);
//            for (SSManagement.Child child : children) {
//                Log.d(TAG, "Child: " + child.toString());
//            }
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testGetChild() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            SSManagement.Child child = SKManagement.getChildById(user.accessToken, "77d108b2-4922-4003-b026-13be9df95bd6");
//            Log.d(TAG, "Child: " + child.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testUpdateChild() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            SSManagement.Child child = SKManagement.updateChildById(user.accessToken, "77d108b2-4922-4003-b026-13be9df95bd6", new JSONObject().put("first_name", "abcde").put("gender", "M").put("birth_date", "2014-08-08").put("birth_date", "2014-08-08"));
//            Log.d(TAG, "Child: " + child.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testDeleteChild() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            boolean result = SKManagement.deleteChildById(user.accessToken, "77d108b2-4922-4003-b026-13be9df95bd6");
//            Log.d(TAG, "Result: " + result);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

    /**
     * Events
     */

//    @Test
//    public void testCreateEvent() {
//        try {
//            Log.d(TAG, "-------------- Test Create Event --------------");
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            List<SSManagement.Child> children = SKManagement.listChildren(user.accessToken);
//            // Test on first child
//            SSManagement.SSEvent event = SKManagement.createEvent(user.accessToken,
//                    new JSONObject().put("child_id", children.get(0).id)
//                                    .put("event_type", "nap")
//                                    .put("start_date", "2017-02-20T19:19:05.234410974Z")
//                                    .put("end_date", "2017-02-20T19:19:05.234410974Z"));
//            Log.d(TAG, "Event: " + event.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testListEventsByChild() {
//        try {
//            Log.d(TAG, "-------------- Test List Events By Child --------------");
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            List<SSManagement.Child> children = SKManagement.listChildren(user.accessToken);
//            List<SSManagement.SSEvent> events = SKManagement.listEventsByChild(user.accessToken, children.get(0).id);
//            for (SSManagement.SSEvent event : events) {
//                Log.d(TAG, "Event: " + event.toString());
//            }
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testGetEventById() {
//        try {
//            Log.d(TAG, "-------------- Test Get Event By ID --------------");
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.SSEvent event = SKManagement.getEventById(user.accessToken, "4d9c98c0-a54a-429d-b5c9-f8a5c07e5220");
//            Log.d(TAG, "Event: " + event);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testUpdateEventById() {
//        try {
//            Log.d(TAG, "-------------- Test Update Event By ID --------------");
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.SSEvent event = SKManagement.updateEventById(user.accessToken, "4d9c98c0-a54a-429d-b5c9-f8a5c07e5220", new JSONObject().put("end_date", "2017-02-20T19:20:05.234410974Z"));
//            Log.d(TAG, "Event: " + event);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testEventsByWeek() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            List<SSManagement.Child> children = SKManagement.listChildren(user.accessToken);
//            // Test on first child
//            List<SSManagement.SSEvent> events = SKManagement.getEventsByWeek(user.accessToken, children.get(0).id, "", "2017-02-20T19:19:05.234410974Z");
//            for (SSManagement.SSEvent event : events) {
//                Log.d(TAG, "Event: " + event.toString());
//            }
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testListArticles() {
//        try {
//            Log.d(TAG, "-------------- Test List Articles --------------");
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            List<SSManagement.SSArticle> articles = SKManagement.listArticles(user.accessToken, 0, 30);
//            for (SSManagement.SSArticle article : articles) {
//                Log.d(TAG, "Article: " + article.toString());
//            }
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testGetArticleById() {
//        try {
//            Log.d(TAG, "-------------- Test Get Article By ID --------------");
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            List<SSManagement.SSArticle> articles = SKManagement.listArticles(user.accessToken, 0, 30);
//            SSManagement.SSArticle article = SKManagement.getArticle(user.accessToken, articles.get(0).id);
//            Log.d(TAG, "Article: " + article);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }


    /**
     * Device
     */
//    @Test
//    public void testListDevice() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abcd@mattel.com", "12345678");
//            SKManagement.listDevices(user.accessToken);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }


    /**
     * Reset
     */
//    @Test
//    public void testRequestPin() {
//        try {
//            boolean success = SKManagement.requestPin("+18054037037");
//            Log.d(TAG, "Success: " + success);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testValidatePin() {
//        try {
//            boolean success = SKManagement.validatePin("+18054037037", "93269");
//            Log.d(TAG, "Success: " + success);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }
//
//    @Test
//    public void testResetPassword() {
//        try {
//            boolean success = SKManagement.resetPassword("+18054037037", "93269", "12345678", "12345678");
//            Log.d(TAG, "Success: " + success);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

    /**
     * Caregivers
     */
//    @Test
//    public void testInvitations() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.UserAccountInfo userAccountInfo = SKManagement.getUserById(user.accessToken, user.resourceOwnerId);
//            String inviteToken = SKManagement.invitations(user.accessToken, userAccountInfo.accountId);
//            Log.d(TAG, "Invite Token: " + inviteToken);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testListCaregivers() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.UserAccountInfo userAccountInfo = SKManagement.getUserById(user.accessToken, user.resourceOwnerId);
//            List<SSManagement.UserAccountInfo> userAccountInfos = SKManagement.listCaregivers(user.accessToken, userAccountInfo.accountId);
//            for (SSManagement.UserAccountInfo info : userAccountInfos) {
//                Log.d(TAG, "User: " + info.toString());
//            }
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testRemoveCaregiver() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.UserAccountInfo userAccountInfo = SKManagement.getUserById(user.accessToken, user.resourceOwnerId);
//            boolean success = SKManagement.removeCaregiver(user.accessToken, userAccountInfo.accountId, "4812cbde-3048-4bd0-a5a8-9a0475fb6fb7");
//            Log.d(TAG, "Success: " + success);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

    /**
     * Device
     */
//    @Test
//    public void testCreateDevice() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.DeviceResponse device = SKManagement.createDevice(user.accessToken,
//                    new JSONObject().put("name", "My Sproutling")
//                            .put("owner_id", user.resourceOwnerId)
//                            .put("owner_type", "Child")
//                            .put("serial", "j29jg93gj198tgj193hg193g")
//                            .put("firmware_version", "1.0.0"));
//            Log.d(TAG, "Device: " + device.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testListDevices() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            List<SSManagement.DeviceResponse> deviceResponses = SKManagement.listDevices(user.accessToken);
//            for (SSManagement.DeviceResponse device : deviceResponses) {
//                Log.d(TAG, "Device: " + device.toString());
//            }
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testGetDevice() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.DeviceResponse device = SKManagement.getDeviceBySerial(user.accessToken, "j29jg93gj198tgj193hg193g");
//            Log.d(TAG, "Device: " + device.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testUpdateDevice() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            SSManagement.DeviceResponse device = SKManagement.updateDevice(user.accessToken, "j29jg93gj198tgj193hg193g",
//                    new JSONObject().put("name", "My Sproutling")
//                            .put("owner_id", user.resourceOwnerId)
//                            .put("owner_type", "Child")
//                            .put("serial", "j29jg93gj198tgj193hg193g")
//                            .put("firmware_version", "1.0.0"));
//            Log.d(TAG, "Device: " + device.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

//    @Test
//    public void testDeleteDevice() {
//        try {
//            SSManagement.User user = SKManagement.loginByPassword("abc@mattel.com", "12345678");
//            boolean deleted = SKManagement.deleteDeviceBySerial(user.accessToken, "j29jg93gj198tgj193hg193g");
//            Log.d(TAG, "Deleted: " + deleted);
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }

    /**
     * Update Info
     */
//    @Test
//    public void testUpdateInfo() {
//        try {
//            SSManagement.UpdateInfo updateInfo = SKManagement.getUpdateInfo();
//            Log.d(TAG, "Update Info: " + updateInfo.toString());
//        } catch (IOException e) {
//            Assert.fail(e.toString());
//        } catch (JSONException e) {
//            Assert.fail(e.toString());
//        } catch (SSException e) {
//            Assert.fail(e.toString());
//        }
//    }
}
