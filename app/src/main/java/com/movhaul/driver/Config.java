package com.movhaul.driver;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

//configuration file contains service url and common methods.
public class Config {

    public static final String WEB_URL = "http://104.197.80.225:3030/";

    public static final String WEB_URL_IMG ="http://104.197.80.225:8080/movehaul/assets/img/";

    public static boolean isStringNullOrWhiteSpace(String value) {
        if (value == null) {
            return true;
        }
        for (int i = 0; i < value.length(); i++) {
            if (!Character.isWhitespace(value.charAt(i))) {
                return false;
            }
        }
        return true;
    }
    public static boolean isNetworkAvailable(Context c1) {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) c1.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }
    public static boolean isConnected(Context context) {
        ConnectivityManager
                cm = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }
}






