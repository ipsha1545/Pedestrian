package com.example.pedestrian.utils;

import android.content.SharedPreferences;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.TreeSet;

public class Preferences {
	
	private static String PREFERANCES = "Preferances";

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
}
