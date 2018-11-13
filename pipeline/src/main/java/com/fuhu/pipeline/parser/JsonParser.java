package com.fuhu.pipeline.parser;

import com.fuhu.pipeline.contract.APipeItem;
import com.fuhu.pipeline.internal.PipeLog;
import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class JsonParser {
    private static final String TAG = JsonParser.class.getSimpleName();
    private static Gson mGson;

    /**
     * Get a Gson object (Lazy initialization)
     * @return gson
     */
    public static Gson getGson() {
        if (mGson == null) {
            mGson = new Gson();
        }
        return mGson;
    }

    /**
     * Convert JSONObject to PipeItem using Gson
     * @param jsonObject origin JSONObject
     * @param obj PipeItem class
     * @param <T> the type of the PipeItem object
     * @return
     */
    public static <T> T fromJSON(final JSONObject jsonObject, final Class<T> obj) throws JsonSyntaxException {
        if (jsonObject != null && jsonObject.toString() != null && obj != null) {
            return getGson().fromJson(jsonObject.toString(), obj);
        }
        return null;
    }

    /**
     * Convert JSONArray to PipeItem using Gson
     * @param jsonArray origin JSONArray
     * @param obj PipeItem class
     * @param <T> the type of the PipeItem object
     * @return
     */
    public static <T> T[] fromJSON(final JSONArray jsonArray, final Class<T[]>  obj) throws JsonSyntaxException {
        if (jsonArray != null && jsonArray.toString() != null && obj != null) {
            return getGson().fromJson(jsonArray.toString(), obj);
        }
        return null;
    }

    /**
     * Convert JSON String to PipeItem using Gson
     * @param jsonString origin JSON String
     * @param obj PipeItem class
     * @param <T> the type of the PipeItem object
     * @return
     */
    public static <T> T fromJSON(final String jsonString, final Class<T> obj) throws JsonSyntaxException {
        return getGson().fromJson(jsonString, obj);
    }

    /**
     * Convert all key-value pairs of the PipeItem to JSONObject
     * @param pipeItem PipeItem
     * @return
     */
    public static JSONObject toJSON(final APipeItem pipeItem) throws JSONException{
        if (pipeItem != null) {
            return pipeItem.toJSONObject(getGson());
        } else {
            PipeLog.d(TAG, "pipeItem is null");
        }
        return new JSONObject();
    }

    /**
     * Convert all key-value pairs of the PipeItem to JSONObject
     * @param object object
     * @return
     */
    public static JSONObject toJSON(final Object object) throws JSONException{
        if (object != null) {
            return new JSONObject(getGson().toJson(object));
        } else {
            PipeLog.d(TAG, "pipeItem is null");
        }
        return new JSONObject();
    }

    /**
     * Convert the specified key-value pairs of the PipeItem to JSONObject
     * @param pipeItem
     * @param keys
     * @return
     */
    public static JSONObject toJSON(final APipeItem pipeItem, final String... keys) throws JSONException {
        JsonObject newJSONObject = new JsonObject();
        PipeLog.d(TAG, "keys size: " + keys.length);
        if (keys == null || keys.length == 0) {
            return toJSON(pipeItem);
        } else {
            for (String key: keys) {
                PipeLog.d(TAG, "key: " + key);
            }
        }

        if (pipeItem != null) {
            JsonElement element = pipeItem.toJsonTree(getGson());

            /** check if element is JSONObject */
            if (element != null && element.isJsonObject()) {
                JsonObject jObject = element.getAsJsonObject();
                Set<Map.Entry<String, JsonElement>> entries = jObject.entrySet();
                HashMap<String, JsonElement> elementHashMap = new HashMap<String, JsonElement>();

                /** put key and value to HashMap */
                for (Map.Entry<String, JsonElement> entry : entries) {
                    PipeLog.d(TAG, "key: " + entry.getKey() + " value: " + entry.getValue());
                    elementHashMap.put(entry.getKey(), entry.getValue());
                }

                /** check if element map contains key and add to new JSONObject */
                for (String key : keys) {
                    if (elementHashMap.containsKey(key)) {
                        newJSONObject.add(key, elementHashMap.get(key));
                    }
                }
                return new JSONObject(newJSONObject.toString());
            } else {
                return toJSON(pipeItem);
            }
        }
        return new JSONObject();
    }

    public static void release() {
        mGson = null;
    }
}
