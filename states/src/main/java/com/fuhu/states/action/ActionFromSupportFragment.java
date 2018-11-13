package com.fuhu.states.action;

import android.support.v4.app.Fragment;

import com.fuhu.states.interfaces.IStatePayload;

/**
 * Created by moi0312 on 2017/1/13.
 */

public class ActionFromSupportFragment extends Action {

	public ActionFromSupportFragment(int type, Fragment dispatcher, IStatePayload payload) {
		super(type, dispatcher, payload);
	}

	@Override
	public Fragment getDispatcher() {
		return (Fragment)this.dispatcher;
	}
}
