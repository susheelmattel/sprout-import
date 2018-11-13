package com.fuhu.states;

import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStateReducer;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by moi0312 on 2016/11/30.
 */

public class CombinedReducer implements IStateReducer {

	protected List<IStateReducer> reducers = new ArrayList<>();

	public CombinedReducer(List<IStateReducer> reducerList) {
		this.reducers = reducerList;
	}

	/**
	 * pass in instances of IStateReducer. <br/>
	 * If your reducers' constructors aare all default (no param), <br/>
	 * you can use another constructor of CombinedReducer which can new the reducer instances for you.
	 * @param reducers
	 */
	public CombinedReducer(IStateReducer[] reducers) {
		this.reducers = new ArrayList<>();
		for(int i=0; i<reducers.length; i++){
			this.reducers.add(reducers[i]);
		}
	}

	/**
	 * just pass in classes of IStateReducer then <br/>
	 * CombinedReducer will find their none param constructor, new the sInstance, <br/>
	 * and add to this CombinedReducer.
	 * @param reducerClasses    class extends IStateReducer
	 */
	@SafeVarargs
	public CombinedReducer(Class<? extends IStateReducer>... reducerClasses) {
		try {
			this.reducers = new ArrayList<>();
			for(int i=0; i<reducerClasses.length; i++){
				Constructor<?> ctr = reducerClasses[i].getConstructor(null);
				IStateReducer stateReducer = (IStateReducer)ctr.newInstance(new Object[] { });
				this.reducers.add(stateReducer);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Override
	public IState reduce(Action action, IState prevState) {
		if(prevState == null) {
			prevState = new State();
		}
		IState state = new State();
		for (IStateReducer reducer : reducers) {
			IState tempState = reducer.reduce(action, prevState);
			if(tempState != null){
				state.update(tempState.data());
			}
		}
//		Log.v("states", "CombinedReducer.reduce: "+state.data().toString());
		return state;
	}
}
