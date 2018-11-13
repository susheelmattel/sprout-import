package com.fuhu.pipetest.states;

import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStateReducer;

/**
 * Created by moi0312 on 2016/12/5.
 */

public class TestReducer1 implements IStateReducer {
	@Override
	public IState reduce(Action action, IState state) {
//		boolean isAdd = false;
//		switch (action.getType()){
//			case Actions.S2_PLUS:
//				isAdd = true;
//			case Actions.S2_MINUS:
//				IStatePayload payload = state.payload();
//				int s2Value = payload.getInt(States.KEY_S2);
//				if(isAdd){
//					s2Value++;
//				}else{
//					s2Value--;
//				}
//				payload.put(States.KEY_S2, s2Value);
//				return state.update(payload);
//		}
		return null;
	}
}
