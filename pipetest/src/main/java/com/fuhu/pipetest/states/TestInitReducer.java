package com.fuhu.pipetest.states;

import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStateReducer;

/**
 * Created by moi0312 on 2016/12/5.
 */

public class TestInitReducer implements IStateReducer {

	@Override
	public IState reduce(Action action, IState state) {
		switch (action.getType()){
			case Actions.TYPE_INIT:
				return state.update(action.payload());
		}
		return null;
	}
}
