package com.fuhu.states.interfaces;

import com.fuhu.states.action.Action;

/**
 * Created by moi0312 on 2016/11/30.
 */
public interface IStateReducer {
	IState reduce(Action action, IState state);
}
