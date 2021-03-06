package com.ajinkyabadve.weather;

import android.app.Application;
import android.content.Context;
import android.support.multidex.MultiDex;

import com.ajinkyabadve.weather.JobSchedular.WeatherExactJob;
import com.ajinkyabadve.weather.JobSchedular.WeatherJobCreator;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.util.SharedPreferenceDataManager;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.Set;

import io.realm.Realm;
import io.realm.RealmConfiguration;
import rx.Scheduler;
import rx.schedulers.Schedulers;

/**
 * Application class for the Weather application
 * Created by Ajinkya on 26-06-2016.
 */
public class WeatherApplication extends Application {
    public Realm realm;
    private SharedPreferenceDataManager sharedPreferenceDataManager;

    @Override
    public void onCreate() {
        super.onCreate();
        sharedPreferenceDataManager = SharedPreferenceDataManager.getInstance(this);
        int firstLunch = sharedPreferenceDataManager.getSavedIntPreference(SharedPreferenceDataManager.FIREST_LAUCH);
        if (firstLunch == 0) {
            sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.FIREST_LAUCH, 1);
        }

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
        } else {
//            JobManager.instance().cancelAll();//just remove it after production
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

    public OpenWeatherMapService getOpenWeatherMapPlacesService() {
        if (openWeatherMapService == null) {
            openWeatherMapService = OpenWeatherMapService.Factory.cretePlaceService();
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

    @Override
    protected void attachBaseContext(Context base) {
        super.attachBaseContext(base);
        MultiDex.install(this);
    }


}
