package com.nerdapplabs.msoauth2;

import android.app.Application;
import android.content.Context;
import android.content.ContextWrapper;

import com.nerdapplabs.msoauth2.utility.LocaleHelper;
import com.nerdapplabs.msoauth2.utility.NetworkConnectivity;
import com.nerdapplabs.msoauth2.utility.Preferences;

import java.io.IOException;

/**
 * Created by Mohd. Shariq on 20/01/17.
 */

public class MSOAuth2 extends Application {

    private static MSOAuth2 mInstance;

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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(LocaleHelper.onAttach(base, "en"));
    }

    public static synchronized MSOAuth2 getInstance() {
        return mInstance;
    }

    public static Context getContext() {
        return mInstance.getApplicationContext();
    }

    public void setConnectivityListener(NetworkConnectivity.ConnectivityReceiverListener listener) {
        NetworkConnectivity.connectivityReceiverListener = listener;
    }
}