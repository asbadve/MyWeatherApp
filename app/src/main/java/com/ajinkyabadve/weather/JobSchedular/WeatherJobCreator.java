package com.ajinkyabadve.weather.JobSchedular;

import android.util.Log;

import com.evernote.android.job.Job;
import com.evernote.android.job.JobCreator;

/**
 * this class crete the jobs for weather application
 * Created by Ajinkya on 27/06/2016.
 */
public class WeatherJobCreator implements JobCreator {
    private static final String TAG = WeatherJobCreator.class.getSimpleName();

    @Override
    public Job create(String tag) {
        Log.d(TAG, "create() called with: " + "tag = [" + tag + "]");
        switch (tag) {
            case WeatherPeriodWiseJob.TAG:
                return new WeatherPeriodWiseJob();
            case WeatherExactJob.TAG:
                return new WeatherExactJob();
            default:
                return null;
        }
    }
}
