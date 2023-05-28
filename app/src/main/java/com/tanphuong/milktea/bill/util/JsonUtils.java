package com.tanphuong.milktea.bill.util;

import android.content.Context;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.stream.Collectors;

public class JsonUtils {
    public static JSONObject readFromResources(Context context, int resource) {
        try {
            final InputStream inputStream = context.getResources().openRawResource(resource);
            return readFromInputStream(inputStream);

        } catch (Exception e) {
            return new JSONObject();
        }
    }

    private static JSONObject readFromInputStream(InputStream inputStream) throws JSONException {
        final BufferedReader reader = new BufferedReader(new InputStreamReader(inputStream));
        final String inputString = reader.lines().collect(Collectors.joining());
        return new JSONObject(inputString);
    }
}