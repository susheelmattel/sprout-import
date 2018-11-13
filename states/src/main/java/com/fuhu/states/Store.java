package com.fuhu.states;

import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IState;
import com.fuhu.states.interfaces.IStateListener;
import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.interfaces.IStateReducer;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by moi0312 on 2016/11/29.
 */

public class Store {

	protected IState appState = new State();
	protected IStateReducer reducer;
	protected List<IStateListener> subscription = new ArrayList<>();

	protected static Store sInstance;

	/**
	 * create a Store sInstance and return.<br/>
	 * (if sInstance already exist, re-init attributes before return)
	 * @param reducer
	 * @return
	 */
	public static Store createStore(CombinedReducer reducer){
		if(sInstance == null){
			sInstance = new Store();
			if(reducer != null){
				sInstance.replaceReducer(reducer);
			}
		}
		return sInstance;
	}

//	/**
//	 * duplicate app state and return it.
//	 * @return a copy of app state
//	 */
//	public State getState(){
//		return this.appState.duplicate();
//	}
	public IStatePayload getStateData(){
		return this.appState.duplicate().data();
	}

	public boolean isStateEmpty(){
		return appState.data().isEmpty();
	}

	/**
	 * register a stateListener so it will be notify if state changed.
	 * @param stateListener
	 */
	public void subscribe(IStateListener stateListener){
		int index = subscription.indexOf(stateListener);
		if(index<0){
			subscription.add(stateListener);
		}
	}

	public boolean unSubscribe(IStateListener stateListener){
		return subscription.remove(stateListener);
	}

	protected void replaceReducer(CombinedReducer nextReducer) {
		this.reducer = nextReducer;
	}

	/**
	 * should be the only way to send action to reducer
	 * @param action
	 */
	synchronized public void dispatch(final Action action){
		State newState = (State)reducer.reduce(action, this.appState.duplicate());
		this.notifyStateChanged(newState.data());
	}

	public void dispatch(final Action action, Object dispatcher){
		if(action != null ){
			action.setDispatcher(dispatcher);
			dispatch(action);
		}
	}

	protected void notifyStateChanged(IStatePayload payload){
		if(!payload.isEmpty()){
//		appState = new State(payload);
			//TODO need to rewrite
			appState.update(payload);

			for (IStateListener stateListener : subscription) {
//				stateListener.onStateChanged(appState.data());
				stateListener.onStateChanged(payload);
			}

		}
	}

	public void reset() {
		appState = new State();
		sInstance = null;
	}
}