package com.nerdapplabs.msoauth2.utility;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import com.nerdapplabs.msoauth2.MSOAuth2;
import com.nerdapplabs.msoauth2.R;

/**
 * Created by Mohd. Shariq on 20/01/17.
 */

public class NetworkConnectivity extends BroadcastReceiver {

    public static ConnectivityReceiverListener connectivityReceiverListener;

    public NetworkConnectivity() {
        super();
    }

    @Override
    public void onReceive(Context context, Intent intent) {
        ConnectivityManager cm = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        boolean isConnected = activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();

        if (connectivityReceiverListener != null) {
            connectivityReceiverListener.onNetworkConnectionChanged(isConnected);
        }
    }

    public static boolean isConnected() {
        ConnectivityManager
                cm = (ConnectivityManager) MSOAuth2.getInstance().getApplicationContext()
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
        return activeNetwork != null
                && activeNetwork.isConnectedOrConnecting();
    }


    public interface ConnectivityReceiverListener {
        void onNetworkConnectionChanged(boolean isConnected);
    }

    /**
     * Method for displaying the network connection status message
     */
    public static void showNetworkConnectMessage(Activity activity, boolean isConnected) {
        String message;
        ErrorType type;
        if (isConnected) {
            message = activity.getString(R.string.internet_connected);
            type = ErrorType.SUCCESS;
        } else {
            message = activity.getString(R.string.internet_connection_error);
            type = ErrorType.ERROR;
        }
        MessageSnackbar.with(activity, null).type(type).message(message)
                .duration(Duration.SHORT).show();
    }
}

