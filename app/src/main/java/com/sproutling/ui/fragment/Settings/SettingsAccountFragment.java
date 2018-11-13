/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.telephony.PhoneNumberFormattingTextWatcher;
import android.telephony.PhoneNumberUtils;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.helpshift.Core;
import com.sproutling.BuildConfig;
import com.sproutling.R;
import com.sproutling.api.SproutlingApi;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.dialogfragment.LogOutDialogFragment;
import com.sproutling.ui.fragment.BaseFragment;
import com.sproutling.ui.widget.CustomShEditText;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.Locale;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.app.Activity.RESULT_OK;
import static com.sproutling.App.FISHER_PRICE_CHINA_FLAVOR;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsAccountFragment extends BaseFragment {
    public static final String TAG = "SettingsAccountFragment";

    private static final int REQUEST_CODE_LOG_OUT = 1;
    private static final int STATE_PASS = 1;
    private static final int STATE_FAIL1 = 0;
    private static final int STATE_FAIL2 = -1;
    private static final int STATE_FAIL3 = -2;

    private CustomShEditText mFirstNameEditText;
    private CustomShEditText mLastNameEditText;
    private CustomShEditText mEmailEditText;
    private CustomShEditText mPhoneEditText;
    private ShTextView mPasswordView;
    private ShTextView mSignOutView;

    private int mFirstNameState, mLastNameState, mEmailState, mPhoneState;

    private OnMyAccountListener mListener;
    private SSManagement.UserAccountInfo mUserAccountInfo;
    private View.OnClickListener mOnChangePasswordClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onChangePasswordClicked();
        }
    };
    private View.OnClickListener mOnSignOutClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            LogOutDialogFragment dialogFragment = LogOutDialogFragment.newInstance();
            dialogFragment.setTargetFragment(SettingsAccountFragment.this, REQUEST_CODE_LOG_OUT);
            dialogFragment.show(getFragmentManager(), null);
        }
    };
    private View.OnFocusChangeListener mOnFocusChangeListener = new View.OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
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

    public SettingsAccountFragment() {
    }

    public static SettingsAccountFragment newInstance() {
        return new SettingsAccountFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_account, container, false);

        mFirstNameEditText = view.findViewById(R.id.first_name);
        mLastNameEditText = view.findViewById(R.id.last_name);
        mEmailEditText = view.findViewById(R.id.email);
        mPhoneEditText = view.findViewById(R.id.phone);

        mPasswordView = (ShTextView) view.findViewById(R.id.password);
        mSignOutView = (ShTextView) view.findViewById(R.id.sign_out);

        mPasswordView.setOnClickListener(mOnChangePasswordClickListener);
        mSignOutView.setOnClickListener(mOnSignOutClickListener);

        mFirstNameEditText.addTextChangedListener(mFirstNameTextWatcher);
        mLastNameEditText.addTextChangedListener(mLastNameTextWatcher);
        mEmailEditText.addTextChangedListener(mEmailTextWatcher);
        mPhoneEditText.addTextChangedListener(mPhoneTextWatcher);

        mPhoneEditText.addTextChangedListener(new PhoneNumberFormattingTextWatcher(Locale.getDefault().getCountry()));

        mFirstNameEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mLastNameEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mEmailEditText.setOnFocusChangeListener(mOnFocusChangeListener);
        mPhoneEditText.setOnFocusChangeListener(mOnFocusChangeListener);

        getAccountInfo();

        return view;
    }

    private void fillViews() {
        mFirstNameEditText.setText(mUserAccountInfo.firstName);
        mLastNameEditText.setText(mUserAccountInfo.lastName);
        mEmailEditText.setText(mUserAccountInfo.email);
        mPhoneEditText.setText(PhoneNumberUtils.formatNumber(mUserAccountInfo.phone, Locale.getDefault().getCountry()));
    }

    @Override
    public void onResume() {
        super.onResume();
        fillViews();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_LOG_OUT:
                if (resultCode == RESULT_OK && Utils.isActivityActive(getActivity())) {
                    removePushNotification();
                }
                break;
            default:
        }
    }

    private void removePushNotification() {
        showProgressBar(true);
        SproutlingApi.deleteHandheld(SharedPrefManager.getHandHeldId(getActivity()), new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {
                AccountManagement.getInstance(getActivity()).clear();
                SharedPrefManager.clear(getActivity());
                if (!BuildConfig.FLAVOR_app.equals(FISHER_PRICE_CHINA_FLAVOR)) {
                    Core.logout();
                }
                showProgressBar(false);
                if (mListener != null) mListener.onSignOut();
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e(TAG, "Error removing push notification");
                // TODO: decide what to do if fail to delete, for now, we have to logout
                AccountManagement.getInstance(getActivity()).clear();
                SharedPrefManager.clear(getActivity());
                showProgressBar(false);
                if (mListener != null) mListener.onSignOut();
            }
        }, AccountManagement.getInstance(getActivity()).getAccessToken());
    }

    private void checkFields(int id) {
        switch (id) {
            case R.id.first_name:
                mFirstNameEditText.showErrorMsg(mFirstNameState == STATE_FAIL1);

                break;
            case R.id.last_name:
                mLastNameEditText.showErrorMsg(mLastNameState == STATE_FAIL1);
                break;
            case R.id.email:
                checkEmail();
                if (mEmailState == STATE_FAIL2) {
                    mEmailEditText.setError(getString(R.string.settings_account_email_error));
                    mEmailEditText.showErrorMsg(true);
                } else if (mEmailState == STATE_FAIL1) {
                    mEmailEditText.setError(getString(R.string.settings_account_email_invalid));
                    mEmailEditText.showErrorMsg(true);
                } else if (mEmailState == STATE_FAIL3) {
                    mEmailEditText.setError(getString(R.string.settings_account_email_used));
                    mEmailEditText.showErrorMsg(true);
                } else {
                    mEmailEditText.showErrorMsg(false);
                }
                break;
            case R.id.phone:
                checkPhone();
                if (mPhoneState == STATE_FAIL2) {
                    mPhoneEditText.setError(getString(R.string.settings_account_phone_error));
                    mPhoneEditText.showErrorMsg(true);
                } else if (mPhoneState == STATE_FAIL1) {
                    mPhoneEditText.setError(getString(R.string.settings_account_phone_invalid));
                    mPhoneEditText.showErrorMsg(true);
                } else if (mPhoneState == STATE_FAIL3) {
                    mPhoneEditText.setError(getString(R.string.settings_account_phone_used));
                    mPhoneEditText.showErrorMsg(true);
                } else {
                    mPhoneEditText.showErrorMsg(false);
                }

                break;
        }
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

    private void enableIfReady() {
        if (mListener != null) mListener.onActionButtonEnabled(TAG, mFirstNameState == STATE_PASS &&
                mLastNameState == STATE_PASS &&
                mEmailState == STATE_PASS &&
                mPhoneState == STATE_PASS);
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initOnMyAccountListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initOnMyAccountListener();
    }

    private void initOnMyAccountListener() {
        try {
            mListener = (OnMyAccountListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnMyAccountListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void getAccountInfo() {
        SSManagement.UserAccountInfo userAccountInfo = AccountManagement.getInstance(getActivity()).getUserAccountInfo();
        if (userAccountInfo != null) {
            mUserAccountInfo = userAccountInfo;
            fillViews();
        } else if (AccountManagement.getInstance(getActivity()).isLoggedIn()) {
            new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {
                SSError mError;

                @Override
                protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                    try {
                        SSManagement.User account = AccountManagement.getInstance(getActivity()).getUser();
                        return SKManagement.getUserById(account.accessToken, account.resourceOwnerId);
                    } catch (IOException | JSONException e) {
                        e.printStackTrace();
                    } catch (SSException e) {
                        e.printStackTrace();
                        mError = e.getError();
                    }
                    return null;
                }

                @Override
                protected void onPostExecute(SSManagement.UserAccountInfo accountInfo) {
                    if (accountInfo != null) {
                        mUserAccountInfo = accountInfo;
                        fillViews();
                    } else {
                        if (mError != null) handleError(mError);
                    }
                }
            }.execute();
        }
    }

    public void updateAccount() {
        new AsyncTask<Void, Void, SSManagement.UserAccountInfo>() {
            SSError mError;

            @Override
            protected SSManagement.UserAccountInfo doInBackground(Void... params) {
                try {
                    SSManagement.User account = AccountManagement.getInstance(getActivity()).getUser();
                    return SKManagement.updateUserById(account.accessToken, mUserAccountInfo.id, getUpdatedUserJSON());
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(SSManagement.UserAccountInfo accountInfo) {
                if (accountInfo != null) {
                    mUserAccountInfo = accountInfo;
                    AccountManagement.getInstance(getActivity()).writeUserAccountInfo(accountInfo);
                    Utils.setMixpanelUserProfile();
                    fillViews();
                    if (mListener != null) mListener.onAccountUpdated(true);
                } else {
                    if (mError != null) handleError(mError);
                }
            }
        }.execute();
    }

    private JSONObject getUpdatedUserJSON() throws JSONException {
        return new JSONObject()
                .put("first_name", mFirstNameEditText.getText())
                .put("last_name", mLastNameEditText.getText())
                .put("email", mEmailEditText.getText())
                .put("phone_number", Utils.formatPhone(mPhoneEditText.getText().toString(), false));
    }

    private void handleError(SSError error) {
        // TODO: change message
        new AlertDialog.Builder(getActivity())
                .setTitle(R.string.settings_account_error_message_title)
                .setMessage(R.string.settings_account_error_message_body)
                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                    }
                })
                .show();
    }

    public interface OnMyAccountListener {
        void onActionButtonEnabled(String tag, boolean enabled);

        void onChangePasswordClicked();

        void onSignOut();

        void onAccountUpdated(boolean updated);
    }
}