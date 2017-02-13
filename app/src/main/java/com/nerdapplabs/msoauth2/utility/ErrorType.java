package com.nerdapplabs.msoauth2.utility;

/**
 * Created by Mohd. Shariq on 01/02/17.
 */

public enum ErrorType {
    SUCCESS, ERROR, UPDATE, WARNING, CUSTOM;

    public static int getColorCode(ErrorType type) {
        switch (type) {
            case SUCCESS:
                return 0xff6ec071;
            case ERROR:
                return 0xffc41930;
            case UPDATE:
                return 0xff676767;
            case WARNING:
                return 0xffffc107;
            case CUSTOM:
                return 0xff2195f3;
        }
        return 0xff676767;
    }
}