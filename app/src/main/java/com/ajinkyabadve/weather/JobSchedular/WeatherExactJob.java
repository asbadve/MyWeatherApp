package com.ajinkyabadve.weather.JobSchedular;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ajinkyabadve.weather.WeatherApplication;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.RealmUtil;
import com.evernote.android.job.Job;
import com.evernote.android.job.JobRequest;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import rx.Subscriber;
import rx.Subscription;

/**
 * Created by Ajinkya on 27/06/2016.
 */
public class WeatherExactJob extends Job {
    public static final String TAG = "WeatherExactJob";
    private Subscription subscription;
    Result result;

    @NonNull
    @Override
    protected Result onRunJob(Params params) {
        Log.d(TAG, "onRunJob() called with: " + "params = [" + params + "]");

        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        WeatherApplication weatherApplication = WeatherApplication.get(getContext());
        OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("cnt", "14");
        queryParam.put("APPID", "8be06227a313736007f84b540e2aed5f");

        subscription = openWeatherMapService.getWeatherForeCastByCity("Mumbai", queryParam)
//                .observeOn(AndroidSchedulers.mainThread())
//                .subscribeOn(weatherApplication.defaultSubscribeScheduler())
                .subscribe(new Subscriber<OpenWeatherMap>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called with: " + "");
                        result = Result.SUCCESS;

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
                        result = Result.FAILURE;
                    }

                    @Override
                    public void onNext(OpenWeatherMap openWeatherMap) {
                        Log.d(TAG, "onNext() called with: " + "openWeatherMap = [" + openWeatherMap + "]");

                        CityRealm cityRealmTemp = RealmUtil.getCityWithWeather(openWeatherMap);
                        Realm realm = Realm.getDefaultInstance();
                        realm.beginTransaction();
                        CityRealm cityRealm = realm.copyToRealmOrUpdate(cityRealmTemp);
                        realm.commitTransaction();
                    }
                });

        if (result == Result.FAILURE) {
            result = Result.RESCHEDULE;
        } else if (result == Result.SUCCESS) {
            int jobId = new JobRequest.Builder(WeatherPeriodicJob.TAG)
                    .setPeriodic(60_000L)
                    .setPersisted(true)
                    .build()
                    .schedule();
        }
        return result;
    }
}
