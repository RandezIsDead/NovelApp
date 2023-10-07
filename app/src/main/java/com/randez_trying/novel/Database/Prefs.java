package com.randez_trying.novel.Database;

import android.content.Context;
import android.content.SharedPreferences;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.randez_trying.novel.Models.Credentials;
import com.randez_trying.novel.Models.User;

public class Prefs {

    private static final String SHARED_PREFS = "Novel_Prefs";
    private static final Gson gson = new Gson();

    public static void write(Context context, String key, String text) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putString(key, text);
        editor.apply();
    }

    public static String read(Context context, String key) {
        return context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE).getString(key, "");
    }

    public static void writeObject(Context context, String key, Object value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        SharedPreferences.Editor prefsEditor = preferences.edit();
        Gson gson = new Gson();
        String json = gson.toJson(value);
        prefsEditor.putString(key, json);
        prefsEditor.apply();
    }

    public static String getJson(Context context, String key) {
        SharedPreferences preferences = context.getSharedPreferences(SHARED_PREFS, Context.MODE_PRIVATE);
        return preferences.getString(key, "");
    }

    public static void saveMe(Context context, User me) {
        writeObject(context, "me", me);
    }

    public static User getMe(Context context) {
        String json = getJson(context, "me");
        if (gson.fromJson(json, new TypeToken<User>() {}.getType()) == null) return null;
        else return gson.fromJson(json, new TypeToken<User>() {}.getType());
    }

    public static void saveCredentials(Context context, Credentials credentials) {
        writeObject(context, "credentials", credentials);
    }

    public static Credentials getCredentials(Context context) {
        String json = getJson(context, "credentials");
        if (gson.fromJson(json, new TypeToken<Credentials>() {}.getType()) == null) return null;
        else return gson.fromJson(json, new TypeToken<Credentials>() {}.getType());
    }

    public static void saveFilter(Context context, User me) {
        writeObject(context, "filter", me);
    }

    public static User getFilter(Context context) {
        String json = getJson(context, "filter");
        if (gson.fromJson(json, new TypeToken<User>() {}.getType()) == null) return null;
        else return gson.fromJson(json, new TypeToken<User>() {}.getType());
    }

    public static void logout(Context context) {
        write(context, "me", null);
    }
}
