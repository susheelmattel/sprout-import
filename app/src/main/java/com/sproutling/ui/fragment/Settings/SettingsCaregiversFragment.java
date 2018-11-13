/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.ui.fragment.Settings;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.RelativeLayout;

import com.sproutling.R;
import com.sproutling.services.AccountManagement;
import com.sproutling.services.SKManagement;
import com.sproutling.services.SSError;
import com.sproutling.services.SSException;
import com.sproutling.services.SSManagement;
import com.sproutling.ui.adapter.Settings.SettingsCaregiversListAdapter;
import com.sproutling.ui.adapter.Settings.SettingsCaregiversListAdapter.CaregiverItem;
import com.sproutling.ui.widget.ShTextView;

import org.json.JSONException;

import java.io.IOException;
import java.util.List;

/**
 * Created by bradylin on 12/13/16.
 */

public class SettingsCaregiversFragment extends Fragment {
    public static final String TAG = "SettingsCaregiversFragment";

    private OnCaregiverListener mListener;

    private RelativeLayout mInfoLayout;
    private ShTextView mInviteView;

    private ListView mListView;
    private SettingsCaregiversListAdapter mListAdapter;

    public static SettingsCaregiversFragment newInstance() {
        return new SettingsCaregiversFragment();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_settings_caregivers, container, false);

        mInfoLayout = (RelativeLayout) view.findViewById(R.id.info_layout);
        view.findViewById(R.id.invite).setOnClickListener(mOnInviteClickListener);

        mListView = (ListView) view.findViewById(R.id.list);
        mListAdapter = new SettingsCaregiversListAdapter(getActivity());
        mListView.setAdapter(mListAdapter);
        mListView.setOnItemClickListener(mOnListViewItemClickListener);

        getCaregiverList();

        return view;
    }

    private void initPage() {
        if (mListAdapter.getCount() == 1) {
            mInfoLayout.setVisibility(View.VISIBLE);
            mListView.setVisibility(View.INVISIBLE);
        } else {
            mInfoLayout.setVisibility(View.INVISIBLE);
            mListView.setVisibility(View.VISIBLE);
        }
    }

    private View.OnClickListener mOnInviteClickListener = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            if (mListener != null) mListener.onQRGenerateClicked();
        }
    };

    private AdapterView.OnItemClickListener mOnListViewItemClickListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            if (mListAdapter.getItem(position) instanceof CaregiverItem) {
                if (mListener != null) mListener.onCaregiverSelected(((CaregiverItem) mListAdapter.getItem(position)).caregiver);
            } else {
                if (mListener != null) mListener.onQRGenerateClicked();
            }
        }
    };

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

    private void initListener(){
        try {
            mListener = (OnCaregiverListener) getActivity();
        } catch (ClassCastException e) {
            throw new ClassCastException(getActivity().toString()
                    + " must implement OnCaregiverListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnCaregiverListener {
        void onCaregiverSelected(SSManagement.UserAccountInfo caregiver);
        void onQRGenerateClicked();
    }

    private void getCaregiverList() {
        new AsyncTask<Void, Void, List<SSManagement.UserAccountInfo>>() {
            SSError mError;
//
            @Override
            protected List<SSManagement.UserAccountInfo> doInBackground(Void... params) {
                try {
                    SSManagement.User account = AccountManagement.getInstance(getActivity()).getUser();
                    SSManagement.UserAccountInfo userAccountInfo = AccountManagement.getInstance(getActivity()).getUserAccountInfo();
                    return SKManagement.listCaregivers(account.accessToken, userAccountInfo.accountId);
                } catch (IOException | JSONException e) {
                    e.printStackTrace();
                } catch (SSException e) {
                    e.printStackTrace();
                    mError = e.getError();
                }
                return null;
            }
//
            @Override
            protected void onPostExecute(List<SSManagement.UserAccountInfo> caregivers) {
                if (caregivers != null) {
                    mListAdapter.setItems(caregivers);
                } else {
                    if (mError != null)
                        handleError(mError, R.string.settings_baby_get_error_message_body);
                }
                initPage();
            }
        }.execute();
    }

    private void handleError(SSError error, int messageId) {
//        new AlertDialog.Builder(getActivity())
//                .setTitle(R.string.settings_baby_error_message_title)
//                .setMessage(messageId)
//                .setNeutralButton(android.R.string.ok, new DialogInterface.OnClickListener() {
//                    public void onClick(DialogInterface dialog, int which) {
//                        dialog.dismiss();
//                    }
//                })
//                .show();
    }
}

