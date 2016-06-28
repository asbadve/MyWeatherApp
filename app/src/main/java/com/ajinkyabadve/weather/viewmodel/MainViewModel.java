package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableField;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import com.ajinkyabadve.weather.WeatherApplication;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.RealmUtil;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Ajinkya on 26-06-2016.
 */
public class MainViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private final Realm realm;
    private final OnDialogShow onDialogShow;
    public ObservableInt helloVisibility;
    public ObservableField<String> infoMessage;
    private Subscription subscription;
    private Context context;

    public interface OnDialogShow {
        void onAddCityDialogShow();
    }


    public MainViewModel(Context context, OnDialogShow onDialogShow) {
        realm = Realm.getDefaultInstance();
        this.context = context;
        this.onDialogShow = onDialogShow;
        infoMessage = new ObservableField<>("hello world");
        checkAnyCityAddedOrNot();
        //loadWeather();
    }

    /***
     * checks any city has been added or not if not then open dialog fragment to add the city
     */
    private void checkAnyCityAddedOrNot() {
        long cityCount = realm.where(CityRealm.class).count();
        if (cityCount == 0) {
            if (onDialogShow != null) {
                onDialogShow.onAddCityDialogShow();
            }
        }

    }

    public void onFabClick(View view) {
        if (onDialogShow != null) {
            onDialogShow.onAddCityDialogShow();
        }
    }


    private void loadWeather() {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        WeatherApplication weatherApplication = WeatherApplication.get(context);
        OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("cnt", "14");
        queryParam.put("APPID", "8be06227a313736007f84b540e2aed5f");

        subscription = openWeatherMapService.getWeatherForeCastByCity("Pune", queryParam)
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
