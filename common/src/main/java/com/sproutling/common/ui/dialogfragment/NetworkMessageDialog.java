package com.sproutling.common.ui.dialogfragment;

import com.sproutling.common.R;

import org.jetbrains.annotations.NotNull;

public class NetworkMessageDialog extends BaseDialogFragment {


    @Override
    public int getImage() {
        return R.drawable.ic_internet_offline;
    }

    @NotNull
    @Override
    public String getTitle() {
        return getString(R.string.internet_offline_title);
    }

    @NotNull
    @Override
    public String getMessage() {
        return getString(R.string.internet_offline_message);
    }

    @NotNull
    @Override
    public String getButtonText() {
        return getString(R.string.try_again);
    }

    @Override
    public boolean getButton2Visibility() {
        return false;
    }

    @NotNull
    @Override
    public String getButton2Text() {
        return getString(R.string.internet_offline_button);
    }

}
