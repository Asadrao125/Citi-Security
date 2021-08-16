package com.appsxone.citisecurity.utils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;

public class HandleDate {
    public static String startDate() {
        String previousDate;
        Calendar cal = Calendar.getInstance();
        cal.add(Calendar.MONTH, -1);
        previousDate = new SimpleDateFormat("MM/dd/yyyy").format(cal.getTime());
        return previousDate;
    }

    public static String endDate() {
        String currentDate = "";
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.O) {
            Date c = Calendar.getInstance().getTime();
            System.out.println("Current time => " + c);
            SimpleDateFormat df = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());
            currentDate = df.format(c);
        }
        return currentDate;
    }
}