package com.nerdapplabs.forumapp;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.nerdapplabs.forumapp.utility.NetworkConnectivity;
import com.nerdapplabs.forumapp.utility.Preferences;

import java.io.IOException;

/**
 * Created by mohd on 20/01/17.
 */

public class ForumApplication extends Application {

    private static ForumApplication mInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mInstance = this;

        // Initialize the Prefs class
        new Preferences.Builder().setContext(this)
                .setMode(ContextWrapper.MODE_PRIVATE)
                .setPrefsName(getPackageName())
                .setUseDefaultSharedPreference(true)
                .build();
    }

    public static synchronized ForumApplication getInstance() {
        return mInstance;
    }

    public static Context getContext() throws IOException {
        return mInstance.getApplicationContext();
    }

    public void setConnectivityListener(NetworkConnectivity.ConnectivityReceiverListener listener) {
        NetworkConnectivity.connectivityReceiverListener = listener;
    }
}