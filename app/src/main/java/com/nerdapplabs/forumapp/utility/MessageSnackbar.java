package com.nerdapplabs.forumapp.utility;


import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.TextView;

/**
 * Created by Mohd. Shariq on 03/02/17.
 */

public class MessageSnackbar {

    private static Context context;
    private static android.support.design.widget.Snackbar snackbar;
    private static MessageSnackbar messageSnackbar;

    private static int colorCode = ErrorType.getColorCode(ErrorType.SUCCESS);
    private static String snackMessage = "";
    private static int snackDuration = Duration.getDuration(Duration.SHORT);
    private static View view;
    private static boolean isCustomView;

    public static MessageSnackbar with(Context context, View fab) {
        MessageSnackbar.context = context.getApplicationContext();
        if (messageSnackbar == null)
            messageSnackbar = new MessageSnackbar();

        if (fab == null) {
            View rootView = ((Activity) context).getWindow().getDecorView().findViewById(android.R.id.content);
            view = rootView;
            snackbar = android.support.design.widget.Snackbar
                    .make(view, "", snackDuration);
        } else {
            view = fab;
            snackbar = android.support.design.widget.Snackbar
                    .make(view, "", snackDuration);
        }

        isCustomView = false;
        return messageSnackbar;
    }

    public static MessageSnackbar type(ErrorType type) {
        colorCode = ErrorType.getColorCode(type);
        return messageSnackbar;
    }

    public static MessageSnackbar type(ErrorType type, int color) {
        if (type == ErrorType.CUSTOM)
            colorCode = color;
        else
            colorCode = ErrorType.getColorCode(type);
        return messageSnackbar;
    }

    public static MessageSnackbar message(String displayingMessage) {
        snackMessage = displayingMessage;
        return messageSnackbar;
    }

    public static MessageSnackbar duration(Duration duration) {
        if (duration != Duration.CUSTOM) {
            snackDuration = Duration.getDuration(duration);
        }
        return messageSnackbar;
    }

    public static MessageSnackbar duration(Duration durationType, int duration) {
        if (durationType == Duration.CUSTOM) {
            snackDuration = duration;
        }
        return messageSnackbar;
    }

    public static MessageSnackbar contentView(final View view, int heightInDp) {
        isCustomView = true;

        final android.support.design.widget.Snackbar.SnackbarLayout snackLayout = (android.support.design.widget.Snackbar.SnackbarLayout) snackbar.getView();
        android.support.design.widget.Snackbar.SnackbarLayout.LayoutParams params =
                (android.support.design.widget.Snackbar.SnackbarLayout.LayoutParams) snackLayout.getLayoutParams();

        params.height = (int) pxFromDp(heightInDp);

        TextView textView = (TextView) snackLayout.findViewById(android.support.design.R.id.snackbar_text);
        textView.setVisibility(View.INVISIBLE);

        snackLayout.addView(view, 0, params);
        return messageSnackbar;
    }

    private static View getSnackBarLayout() {
        if (snackbar != null) {
            return snackbar.getView();
        }
        return null;
    }

    private static MessageSnackbar setColor(int colorId) {
        View snackBarView = getSnackBarLayout();
        if (snackBarView != null) {
            snackBarView.setBackgroundColor(colorId);
        }

        return messageSnackbar;
    }

    public static void show() {
        if (isCustomView) {
            snackbar.setDuration(snackDuration);
            snackbar.show();
        } else {
            snackbar = android.support.design.widget.Snackbar
                    .make(view, snackMessage, snackDuration)
                    .setDuration(snackDuration);

            setColor(colorCode);
        }
        snackbar.show();
    }

    private static float pxFromDp(int dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static void dismiss() {
        if (snackbar != null) {
            if (snackbar.isShown()) {
                snackbar.dismiss();
            }
        }
    }
}