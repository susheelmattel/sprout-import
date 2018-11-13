/*
 * Copyright (C) 2017 Mattel, Inc. All rights reserved.
 */

package com.sproutling.states;

import com.fuhu.states.CombinedReducer;
import com.fuhu.states.Store;

/**
 * Created by moi0312 on 2017/1/25.
 */

public class SproutlingStore extends Store {

	/**
	 * create a Store sInstance and return.<br/>
	 * (if sInstance already exist, re-init attributes before return)
	 *
	 * @param reducer
	 * @return
	 */
	public static SproutlingStore createStore(CombinedReducer reducer) {
		if (sInstance == null) {
			sInstance = new SproutlingStore();
			if (reducer != null) {
				((SproutlingStore) sInstance).replaceReducer(reducer);
			}
		}
		return (SproutlingStore) sInstance;
	}

//	public void dispatchNightLightSwitch(boolean isOn) {
//		super.dispatch(new Action(Actions.SWITCH_NIGHT_LIGHT, new Payload().put(Actions.Key.IS_NIGHT_LIGHT_ON, isOn)));
//	}
//
//	public void dispatchNightLightLevel(float level) {
//		super.dispatch(new Action(Actions.SET_NIGHT_LIGHT_LEVEL, new Payload().put(Actions.Key.NIGHT_LIGHT_LEVEL, level)));
//	}
//
//	public void dispatchNightLightColor(int colorType) {
//		super.dispatch(new Action(Actions.SET_NIGHT_LIGHT_COLOR, new Payload().put(Actions.Key.NIGHT_LIGHT_COLOR, colorType)));
//	}
//
//	public void dispatchMusicSwitch(boolean isPlay) {
//		super.dispatch(new Action(Actions.SWITCH_MUSIC_PLAY, new Payload().put(Actions.Key.IS_MUSIC_PLAY, isPlay)));
//	}
//
//	public void dispatchMusicVol(float musicVol) {
//		super.dispatch(new Action(Actions.SET_MUSIC_VOL, new Payload().put(Actions.Key.MUSIC_VOL, musicVol)));
//	}
}
