package com.ajinkyabadve.weather.JobSchedular;

import android.support.annotation.NonNull;
import android.util.Log;

import com.ajinkyabadve.weather.R;
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
import io.realm.RealmResults;
import rx.Observable;
import rx.Subscriber;
import rx.Subscription;
import rx.functions.Func1;

/**
 * class produces the periodic job to fetch the weather forecast
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
        final OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();


        final Map<String, String> queryParam = new HashMap<>();
        queryParam.put("cnt", getContext().getString(R.string.cnt_parameter_for_days));
        queryParam.put("APPID", getContext().getString(R.string.open_weather_map));
        queryParam.put("units", getContext().getString(R.string.unit_param));

        Realm realm = Realm.getDefaultInstance();
        List<String> cities = new ArrayList<>();
        RealmResults<CityRealm> cityRealms = realm.where(CityRealm.class).findAll();
        for (int i = 0; i < cityRealms.size(); i++) {
            CityRealm cityRealm = cityRealms.get(i);
            cities.add(cityRealm.getName());
        }

        if (cities.size() > 0) {
            //use below code to get the multiple cities data at once
            subscription = Observable.from(cities).flatMap(new Func1<String, Observable<OpenWeatherMap>>() {
                @Override
                public Observable<OpenWeatherMap> call(String s) {
                    return openWeatherMapService.getWeatherForeCastByCity(s, queryParam);
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

                    List<CityRealm> cityRealmTemp = RealmUtil.getCityWithWeather(openWeatherMaps);
                    Realm realm = Realm.getDefaultInstance();
                    realm.beginTransaction();
                    List<CityRealm> cityRealm = realm.copyToRealmOrUpdate(cityRealmTemp);
                    realm.commitTransaction();

                }
            });
        } else {
            Log.d(TAG, "onRunJob() called with: " + "params = [" + params + "]-No city Present");
        }
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
