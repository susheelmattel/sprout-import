/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Setup;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Base64;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.auth0.android.jwt.DecodeException;
import com.auth0.android.jwt.JWT;
import com.helpshift.Core;
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.pojos.LoginRequestBody;
import com.sproutling.pojos.LoginResponse;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.activity.LoginActivity;
import com.sproutling.ui.activity.SetupActivity;
import com.sproutling.ui.dialogfragment.CaregiverAcceptInvitationDialogFragment;
import com.sproutling.ui.dialogfragment.EmailRegisteredDialogFragment;
import com.sproutling.ui.dialogfragment.GenericErrorDialogFragment;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.CustomShInputTextLayout;
import com.sproutling.utils.LogEvents;
import com.sproutling.utils.Utils;

import org.json.JSONException;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.sproutling.App.FISHER_PRICE_CHINA_FLAVOR;

public class SetupAccountCreationFragment extends BaseFragment {

    public static final String TAG = "SetupAccountCreationFragment";

    private static final int PASSWORD_MAX = 72;
    private static final int PASSWORD_MIN = 8;
    private static final int STATE_PASS = 1;
    private static final int STATE_FAIL1 = 0;
    private static final int STATE_FAIL2 = -1;
    private static final int STATE_FAIL3 = -2;

    private static final int REQUEST_ACCEPT_INVITATION = 1;
    private static final int REQUEST_CODE_EMAIL_DUPLICATE = 2;

    private OnAccountCreationListener mListener;

    private CustomShEditText mEmailEditText, mPhoneEditText, mPasswordEditText;
    private CustomShEditText mFirstNameEditText, mLastNameEditText;
    private CustomShInputTextLayout mFirstNameEditTextWrapper, mLastNameEditTextWrapper, mEmailEditTextWrapper,
            mPhoneEditTextWrapper, mPasswordEditTextWrapper;

    private int mFirstNameState, mLastNameState, mEmailState, mPhoneState, mPasswordState;
    private String mAccountType = SSManagement.TYPE_GUARDIAN;
    private String mCaregiverData;
    private View.OnClickListener mOnLayoutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
        }
    };
    private TextView.OnEditorActionListener mOnEditorActionListener = new TextView.OnEditorActionListener() {

        @Override
        public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
            if (actionId == EditorInfo.IME_ACTION_DONE) {
                InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(v.getApplicationWindowToken(), 0);
                if (v.getId() == mPasswordEditText.getId()) {
                    checkFields(mPasswordEditText.getId());
                    if (isStatesAllPass() && mListener != null) mListener.performAccountCreation();
                }
            }
            return false;
        }
    };
    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            showInfoMsg(v.getId(), hasFocus);
            if (!hasFocus) {
                checkFields(v.getId());
            }
        }
    };
    private TextWatcher mFirstNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkFirstName();
            enableIfReady();
        }
    };
    private TextWatcher mLastNameTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkLastName();
            enableIfReady();
        }
    };
    private TextWatcher mEmailTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkEmail();
            enableIfReady();
        }
    };
    private TextWatcher mPhoneTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkPhone();
            enableIfReady();
        }
    };
    private TextWatcher mPasswordTextWatcher = new TextWatcher() {
        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {
        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {
        }

        @Override
        public void afterTextChanged(Editable s) {
            checkPassword();
            enableIfReady();
        }
    };

    public SetupAccountCreationFragment() {
    }

    public static SetupAccountCreationFragment newInstance(String caregiverData) {
        SetupAccountCreationFragment fragment = new SetupAccountCreationFragment();
        Bundle arguments = new Bundle();
        arguments.putString(SetupActivity.EXTRA_CAREGIVER_DATA, caregiverData);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Bundle arguments = getArguments();
        if (arguments != null) {
            mCaregiverData = arguments.getString(SetupActivity.EXTRA_CAREGIVER_DATA);
        }
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_ADJUST_NOTHING);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_setup_account_creation, container, false);

        view.findViewById(R.id.sign_up_layout_scroll).setOnClickListener(mOnLayoutClickListener);

        mFirstNameEditText = view.findViewById(R.id.firstName);
        mFirstNameEditTextWrapper = view.findViewById(R.id.firstNameWrapper);
        mLastNameEditText = view.findViewById(R.id.lastName);
        mLastNameEditTextWrapper = view.findViewById(R.id.lastNameWrapper);
        mEmailEditText = view.findViewById(R.id.email);
        mEmailEditTextWrapper = view.findViewById(R.id.emailWrapper);
        mPhoneEditText = view.findViewById(R.id.phone);
        mPhoneEditTextWrapper = view.findViewById(R.id.phoneWrapper);
        mPasswordEditText = view.findViewById(R.id.password);
        mPasswordEditTextWrapper = view.findViewById(R.id.passwordWrapper);

        mFirstNameEditText.addTextChangedListener(mFirstNameTextWatcher);
        mLastNameEditText.addTextChangedListener(mLastNameTextWatcher);
        mEmailEditText.addTextChangedListener(mEmailTextWatcher);
        mPhoneEditText.addTextChangedListener(mPhoneTextWatcher);
        mPasswordEditText.addTextChangedListener(mPasswordTextWatcher);


        String countryCode = Locale.getDefault().getCountry();
        mPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(countryCode));

        mFirstNameEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mLastNameEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mEmailEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mPhoneEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mPasswordEditText.setOnFocusChangeListener(mOnFocusChangeListener);

        if (mCaregiverData != null) {
            showInvitationPopup(getGuardianName(mCaregiverData));
        }

        mFirstNameEditText.setOnEditorActionListener(mOnEditorActionListener);
        mLastNameEditText.setOnEditorActionListener(mOnEditorActionListener);
        mEmailEditText.setOnEditorActionListener(mOnEditorActionListener);
        mPhoneEditText.setOnEditorActionListener(mOnEditorActionListener);
        mPasswordEditText.setOnEditorActionListener(mOnEditorActionListener);

        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        showProgressBar(false);
    }

    private String decodeJWT(String jwtToken) {
        byte[] data = Base64.decode(jwtToken, Base64.DEFAULT);
        return new String(data, StandardCharsets.UTF_8);
    }

    private String getGuardianName(String jwtToken) {
        try {
            String inviteToken = decodeJWT(jwtToken);
            JWT jwt = new JWT(inviteToken);
            return jwt.getClaim("gfn").asString() + " " + jwt.getClaim("gln").asString();
        } catch (DecodeException exception) {
            //Invalid token
            return "";
        }
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_ACCEPT_INVITATION:
                if (resultCode == RESULT_OK) {
                    mAccountType = SSManagement.TYPE_CAREGIVER;
                } else {
                    getActivity().finish();
                }
                break;
            case REQUEST_CODE_EMAIL_DUPLICATE:
                if (resultCode == RESULT_OK) {
                    startActivity(new Intent(getActivity(), LoginActivity.class));
                }
                break;
            default:
        }
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
//            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnAccountCreationListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnAccountCreationListener();
    }

    private void initOnAccountCreationListener() {
        try {
            mListener = (OnAccountCreationListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnAccountCreationListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void showInfoMsg(int id, boolean hasFocus) {
        switch (id) {
            case R.id.phone:
                mPhoneEditTextWrapper.showInfoMsg(hasFocus);
                break;
            case R.id.password:
                mPasswordEditTextWrapper.showInfoMsg(hasFocus);
                break;
            default:
                break;
        }
    }

    private void checkFields(int id) {
        switch (id) {
            case R.id.firstName:
                mFirstNameEditTextWrapper.showErrorMsg(mFirstNameState == STATE_FAIL1);
                break;
            case R.id.lastName:
                mLastNameEditTextWrapper.showErrorMsg(mLastNameState == STATE_FAIL1);
                break;
            case R.id.email:
                checkEmail();
                if (mEmailState == STATE_FAIL2) {
                    mEmailEditTextWrapper.setError(getString(R.string.setup_account_creation_email_error));
                } else if (mEmailState == STATE_FAIL1) {
                    mEmailEditTextWrapper.setError(getString(R.string.setup_account_creation_email_invalid));
                } else if (mEmailState == STATE_FAIL3) {
                    mEmailEditTextWrapper.setError(getString(R.string.setup_account_creation_email_used));
                } else {
                    mEmailEditTextWrapper.showErrorMsg(false);
                }
                break;
            case R.id.phone:
                checkPhone();
                if (mPhoneState == STATE_FAIL2) {
                    mPhoneEditTextWrapper.setError(getString(R.string.setup_account_creation_phone_error));
                } else if (mPhoneState == STATE_FAIL1) {
                    mPhoneEditTextWrapper.setError(getString(R.string.setup_account_creation_phone_invalid));
                } else if (mPhoneState == STATE_FAIL3) {
                    mPhoneEditTextWrapper.setError(getString(R.string.setup_account_creation_phone_used));
                } else {
                    mPhoneEditTextWrapper.showErrorMsg(false);
                }

                break;
            case R.id.password:
                checkPassword();
                if (mPasswordState == STATE_PASS) {
                    mPasswordEditTextWrapper.showErrorMsg(false);
                } else if (mPasswordState == STATE_FAIL2) {
                    mPasswordEditTextWrapper.setError(getString(R.string.setup_account_creation_password_short));
                } else if (mPasswordState == STATE_FAIL1) {
                    mPasswordEditTextWrapper.setError(getString(R.string.setup_account_creation_password_long));
                } else {
                    mPasswordEditTextWrapper.setError(getString(R.string.setup_account_creation_password_error));
                }

                break;
        }
    }

    private void showInvitationPopup(String guardianName) {
        CaregiverAcceptInvitationDialogFragment dialogFragment = CaregiverAcceptInvitationDialogFragment.newInstance(guardianName);
        dialogFragment.setTargetFragment(SetupAccountCreationFragment.this, REQUEST_ACCEPT_INVITATION);
        dialogFragment.show(getFragmentManager(), null);
    }

    private void enableIfReady() {
        if (mListener != null) mListener.onActionButtonEnabled(isStatesAllPass());
    }

    private boolean isStatesAllPass() {
        return mFirstNameState == STATE_PASS &&
                mLastNameState == STATE_PASS &&
                mEmailState == STATE_PASS &&
                mPhoneState == STATE_PASS &&
                mPasswordState == STATE_PASS;
    }

    private void checkFirstName() {
        mFirstNameState = mFirstNameEditText.getText().length() == 0 ? STATE_FAIL1 : STATE_PASS;
    }

    private void checkLastName() {
        mLastNameState = mLastNameEditText.getText().length() == 0 ? STATE_FAIL1 : STATE_PASS;
    }

    private void checkEmail() {
        if (mEmailEditText.getText().length() == 0) {
            mEmailState = STATE_FAIL2;
        } else if (!android.util.Patterns.EMAIL_ADDRESS.matcher(mEmailEditText.getText()).matches()) {
            mEmailState = STATE_FAIL1;
        } else {
            mEmailState = STATE_PASS;
        }
    }

    private void checkPhone() {
        if (mPhoneEditText.getText().length() == 0) {
            mPhoneState = STATE_FAIL2;
        } else if (!Utils.isPhoneValid(mPhoneEditText.getText().toString())) {
            mPhoneState = STATE_FAIL1;
        } else {
            mPhoneState = STATE_PASS;
        }
    }

    private void checkPassword() {
        int length = mPasswordEditText.getText().length();
        if (length == 0) {
            mPasswordState = STATE_FAIL3;
        } else if (length < PASSWORD_MIN) {
            mPasswordState = STATE_FAIL2;
        } else if (length > PASSWORD_MAX) {
            mPasswordState = STATE_FAIL1;
        } else {
            mPasswordState = STATE_PASS;
        }
    }

    public void createAccount() {
        final String firstName = mFirstNameEditText.getText().toString();
        final String lastName = mLastNameEditText.getText().toString();
        final String email = mEmailEditText.getText().toString();
        final String phone = Utils.formatPhone(mPhoneEditText.getText().toString(), false);
        final String password = mPasswordEditText.getText().toString();
        final String inviteToken = mCaregiverData != null ? decodeJWT(mCaregiverData) : "";

        new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {
            SSError mError;

            @Override
            protected void onPreExecute() {
                showProgressBar(true);
            }

            @Override
            protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                try {
                    return SKManagement.createUser(email, firstName, lastName, mAccountType, password, phone, inviteToken);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.UserAccountInfo userAccountInfo) {
//                showProgressBar(false);
                if (userAccountInfo != null) {
                    AccountManagement accountManagement = AccountManagement.getInstance(getActivity());
                    accountManagement.writeUserAccountInfo(userAccountInfo);
                    accountManagement.writePassword(password);
                    //update the baby age after its added
                    Utils.setMixpanelUserProfile();
                    if (TextUtils.isEmpty(inviteToken)) {
                        Utils.logEvents(LogEvents.GUARDIAN_REGISTERED);
                    } else {
                        Utils.logEvents(LogEvents.CARE_GIVER_REGISTERED);
                    }
                    if (!BuildConfig.FLAVOR_app.equals(FISHER_PRICE_CHINA_FLAVOR)) {
                        Core.login(userAccountInfo.email, userAccountInfo.firstName, userAccountInfo.email);
                    }

                    login(email, password);
                } else {
                    showProgressBar(false);
                    if (mListener != null) mListener.onAccountCreated(false, mAccountType);
                    handleError(mError);
                }
            }
        }.execute();
    }

    private void loginIdentity(String email, String password) {
        final LoginRequestBody requestBody = new LoginRequestBody(email, password);
        showProgressBar(true);
        SproutlingApi.login(requestBody, new Callback<LoginResponse>() {
            @Override
            public void onResponse(Call<LoginResponse> call, Response<LoginResponse> response) {
                showProgressBar(false);
                if (response.isSuccessful()) {
                    LoginResponse loginResponse = response.body();
                    AccountManagement.getInstance(getActivity()).saveUserAccount(loginResponse);
                    if (mListener != null) mListener.onAccountCreated(true, mAccountType);
                }
            }

            @Override
            public void onFailure(Call<LoginResponse> call, Throwable t) {
                showProgressBar(false);
            }
        });
    }

    private void login(final String email, final String password) {
        new AsyncTask<Void, Void, SSManagement.User>() {
            SSError mError;

            @Override
            protected SSManagement.User doInBackground(Void... params) {
                try {
                    return SKManagement.loginByPassword(email, password);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.User user) {
//                showProgressBar(false);
                if (user != null) {
                    AccountManagement.getInstance(getActivity()).writeToPreferences(user);

                    Utils.logEvents(LogEvents.LOGGED_IN);
                    mListener.onUserLoggedIn();
//                    if (mListener != null) mListener.onAccountCreated(true, mAccountType);
                    updateAlert();
                } else {
                    showProgressBar(false);
                    if (mError != null) handleError(mError);
                }
            }
        }.execute();
    }

    private void updateAlert() {
        AccountManagement account = AccountManagement.getInstance(getActivity());
        final SSManagement.User user = account.getUser();
        final SSManagement.UserAccountInfo userAccountInfo = account.getUserAccountInfo();

        new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {
            SSError mError;

//            @Override
//            protected void onPreExecute() {
//                showProgressBar(true);
//            }

            @Override
            protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                try {
                    return SKManagement.updateAlert(user.accessToken, userAccountInfo.id, SSManagement.PushNotification.getDefaultSettingsAPIJSON());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.UserAccountInfo userAccountInfo) {
//                showProgressBar(false);
                if (userAccountInfo != null) {
                    AccountManagement.getInstance(getActivity()).writeUserAccountInfo(userAccountInfo);
                    if (mListener != null) mListener.onAccountCreated(true, mAccountType);
                } else {
                    showProgressBar(false);
                    if (mError != null) handleError(mError);
                }
            }
        }.execute();
    }

    private void handleError(SSError error) {
        if (error == null) {
            GenericErrorDialogFragment.newInstance()
                    .show(getFragmentManager(), null);
        } else if (error.code == SSError.Taken) {
            if (error.message.contains("email") || error.path.contains("email")) {
                showEmailTaken();
            } else if (error.message.contains("email") || error.path.contains("phone")) {
                showPhoneTaken();
            }
        }
    }

    private void showEmailTaken() {
        mEmailEditText.setError(getString(R.string.setup_account_creation_email_used));
        mEmailState = STATE_FAIL3;

        EmailRegisteredDialogFragment dialogFragment = EmailRegisteredDialogFragment.newInstance();
        dialogFragment.setTargetFragment(SetupAccountCreationFragment.this, REQUEST_CODE_EMAIL_DUPLICATE);
        dialogFragment.show(getFragmentManager(), null);
    }

    private void showPhoneTaken() {
        mPhoneEditText.setError(getString(R.string.setup_account_creation_phone_used));

        mPhoneState = STATE_FAIL3;
    }

    public interface OnAccountCreationListener {
        void onActionButtonEnabled(boolean enabled);

        void onAccountCreated(boolean created, String accountType);

        void performAccountCreation();

        void onUserLoggedIn();
    }
}
