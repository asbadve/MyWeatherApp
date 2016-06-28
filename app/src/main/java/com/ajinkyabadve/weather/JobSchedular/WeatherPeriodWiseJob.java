package com.ajinkyabadve.weather.JobSchedular;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ajinkyabadve.weather.WeatherApplication;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.RealmUtil;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobManager;
import com.evernote.android.job.JobRequest;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.realm.Realm;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * Created by Ajinkya on 27/06/2016.
 */
public class WeatherPeriodWiseJob extends Job {


    public static final String TAG = "WeatherPeriodWiseJob";
    private Subscription subscription;

    public WeatherPeriodWiseJob() {
    }

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(TAG, "onRunJob() called with: " + "params = [" + params + "]");
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        WeatherApplication weatherApplication = WeatherApplication.get(getContext());
        OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();
        final Map<String, String> queryParam = new HashMap<>();
        queryParam.put("cnt", "14");
        queryParam.put("APPID", "8be06227a313736007f84b540e2aed5f");


        List<String> cities = new ArrayList<>();
        cities.add("Pune");
        cities.add("Mumbai");
        final Map<String, String> queryParamtest = new HashMap<>();
        queryParam.put("cnt", "14");
        queryParam.put("APPID", "8be06227a313736007f84b540e2aed5f");

        //use below code to get the multiple cities data at once
        subscription = Observable.from(cities).flatMap(new Func1<String, Observable<OpenWeatherMap>>() {
            @Override
            public Observable<OpenWeatherMap> call(String s) {
                return WeatherApplication.get(getContext()).getOpenWeatherMapService().getWeatherForeCastByCity(s, queryParam);
            }
        }).toList().subscribe(new Subscriber<List<OpenWeatherMap>>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted() called with: " + "");

            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError() called with: " + "e = [" + e + "]");

            }

            @Override
            public void onNext(List<OpenWeatherMap> openWeatherMaps) {

                Log.d(TAG, "onNext() called with: " + "openWeatherMaps = [" + openWeatherMaps + "]");
            }
        });

        return Result.SUCCESS;
    }

    public static void scheduleJob() {
        new JobRequest.Builder(WeatherPeriodWiseJob.TAG)
                .setExecutionWindow(30_000L, 40_000L)
                .build()
                .schedule();
    }

    private int schedulePeriodicJob() {
        int jobId = new JobRequest.Builder(WeatherPeriodWiseJob.TAG)
                .setPeriodic(60_000L)
                .setPersisted(true)
                .build()
                .schedule();
        return jobId;
    }

    private void scheduleExactJob() {
        int jobId = new JobRequest.Builder(WeatherPeriodWiseJob.TAG)
                .setExact(10_000L)
                .setPersisted(true)
                .build()
                .schedule();
    }

    private void cancelJob(int jobId) {
        JobManager.instance().cancel(jobId);
    }
}
