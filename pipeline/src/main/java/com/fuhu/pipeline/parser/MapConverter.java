package com.fuhu.pipeline.parser;


import com.fuhu.pipeline.internal.PipeLog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class MapConverter {
    private static final String TAG = MapConverter.class.getSimpleName();
    private static final boolean isDebug = false;
    
    /**
     * Covert JSONObject to Map<String, Object>
     * JSONArray can be convert to ArrayList<Map<String, Object>>
     *
     * @param json JSONObject
     * @return Map object
     */
    public static Map<String, Object> toMap(final JSONObject json) {
        final Map<String, Object> map = new HashMap<>();

        if (json != null) {
            final Iterator<String> iterator = json.keys();

            if (iterator != null) {
                while (iterator.hasNext()) {
                    final String key = iterator.next();
                    // Check if the value is null.
                    if (json.isNull(key)) {
                        map.put(key, null);
                        continue;
                    }

                    // Fine the type of object.
                    final Object value = json.opt(key);
                    PipeLog("key: " + key + " value: " + value);

                    if (value instanceof JSONObject) {
                        PipeLog("type: JSONObject");
                        map.put(key, toMap((JSONObject) value));
                    } else if (value instanceof JSONArray) {
                        PipeLog("type: JSONArray");
                        map.put(key, toMap((JSONArray) value));
                    } else {
                        PipeLog("type: Primitive type");
                        map.put(key, value);
                    }
                }
            }
        }
//        PipeLog("convert: " + map.toString());
        return map;
    }

    /**
     * Convert JSONArray to ArrayList<Map<String, Object>>
     *
     * @param array JSONArray
     * @return ArrayList
     */
    public static ArrayList<Object> toMap(final JSONArray array) {
        final ArrayList<Object> maps = new ArrayList<>();
        if (array != null) {
            for (int i = 0, size = array.length(); i < size; i++) {
                JSONObject item = array.optJSONObject(i);

                PipeLog("item: " + item);
                // Check if object type is JSONObject
                if (item != null) {
                    maps.add(toMap(item));
                } else {
                    // primitive types
                    maps.add(array.opt(i));
                }
            }
        }
        return maps;
    }

    /**
     * Covert Map<String, Object> to JSONObject
     * @param message message
     * @return JSONObject
     */
    public static JSONObject toJSON(final Map<String, Object> message, final JsonObjWrapper jsonWrapper) throws JSONException {
        final JSONObject jsonObject = new JSONObject();

        if (message != null || !message.isEmpty()) {
            final Iterator<String> iterator = message.keySet().iterator();

            while (iterator.hasNext()) {
                final String key = iterator.next();
                jsonObject.put(key, jsonWrapper.wrap(message.get(key)));
            }
        } else {
            PipeLog("map is null.");
        }
        return jsonObject;
    }

    private static void PipeLog(final String log) {
        if (isDebug) {
            PipeLog.d(TAG, log);
        }
    }
}
