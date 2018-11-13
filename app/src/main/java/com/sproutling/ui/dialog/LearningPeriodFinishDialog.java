package com.sproutling.ui.dialog;

import android.app.Dialog;
import android.content.Context;
import android.support.annotation.NonNull;
import android.text.TextUtils;
import android.view.View;

import com.fuhu.states.payloads.Payload;
import com.sproutling.App;
import com.sproutling.R;
import com.sproutling.states.Actions;
import com.sproutling.states.States;
import com.sproutling.ui.activity.StatusActivity;
import com.sproutling.ui.widget.ShTextView;
import com.sproutling.utils.SharedPrefManager;
import com.sproutling.utils.Utils;

import static com.sproutling.utils.LogEvents.LEARNING_PERIOD_ENDED;

/**
 * Created by subram13 on 10/13/17.
 */

public class LearningPeriodFinishDialog extends Dialog {
    public LearningPeriodFinishDialog(@NonNull Context context, String childName) {
        super(context, R.style.CustomDialog);
        setContentView(R.layout.dialog_learningperiod_finish);
        setCanceledOnTouchOutside(false);
        setCancelable(false);
        findViewById(R.id.btnGotIt).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (StatusActivity.sInstance != null) {
                    StatusActivity.sInstance.setLearningPeriodCompleted(true);
                    SharedPrefManager.setLearningPeriodDone(StatusActivity.sInstance);
                    App.getInstance().dispatchAction(Actions.DATA_UPDATE, new Payload().put(States.Key.LEARNING_PERIOD_STATUS, SharedPrefManager.SPValue.LEARNING_PERIOD_ENDED));
                }
                Utils.logEvents(LEARNING_PERIOD_ENDED);
                cancel();
            }
        });

        String description = String.format(getContext().getString(R.string.learningperiod_finish_dlg_text), TextUtils.isEmpty(childName) ? getContext().getString(R.string.your_baby) : childName);
        ((ShTextView) findViewById(R.id.tv_lp_description)).setText(description);
    }
}
