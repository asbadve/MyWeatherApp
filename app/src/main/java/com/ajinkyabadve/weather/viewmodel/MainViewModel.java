package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;

import com.ajinkyabadve.weather.WeatherApplication;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.RealmUtil;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Ajinkya on 26-06-2016.
 */
public class MainViewModel implements ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    public ObservableInt helloVisibility;
    public ObservableField<String> infoMessage;
    private Subscription subscription;
    private Context context;


    public MainViewModel(Context context) {
        this.context = context;
        infoMessage = new ObservableField<>("hello world");
        loadWeather();
    }

    private void loadWeather() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        WeatherApplication weatherApplication = WeatherApplication.get(context);
        OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("cnt", "14");
        queryParam.put("APPID", "8be06227a313736007f84b540e2aed5f");

        subscription = openWeatherMapService.getWeatherForeCast("Mumbai", queryParam)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(weatherApplication.defaultSubscribeScheduler())
                .subscribe(new Subscriber<OpenWeatherMap>() {
                    @Override
                    public void onCompleted() {

                        Log.d(TAG, "onCompleted() called with: " + "");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: " + "e = [" + e + "]");

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



    }

    @Override
    public void onDestroy() {

    }


}
