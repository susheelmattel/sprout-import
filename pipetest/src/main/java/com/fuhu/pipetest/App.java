package com.fuhu.pipetest;

import android.app.Application;
import android.content.Context;

import com.fuhu.pipeline.manager.MqttManager;
import com.fuhu.pipetest.pipeline.MqttCallbackHandler;
import com.fuhu.pipetest.pipeline.TestAPI;
import com.fuhu.pipetest.states.Actions;
import com.fuhu.pipetest.states.TestInitReducer;
import com.fuhu.states.CombinedReducer;
import com.fuhu.states.Store;
import com.fuhu.states.action.Action;
import com.fuhu.states.interfaces.IStateApplication;
import com.fuhu.states.interfaces.IStatePayload;
import com.fuhu.states.payloads.Payload;

import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;


public class App extends Application implements IStateApplication {
	private static final String TAG = App.class.getSimpleName();

	private static App instance;
	private static Store store;

	@Override
	public void onCreate() {
		super.onCreate();
		instance = this;

        // store init
        CombinedReducer reducer = new CombinedReducer(
                TestInitReducer.class
        );
        store = Store.createStore(reducer);

        //dispatch init state
        if(store.isStateEmpty()){
            Action actInit = new Action(Actions.TYPE_INIT, null, new Payload());
//            actInit.payload().put(States.INIT_TEST, "state init test");
//			actInit.payload().put(States.KEY_S2, 20);
            store.dispatch(actInit);
        }

        // initial MqttManager with client id.
        MqttCallback mqttCallback = new MqttCallbackHandler(this);
        String clientId = MqttClient.generateClientId();
        MqttManager.getInstance().init(this, TestAPI.getMqttTlsUrl(), clientId, mqttCallback);
	}

	@Override
	public Store getStore() { return store; }
	@Override
	public void dispatchAction(int type, IStatePayload payload, Object dispatcher){
		store.dispatch(new Action(type, dispatcher, payload));
	}
	@Override
	public void dispatchAction(int type, IStatePayload payload){
		store.dispatch(new Action(type, null, payload));
	}

	public static App getInstance() {
		return instance;
	}

	public static String getAppPackage() {
		return instance.getPackageName();
	}

    public static Context getAppContext() {
        return instance.getApplicationContext();
    }
}
