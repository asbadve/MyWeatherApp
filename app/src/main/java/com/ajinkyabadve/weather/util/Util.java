package com.ajinkyabadve.weather.util;

import android.content.Context;
import android.text.format.DateFormat;
import android.util.Log;

import com.ajinkyabadve.weather.R;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

/**
 * Created by Ajinkya on 29-06-2016.
 */
public class Util {
    public static final String DATE_FORMAT = "yyyyMMdd";


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

    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     *
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateString(Date date) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(date);
    }

    public static Integer getDbDateLong(Date date) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return Integer.valueOf(sdf.format(date));
    }


    /**
     * Converts Date class to a string representation, used for easy comparison and database lookup.
     *
     * @param date The input date
     * @return a DB-friendly representation of the date, using the format defined in DATE_FORMAT.
     */
    public static String getDbDateStringAfter14Days(Date date) {
        // Because the API returns a unix timestamp (measured in seconds),
        // it must be converted to milliseconds in order to be converted to valid date.
        Calendar c = Calendar.getInstance();
        c.setTime(date);
        c.add(Calendar.DATE, 13);
        SimpleDateFormat sdf = new SimpleDateFormat(DATE_FORMAT);
        return sdf.format(c.getTime());
    }


    /**
     * Converts db date format to the format "Month day", e.g "June 24".
     *
     * @param context Context to use for resource localization
     * @param dateStr The db formatted date string, expected to be of the form specified
     *                in Utility.DATE_FORMAT
     * @return The day in the form of a string formatted "December 6"
     */
    public static String getFormattedMonthDay(Context context, String dateStr) {
        SimpleDateFormat dbDateFormat = new SimpleDateFormat(DATE_FORMAT);
        try {
            long msTime = System.currentTimeMillis();
            Date curDateTime = new Date(msTime);
            Calendar calendarForCurrentDate = Calendar.getInstance();
            calendarForCurrentDate.setTime(curDateTime);
            int dayOfMonthForCurrentDate = calendarForCurrentDate.get(Calendar.DAY_OF_MONTH);

            Date inputDateTest = dbDateFormat.parse(dateStr);
            Calendar calendarForInputDate = Calendar.getInstance();
            calendarForInputDate.setTime(inputDateTest);
            int dayOfMonthForInputDate = calendarForInputDate.get(Calendar.DAY_OF_MONTH);

            if (dayOfMonthForCurrentDate == dayOfMonthForInputDate) {
                return "Today , " + new SimpleDateFormat("MMM").format(calendarForInputDate.getTime()) + " " + dayOfMonthForInputDate;
            } else if (dayOfMonthForCurrentDate + 1 == dayOfMonthForInputDate) {
                return "Tomorrow ";

            } else {

                return new SimpleDateFormat("EEEE").format(calendarForInputDate.getTime());

//                Date inputDate = dbDateFormat.parse(dateStr);
//                SimpleDateFormat monthDayFormat = new SimpleDateFormat("MMMM dd yyyy");
//                String monthDayString = monthDayFormat.format(inputDate);
//                return monthDayString;
            }

        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    public static String formatTemperature(Context context, double temperature) {
        double temp;
        boolean isMetric = false;
        if (!isMetric) {
            //temp = 9 * temperature / 5 + 32;

            temp = (5.0 / 9.0) * (temperature - 32);//294.57

        } else {
            temp = temperature;
        }
        return context.getString(R.string.format_temperature, temp);
    }

    /**
     * Helper method to provide the icon resource id according to the weather condition id returned
     * by the OpenWeatherMap call.
     *
     * @param weatherId from OpenWeatherMap API response
     * @return resource id for the corresponding icon. -1 if no relation is found.
     */
    public static int getIconResourceForWeatherCondition(int weatherId) {
        // Based on weather code data found at:
        // http://bugs.openweathermap.org/projects/api/wiki/Weather_Condition_Codes
        if (weatherId >= 200 && weatherId <= 232) {
            return R.drawable.ic_storm;
        } else if (weatherId >= 300 && weatherId <= 321) {
            return R.drawable.ic_light_rain;
        } else if (weatherId >= 500 && weatherId <= 504) {
            return R.drawable.ic_rain;
        } else if (weatherId == 511) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 520 && weatherId <= 531) {
            return R.drawable.ic_rain;
        } else if (weatherId >= 600 && weatherId <= 622) {
            return R.drawable.ic_snow;
        } else if (weatherId >= 701 && weatherId <= 761) {
            return R.drawable.ic_fog;
        } else if (weatherId == 761 || weatherId == 781) {
            return R.drawable.ic_storm;
        } else if (weatherId == 800) {
            return R.drawable.ic_clear;
        } else if (weatherId == 801) {
            return R.drawable.ic_light_clouds;
        } else if (weatherId >= 802 && weatherId <= 804) {
            return R.drawable.ic_cloudy;
        }
        return -1;
    }
}
