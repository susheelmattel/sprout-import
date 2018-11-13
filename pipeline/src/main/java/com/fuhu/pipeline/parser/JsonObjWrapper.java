package com.fuhu.pipeline.parser;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Array;
import java.util.Collection;
import java.util.Map;

public class JsonObjWrapper {
    private static JsonObjWrapper INSTANCE;

    /**
     * Default constructor.
     */
    private JsonObjWrapper() {}

    /**
     * Get an instance of JSONWrapper.
     * @return JSONWrapper
     */
    public static JsonObjWrapper getInstance() {
        if (INSTANCE == null){
            synchronized(JsonObjWrapper.class) {
                if(INSTANCE == null) {
                    INSTANCE = new JsonObjWrapper();
                }
            }
        }
        return INSTANCE;
    }
    
    /**
     * Covert Object to JSONObject or JSONArrary*
     * @param object Object
     * @return JSONObject or JSONArray
     */
    public Object wrap(Object object) {
        if (object == null) {
            return JSONObject.NULL;
        }
        if (object instanceof JSONArray || object instanceof JSONObject) {
            return object;
        }
        if (object.equals(JSONObject.NULL)) {
            return object;
        }

        try {
            // Checks if object type is Collection
            if (object instanceof Collection) {
                return new JSONArray((Collection) object);
            } else if (object.getClass().isArray()) {
                return toJSONArray(object);
            }

            // Checks if object type is Map
            if (object instanceof Map) {
                return new JSONObject((Map) object);
            }

            // Checks if object type is primitive type
            if (object instanceof Boolean ||
                    object instanceof Byte ||
                    object instanceof Character ||
                    object instanceof Double ||
                    object instanceof Float ||
                    object instanceof Integer ||
                    object instanceof Long ||
                    object instanceof Short ||
                    object instanceof String) {
                return object;
            }
        } catch (Exception ignored) {
            ignored.printStackTrace();
        }
        return null;
    }

    /**
     * Covert Object to JSONOArray
     * JSONArray can be convert to ArrayList<Bundle>
     *
     * @param array Object
     * @return JSONArray
     */
    public JSONArray toJSONArray(Object array) throws JSONException {
        JSONArray result = new JSONArray();
        if (!array.getClass().isArray()) {
            throw new JSONException("Not a primitive array: " + array.getClass());
        }
        final int length = Array.getLength(array);
        for (int i = 0; i < length; ++i) {
            result.put(wrap(Array.get(array, i)));
        }
        return result;
    }

    /**
     * Destroy object.
     */
    public static void release() {
        INSTANCE = null;
    }
}
