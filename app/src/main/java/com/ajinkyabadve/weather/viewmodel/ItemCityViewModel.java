package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.ajinkyabadve.weather.model.realm.CityRealm;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class ItemCityViewModel extends BaseObservable implements ViewModel {
    private CityRealm cityRealm;
    private Context context;

    public ItemCityViewModel(Context context, CityRealm cityRealm) {
        this.cityRealm = cityRealm;
        this.context = context;
    }

    public String getCityName() {
        return cityRealm.getName();
    }


    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setCityRealm(CityRealm cityRealm) {
        this.cityRealm = cityRealm;
        notifyChange();
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
