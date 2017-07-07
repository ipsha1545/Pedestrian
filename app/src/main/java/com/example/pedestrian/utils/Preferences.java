package com.example.pedestrian.utils;

import android.content.SharedPreferences;

import java.util.Set;

public class Preferences {
	
	private static String PREFERANCES = "Preferances";

	//For facebook authentication

	public static String getSettingsParam(String paramName) {
		SharedPreferences settings = getPreferences();
		return settings.getString(paramName, "");
	}

	public static SharedPreferences getPreferences() {
		SharedPreferences settings = ApplicationContextProvider.getContext()
				.getSharedPreferences(PREFERANCES, 0);
		return settings;
	}

	public static void setSettingsParam(String paramName, String paramValue) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString(paramName, paramValue);
		editor.commit();
	}

	public static void setSettingsParamLong(String paramName, long paramValue) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putLong(paramName, paramValue);
		editor.commit();
	}

	public static void setStringArray(String paramName, Set<String> paramValue) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putStringSet(paramName, paramValue);
		editor.commit();
	}

	public static Set<String> getStringSet(String paramName) {
		SharedPreferences settings = getPreferences();
		return settings.getStringSet(paramName, null);
	}

	public static void setUserId(int userId) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("userID", userId);
		editor.commit();
	}

	public static void setUserIdfb(int userId) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putInt("userIDfb", userId);
		editor.commit();
	}



	public static int getUserId() {
		SharedPreferences settings = getPreferences();
		return settings.getInt("userID", 0);
	}

	public static int getUserIdfb() {
		SharedPreferences settings = getPreferences();
		return settings.getInt("userIDfb", 0);
	}

	public static void setUserName(String userName) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userName", userName);
		editor.commit();
	}

	public static void setUserNamefb(String userName) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userNamefb", userName);
		editor.commit();
	}

	public static String getUserNamefb() {
		SharedPreferences settings = getPreferences();
		return settings.getString("userNamefb", "");
	}

	public static void setUserImage(String userImage) {
		SharedPreferences settings = getPreferences();
		SharedPreferences.Editor editor = settings.edit();
		editor.putString("userImage", userImage);
		editor.commit();
	}


	public static String getUserImage() {
		SharedPreferences settings = getPreferences();
		return settings.getString("userImage", "");
	}
}
