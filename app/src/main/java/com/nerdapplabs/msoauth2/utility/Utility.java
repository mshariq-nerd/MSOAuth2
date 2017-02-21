package com.nerdapplabs.msoauth2.utility;

import java.util.Calendar;

/**
 * Created by Mohd. Shariq on 21/02/17.
 */

public class Utility {

    public static int getDobStartYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -105);
        return prevYear.get(Calendar.YEAR);
    }

    public static int getDobEndYear() {
        Calendar prevYear = Calendar.getInstance();
        prevYear.add(Calendar.YEAR, -5);
        return prevYear.get(Calendar.YEAR);
    }
}
