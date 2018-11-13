/*
 * Copyright (C) 2016 Mattel, Inc. All rights reserved.
 */

package com.sproutling.http;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Stream helpers
 * Created by bradylin on 11/18/16.
 */

public class Streams {
    private Streams() {
    }

    /**
     * Write stream into file
     *
     * @param from Input stream
     * @param to  Output file
     * @throws java.io.IOException
     */
    public static void file(InputStream from, File to) throws IOException {
        FileOutputStream output = new FileOutputStream(to);
        try {
            copy(from, output);
        } finally {
            output.close();
        }
    }

    /**
     * Copy one stream to another
     *
     * @param from Input stream
     * @param to   Output stream
     * @throws IOException
     */
    public static void copy(InputStream from, OutputStream to) throws IOException {
        byte[] buffer = new byte[4 * 1024];
        int read;
        while ((read = from.read(buffer)) != -1) {
            to.write(buffer, 0, read);
        }
        to.flush();
    }

    /**
     * Read stream as json object
     *
     * @param input Input stream
     * @return Json object
     * @throws IOException
     * @throws org.json.JSONException
     */
    public static JSONObject json(InputStream input) throws IOException, JSONException {
        return new JSONObject(string(input));
    }

    public static JSONArray jsonArray(InputStream input) throws IOException, JSONException {
        return new JSONArray(string(input));
    }

    /**
     * Read stream as string
     *
     * @param input Input stream
     * @return String value
     * @throws IOException
     */
    public static String string(InputStream input) throws IOException {
        return new String(bytes(input));
    }

    /**
     * Read stream as byte array
     *
     * @param input Input stream
     * @return Byte array
     * @throws IOException
     */
    public static byte[] bytes(InputStream input) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        try {
            copy(input, output);
            return output.toByteArray();
        } finally {
            output.close();
        }
    }
}
