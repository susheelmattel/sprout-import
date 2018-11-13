/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.states;

import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStateReducer;
import com.fuhu.states.payloads.Payload;
import com.sproutling.ui.activity.StatusActivity;

import static com.sproutling.ui.dialog.IntegrationDialog.ROLL_OVER;

/**
 * Created by Xylon on 2017/1/16.
 */

public class DialogReducer implements IStateReducer {
	@Override
	public IState reduce(Action action, IState state) {

		Payload actPayload = (Payload) action.payload(); //action's payload

		switch (action.getType()) {
			case Actions.CALL_DIALOG:
				if (StatusActivity.sInstance != null) {
					StatusActivity.sInstance.showAlarmDialog(actPayload.getInt(Actions.Key.DIALOG, ROLL_OVER));
				}
				return null;

			case Actions.CALL_NOTIFICATION:
				if (StatusActivity.sInstance != null) {
					StatusActivity.sInstance.showNotification(actPayload.getString(Actions.Key.NOTIFICATION));
				}
				return null;
		}
		return null;
	}
}