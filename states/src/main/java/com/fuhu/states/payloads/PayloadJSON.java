package com.fuhu.states.payloads;

import com.fuhu.states.interfaces.IStatePayload;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by moi0312 on 2016/12/8.
 */

public class PayloadJSON extends AKeyValueStatePayload {
	private JSONObject data;

	public PayloadJSON() {
		try {
			this.data =  new JSONObject("{}");
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	public PayloadJSON(JSONObject data) {
		this.data = data;
	}

	public PayloadJSON(JSONObject stateObj, String[] nameStrings) {
		try {
			data = new JSONObject(stateObj, nameStrings);
		} catch (JSONException e) {
			e.printStackTrace();
		}
	}

	@Override
	public String[] getKeys() {
		JSONArray arr = this.data.names();
		if(arr != null){
			String[] keys = new String[arr.length()];
			try {
				for(int i=0; i< arr.length(); i++){
					keys[i] = arr.getString(i);
				}
			} catch (JSONException e) {
				e.printStackTrace();
			}
			return keys;
		}else {
			return new String[0];
		}
	}

	@Override
	public PayloadJSON duplicate() {
		String[] names = getKeys();
		return new PayloadJSON(this.data, names);
	}

	@Override
	public PayloadJSON update(IStatePayload payload) {
		if(payload != null && payload instanceof PayloadJSON){
			PayloadJSON pl = (PayloadJSON)payload;
			if(!pl.isEmpty()) {
				String[] names = pl.getKeys();
				for (int i = 0; i < names.length; i++) {
					this.put(names[i], pl.get(names[i]));
				}
			}
		}
		return this;
	}

	@Override
	public boolean isEmpty() {
		if(data == null || data.length()==0){
			return true;
		}
		return false;
	}

	@Override
	public void remove(String key) {
		this.data.remove(key);
	}
	@Override
	public Object get(String k) {
		try {
			return data.get(k);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public String getString(String k) {
		try {
			return data.getString(k);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return null;
	}
	@Override
	public boolean getBoolean(String k, boolean defaultValue) {
		try {
			return data.getBoolean(k);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	@Override
	public int getInt(String k, int defaultValue) {
		try {
			return data.getInt(k);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	@Override
	public double getDouble(String k, double defaultValue) {
		try {
			return data.getDouble(k);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}

	@Override
	public float getFloat(String k, float defaultValue) {
		return (float)getDouble(k, defaultValue);
	}

	@Override
	public long getLong(String k, long defaultValue) {
		try {
			return data.getLong(k);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return defaultValue;
	}
	@Override
	public PayloadJSON put(String k, String v) {
		try {
			data = data.put(k,v);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}
	@Override
	public PayloadJSON put(String k, Object v) {
		try {
			data = data.put(k,v);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}
	@Override
	public PayloadJSON put(String k, boolean v) {
		try {
			data = data.put(k,v);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}
	@Override
	public PayloadJSON put(String k, int v) {
		try {
			data = data.put(k,v);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}
	@Override
	public PayloadJSON put(String k, double v) {
		try {
			data = data.put(k,v);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public PayloadJSON put(String k, float v) {
		try {
			data = data.put(k,v);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}

	@Override
	public PayloadJSON put(String k, long v) {
		try {
			data = data.put(k,v);
		} catch (JSONException e) {
			e.printStackTrace();
		}
		return this;
	}
}
