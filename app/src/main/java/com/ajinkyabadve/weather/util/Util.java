package com.ajinkyabadve.weather.util;

import android.text.format.DateFormat;
import android.util.Log;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ajinkya on 29-06-2016.
 */
public class Util {


    /**
     * Convert date in dd/mm/yy
     *
     * @param dateInMiliSeconds
     * @return
     */
    public static String getDateFormatByString(long dateInMiliSeconds) {

        Calendar cal = Calendar.getInstance();
        TimeZone tz = cal.getTimeZone();

        /* debug: is it local time? */
        Log.d("Time zone: ", tz.getDisplayName());

        /* date formatter in local timezone */
        SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
        sdf.setTimeZone(tz);

        /* print your timestamp and double check it's the date you expect */
        long timestamp = dateInMiliSeconds;
        String localTime = sdf.format(new Date(timestamp * 1000)); // I assume your timestamp is in seconds and you're converting to milliseconds?
        Log.d("Time: ", localTime);
        Date date = null;
        try {
            date = sdf.parse(localTime);
            Log.d("Date format ", localTime);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return DateFormat.format("dd", date) + " " + DateFormat.format("EEEE", date);
    }
}
