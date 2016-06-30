package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableInt;
import android.view.View;

import com.ajinkyabadve.weather.model.realm.CityRealm;

import io.realm.Realm;
import rx.Subscription;

/**
 * Created by Ajinkya on 26-06-2016.
 */
public class MainViewModel extends BaseObservable implements ViewModel {

    private static final String TAG = MainViewModel.class.getSimpleName();
    private final Realm realm;
    private final OnDialogShow onDialogShow;
    public ObservableInt helloVisibility;
    private Subscription subscription;
    private Context context;

    public interface OnDialogShow {
        void onAddCityDialogShow();
    }


    public MainViewModel(Context context, OnDialogShow onDialogShow) {
        realm = Realm.getDefaultInstance();
        this.context = context;
        this.onDialogShow = onDialogShow;
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


    @Override
    public void onDestroy() {

    }

    @Override
    public void onStart() {

    }

    @Override
    public void Stop() {

    }


}
