/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.dialogfragment.CaregiverRemoveDialogFragment;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.Utils;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;

import static android.app.Activity.RESULT_OK;
import static com.sproutling.utils.LogEvents.CARE_GIVER_REMOVED;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsCaregiversDetailFragment extends Fragment {
    public static final String TAG = "SettingsCaregiversDetailFragment";

    public static final String EXTRA_CAREGIVER_JSON = "caregiver_json";
    public static final String EXTRA_CHILD_NAME = "child_name";

    private static final int REQUEST_CODE_REMOVE = 1;

    private OnCaregiverDetailListener mListener;

    private ShTextView mFirstNameView, mLastNameView, mPhoneView, mRemoveView;

    private SSManagement.UserAccountInfo mCaregiver;
    private String mChildName;

    public static SettingsCaregiversDetailFragment newInstance(SSManagement.UserAccountInfo caregiver, String childName) {
        SettingsCaregiversDetailFragment fragment = new SettingsCaregiversDetailFragment();
        Bundle arguments = new Bundle();
        arguments.putString(EXTRA_CAREGIVER_JSON, caregiver.toString());
        arguments.putString(EXTRA_CHILD_NAME, childName);
        fragment.setArguments(arguments);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mCaregiver = new SSManagement.UserAccountInfo();
        Bundle arguments = getArguments();
        if (arguments != null) {
            try {
                mCaregiver = new SSManagement.UserAccountInfo(new JSONObject(arguments.getString(EXTRA_CAREGIVER_JSON)));
            } catch (JSONException e) {
                e.printStackTrace();
            }
            mChildName = arguments.getString(EXTRA_CHILD_NAME);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_caregivers_detail, container, false);

        mFirstNameView = (ShTextView) view.findViewById(R.id.first_name);
        mLastNameView = (ShTextView) view.findViewById(R.id.last_name);
        mPhoneView = (ShTextView) view.findViewById(R.id.phone);
        view.findViewById(R.id.remove).setOnClickListener(mOnRemoveClickListener);

        initUI();

        return view;
    }

    private void initUI() {
        mFirstNameView.setText(mCaregiver.firstName);
        mLastNameView.setText(mCaregiver.lastName);
        mPhoneView.setText(mCaregiver.phone);
    }

    private View.OnClickListener mOnRemoveClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            CaregiverRemoveDialogFragment dialogFragment = CaregiverRemoveDialogFragment.newInstance(mCaregiver.firstName, mChildName);
            dialogFragment.setTargetFragment(SettingsCaregiversDetailFragment.this, REQUEST_CODE_REMOVE);
            dialogFragment.show(getFragmentManager(), null);
        }
    };

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_REMOVE:
                if (resultCode == RESULT_OK) {
                    removeCaregiver();
                }
                break;
            default:
        }
    }

    public void onButtonPressed(Uri uri) {
//        if (mListener != null) {
//        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        initListener();
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        initListener();
    }

    private void initListener() {
        try {
            mListener = (OnCaregiverDetailListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnCaregiverDetailListener");
        }
    }


    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCaregiverDetailListener {
        void onCaregiverRemoved(boolean removed);
    }

    public void removeCaregiver() {
        new AsyncTask<Void, Void, Boolean>() {
            SSError mError;

            @Override
            protected Boolean doInBackground(Void... params) {
                try {
                    SSManagement.User account = AccountManagement.getInstance(getActivity()).getUser();
                    return SKManagement.removeCaregiver(account.accessToken, mCaregiver.accountId, mCaregiver.id);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }

            @Override
            protected void onPostExecute(Boolean removed) {
                Utils.logEvents(CARE_GIVER_REMOVED);
                if (mListener != null) mListener.onCaregiverRemoved(removed);
            }
        }.execute();
    }

    private void handleError(SSError error) {

    }
}

