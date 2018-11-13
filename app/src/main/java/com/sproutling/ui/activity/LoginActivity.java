/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.activity;

import android.app.AlertDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.IntentCompat;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.webkit.URLUtil;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.helpshift.Core;
import com.sproutling.App;
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.pipeline.MqttAPI;
import com.sproutling.pojos.CreateHandheldResponse;
import com.sproutling.pojos.LoginRequestBody;
import com.sproutling.pojos.LoginResponse;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.dialogfragment.InternetOfflineDialogFragment;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static com.sproutling.App.FISHER_PRICE_CHINA_FLAVOR;


public class LoginActivity extends BaseActivity {

    public static final String TAG = "LoginActivity";

    private static final int MAX_ATTEMPT = 3;
    private static final int PASSWORD_MAX = 72;
    private static final int PASSWORD_MIN = 8;

    private CustomShEditText mEmailPhoneEditText, mPasswordEditText;
    private Button mSignInButton;
    TextWatcher mTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            mSignInButton.setEnabled(mEmailPhoneEditText.getText().length() > 0 &&
                    mPasswordEditText.getText().length() >= PASSWORD_MIN &&
                    mPasswordEditText.getText().length() <= PASSWORD_MAX &&
                    isUserIdValid(mEmailPhoneEditText.getText().toString()));
        }
    };
    private ShTextView mTvServerUrl;
    private Handler mHandler;
    private int mAttempt = MAX_ATTEMPT;
    private SSManagement.User mUser;
    View.OnClickListener mOnClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.navigation_back:
                    finish();
                    break;
                case R.id.sign_in:
                    ConnectivityManager connectivityManager = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
                    NetworkInfo networkInfo = connectivityManager.getActiveNetworkInfo();
                    if (networkInfo != null && networkInfo.isConnected()) {
                        login();
                    } else {
                        InternetOfflineDialogFragment
                                .newInstance()
                                .show(getFragmentManager(), null);
                    }
                    break;
                case R.id.forgot_password:
                    Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
                    startActivity(intent);
                    break;
                default:
            }
        }
    };
    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
            }
            return false;
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        mTvServerUrl = (ShTextView) findViewById(R.id.tv_update_url);
        mTvServerUrl.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        mTvServerUrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showEnvironmentSwitchDialog();
            }
        });

        findViewById(R.id.navigation_back).setOnClickListener(mOnClickListener);
        mEmailPhoneEditText = findViewById(R.id.email);
        mEmailPhoneEditText.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (!hasFocus) {
                    mEmailPhoneEditText.showErrorMsg(!isUserIdValid(mEmailPhoneEditText.getText().toString()));
                    if (Utils.isPhoneValid(mEmailPhoneEditText.getText().toString())) {
                        String phoneFormat = Utils.formatPhone(mEmailPhoneEditText.getText().toString(), true);
                        Log.d(TAG, "phone format: " + phoneFormat);
                        mEmailPhoneEditText.setText(phoneFormat);
                    }
                }
            }
        });
        String countryCode = Locale.getDefault().getCountry();
        mEmailPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(countryCode));
        mPasswordEditText = findViewById(R.id.password);
//        mEmailErrorView = (ShTextView) findViewById(R.id.email_error);
        mSignInButton = (Button) findViewById(R.id.sign_in);
        mSignInButton.setOnClickListener(mOnClickListener);
        findViewById(R.id.forgot_password).setOnClickListener(mOnClickListener);

        mEmailPhoneEditText.addTextChangedListener(mTextWatcher);
        mPasswordEditText.addTextChangedListener(mTextWatcher);

        mEmailPhoneEditText.setOnEditorActionListener(mOnEditorActionListener);
        mPasswordEditText.setOnEditorActionListener(mOnEditorActionListener);


        mSignInButton.setEnabled(false);
        mHandler = new Handler();

        ShTextView tvStyleGuide = findViewById(R.id.tv_style_guide);
        tvStyleGuide.setVisibility(BuildConfig.DEBUG ? View.VISIBLE : View.GONE);
        tvStyleGuide.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(LoginActivity.this, StyleGuideSample.class));
            }
        });
    }

    private void showEnvironmentSwitchDialog() {
        AlertDialog.Builder builder;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            builder = new AlertDialog.Builder(this);
        } else {
            builder = new AlertDialog.Builder(this);
        }
        View view = getLayoutInflater().inflate(R.layout.dialog_environment_switch, null);
        final CustomShEditText etServerUrl = view.findViewById(R.id.et_server_url);
        final CustomShEditText etMqttServerUrl = view.findViewById(R.id.et_mqtt_url);
        etServerUrl.setText(SSManagement.ENDPOINT);
        etMqttServerUrl.setText(MqttAPI.MQTT_TLS_BROKER);
        builder.setView(view);
        builder.setPositiveButton(R.string.save, null);

        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        final AlertDialog mAlertDialog = builder.create();
        mAlertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(final DialogInterface dialog) {
                Button positiveBtn = mAlertDialog.getButton(AlertDialog.BUTTON_POSITIVE);
                positiveBtn.setOnClickListener(new View.OnClickListener() {

                    @Override
                    public void onClick(View view) {
                        String serverUrl = etServerUrl.getText().toString();
                        String mqttUrl = etMqttServerUrl.getText().toString();
                        String errorMessage = null;
                        if (!URLUtil.isValidUrl(serverUrl)) {
                            errorMessage = "Invalid Server Url format.";
                        }
                        if (TextUtils.isEmpty(errorMessage)) {
                            SSManagement.ENDPOINT = serverUrl;
                            MqttAPI.MQTT_TLS_BROKER = mqttUrl;
                            SharedPrefManager.saveServerUrl(LoginActivity.this, SSManagement.ENDPOINT);
                            SharedPrefManager.saveMqttUrl(LoginActivity.this, MqttAPI.MQTT_TLS_BROKER);
                            SproutlingApi.updateServerUrl(SSManagement.ENDPOINT);
                            dialog.dismiss();
                        } else {
                            Toast.makeText(LoginActivity.this, errorMessage, Toast.LENGTH_LONG).show();
                        }
                    }
                });
            }
        });
        mAlertDialog.show();
    }

    private boolean isUserIdValid(String userId) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(userId).matches() || Utils.isPhoneValid(userId);
    }

    boolean retry(SSException exception) {
        if (BuildConfig.DEBUG) {
            Log.d(TAG, "Retry: " + mAttempt);
        }
        if (exception.getResponseCode() == 401) {
            if (--mAttempt != 0) {
                return true;
            } else {
                mAttempt = MAX_ATTEMPT;
                return false;
            }
        }
        return false;
    }

    public void login() {
        final String userID = Utils.isPhoneValid(mEmailPhoneEditText.getText().toString()) ? Utils.formatPhone(mEmailPhoneEditText.getText().toString(), false) : mEmailPhoneEditText.getText().toString();
        final String password = mPasswordEditText.getText().toString();
//        loginIdentity(email, password);

        new AsyncTask<Void, Void, SSManagement.User>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected SSManagement.User doInBackground(Void... params) {
                try {
                    return SKManagement.loginByPassword(userID, password);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(final SSManagement.User user) {
                if (user != null) {
                    mUser = user;
                    AccountManagement accountManagement = AccountManagement.getInstance(LoginActivity.this);
                    accountManagement.writeToPreferences(user);
                    accountManagement.writePassword(password);
                    Utils.logEvents(LogEvents.LOGGED_IN);
                    App.getInstance().initializeMqtt();
                    setUpPushNotification();
                } else {
                    showProgressBar(false);
                    if (mError != null) handleError(mError);
                }
            }
        }.execute();
    }

    public void getUser(final SSManagement.User user) {
        new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {
            private SSException mException;

            @Override
            protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                try {
                    return SKManagement.getUserById(user.accessToken, user.resourceOwnerId);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.UserAccountInfo userAccountInfo) {
                if (userAccountInfo != null) {
                    mAttempt = MAX_ATTEMPT;
                    AccountManagement.getInstance(LoginActivity.this).writeUserAccountInfo(userAccountInfo);
                    Utils.setMixpanelUserProfile();
                    if (!BuildConfig.FLAVOR_app.equals(FISHER_PRICE_CHINA_FLAVOR)) {
                        Core.login(userAccountInfo.email, userAccountInfo.firstName, userAccountInfo.email);
                    }
                    getChildInfo(user.accessToken);
                } else if (mException != null && retry(mException)) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getUser(user);
                        }
                    }, 1000);
                } else {
                    showProgressBar(false);
                    AccountManagement.getInstance(LoginActivity.this).clear();
                    if (mException != null && mException.getError() != null)
                        handleError(mException.getError());
                }
            }
        }.execute();
    }

    private void getChildInfo(final String accessToken) {
        new AsyncTask<Void, Void, SSManagement.Child>() {
            private SSException mException;

            private SSManagement.Child getChild() throws JSONException, SSException, IOException {
                List<SSManagement.Child> children = SKManagement.listChildren(accessToken);
                if (children.isEmpty()) return null;
                return children.get(0);
            }

            @Override
            protected SSManagement.Child doInBackground(Void... voids) {
                try {
                    return getChild();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.Child child) {
                if (child != null) {
                    mAttempt = MAX_ATTEMPT;
                    AccountManagement.getInstance(LoginActivity.this).writeChild(child);
                    listDevice(accessToken);
                } else if (mException != null && retry(mException)) {
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            getChildInfo(accessToken);
                        }
                    }, 1000);
                } else {
                    showProgressBar(false);
                    AccountManagement.getInstance(LoginActivity.this).clear();
                    if (mException != null && mException.getError() != null)
                        handleError(mException.getError());
                }
            }
        }.execute();
    }

    private void listDevice(final String accessToken) {
        new AsyncTask<Void, Void, List<SSManagement.DeviceResponse>>() {
            private SSException mException;

            @Override
            protected List<SSManagement.DeviceResponse> doInBackground(Void... voids) {
                try {
                    return SKManagement.listDevices(accessToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    mException = e;
                }
                return null;
            }

            @Override
            protected void onPostExecute(List<SSManagement.DeviceResponse> devices) {
                if (devices != null && !devices.isEmpty()) {
                    showProgressBar(false);
                    SharedPrefManager.saveDevice(LoginActivity.this, devices.get(0));
                    transitionToActivity(StatusActivity.class.getName());
                } else if (devices != null && devices.isEmpty()) {
                    checkEvents(accessToken);
                } else if (mException != null) {
                    if (mException.getResponseCode() == 404) {
                        showProgressBar(false);
                        // TODO: how to handle this
                        transitionToActivity(StatusActivity.class.getName());
                    } else if (retry(mException)) {
                        mHandler.postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                listDevice(accessToken);
                            }
                        }, 2000);
                    }
                } else if (SharedPrefManager.getDevice(LoginActivity.this) != null) {
                    showProgressBar(false);
                    transitionToActivity(StatusActivity.class.getName());
                } else {
                    showProgressBar(false);
                    transitionToActivity(SettingsActivity.class.getName());
                }
            }
        }.execute();
    }

    private void checkEvents(final String accessToken) {
        new AsyncTask<Void, Void, Boolean>() {
            SSError mError;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    List<SSManagement.SSEvent> events = SKManagement.listEventsByChild(accessToken, AccountManagement.getInstance(LoginActivity.this).getChild().id);
                    return events == null || events.isEmpty();
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return Boolean.TRUE;
            }

            @Override
            protected void onPostExecute(Boolean empty) {
                showProgressBar(false);
                if (empty) {
                    SharedPrefManager.clearDevice(LoginActivity.this);
                    Utils.logEvents(LogEvents.TIMELINE_NO_DATA);
                    transitionToActivity(SettingsActivity.class.getName());
                } else {
                    transitionToActivity(StatusActivity.class.getName());
                }
            }
        }.execute();
    }

    @Override
    protected void onPushNotificationRegistrationSuccess(CreateHandheldResponse createHandheldResponse) {
        super.onPushNotificationRegistrationSuccess(createHandheldResponse);
        getUser(mUser);
    }

    @Override
    protected void onPushNotificationRegistrationFailure(Throwable t) {
        super.onPushNotificationRegistrationFailure(t);
        //even if push notification fails move forward in the app.
        getUser(mUser);
    }

    private void transitionToActivity(String className) {
        Intent intent = IntentCompat.makeRestartActivityTask(new ComponentName(getPackageName(), className));
        startActivity(intent);
        finish();
    }

    private void loginIdentity(String email, String password) {
        final LoginRequestBody requestBody = new LoginRequestBody(email, password);
//        showProgressBar(true);
        SproutlingApi.login(requestBody, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
//                showProgressBar(false);
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    AccountManagement.getInstance(LoginActivity.this).saveUserAccount(loginResponse);
//                    if (mListener != null) mListener.onAccountCreated(true);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                Log.e(TAG, "Login Failure");
            }
        });
    }

    void handleError(SSError error) {
        AccountManagement.getInstance(this).clear();
        SharedPrefManager.clear(this);

        new AlertDialog.Builder(this)
                .setTitle(R.string.login_error_message_title)
                .setMessage(R.string.login_error_message_body)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        showProgressBar(false);
                    }
                })
                .show();
    }
}
