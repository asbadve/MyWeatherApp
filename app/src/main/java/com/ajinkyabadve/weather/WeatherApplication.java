package com.ajinkyabadve.weather;

import android.app.Application;
import android.content.Context;

import com.ajinkyabadve.weather.JobSchedular.WeatherExactJob;
import com.ajinkyabadve.weather.JobSchedular.WeatherJobCreator;
import com.ajinkyabadve.weather.JobSchedular.WeatherPeriodicJob;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Created by Ajinkya on 26-06-2016.
 */
public class WeatherApplication extends Application {
    public Realm realm;

    @Override
    public void onCreate() {
        super.onCreate();


        RealmConfiguration realmConfiguration = new RealmConfiguration.Builder(this)
                .build();

        Realm.setDefaultConfiguration(realmConfiguration);

        JobManager.create(this).addJobCreator(new WeatherJobCreator());

        realm = Realm.getDefaultInstance();

        Set<JobRequest> allJobRequests = JobManager.instance().getAllJobRequests();
        if (allJobRequests.size() == 0) {
            int jobId = new JobRequest.Builder(WeatherExactJob.TAG)
                    .setExact(10_000L)
                    .setPersisted(true)
                    .build()
                    .schedule();
        }


    }


    private OpenWeatherMapService openWeatherMapService;
    private Scheduler defaultSubscribeScheduler;

    public static WeatherApplication get(Context context) {
        return (WeatherApplication) context.getApplicationContext();
    }

    public OpenWeatherMapService getOpenWeatherMapService() {
        if (openWeatherMapService == null) {
            openWeatherMapService = OpenWeatherMapService.Factory.create();
        }
        return openWeatherMapService;
    }

    //For setting mocks during testing
    public void setOpenWeatherMapService(OpenWeatherMapService openWeatherMap) {
        this.openWeatherMapService = openWeatherMap;
    }

    public Scheduler defaultSubscribeScheduler() {
        if (defaultSubscribeScheduler == null) {
            defaultSubscribeScheduler = Schedulers.io();
        }
        return defaultSubscribeScheduler;
    }

    //User to change scheduler from tests
    public void setDefaultSubscribeScheduler(Scheduler scheduler) {
        this.defaultSubscribeScheduler = scheduler;
    }

}
