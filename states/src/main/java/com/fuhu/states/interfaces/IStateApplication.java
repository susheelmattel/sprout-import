package com.fuhu.states.interfaces;

import com.fuhu.states.Store;

/**
 * Created by moi0312 on 2016/11/29.
 */

public interface IStateApplication {
	Store getStore();
	void dispatchAction(int type, IStatePayload payload);
	void dispatchAction(int type, IStatePayload payload, Object dispatcher);
}
