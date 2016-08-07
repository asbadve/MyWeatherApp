package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.util.SharedPreferenceDataManager;
import com.ajinkyabadve.weather.view.adapter.CitiesAdapter;

/**
 * View model for {@link CitiesAdapter}
 * Created by Ajinkya on 29/06/2016.
 */
public class ItemCityViewModel extends BaseObservable implements ViewModel {
    private SharedPreferenceDataManager sharedPreferenceDataManager;
    private CityRealm cityRealm;
    private Context context;
    private CitiesAdapter.OnItemClick onItemClick;
    private int defaultId;

    /***
     * @param context
     * @param cityRealm
     * @param onItemClick
     */
    public ItemCityViewModel(Context context, CityRealm cityRealm, CitiesAdapter.OnItemClick onItemClick) {
        this.cityRealm = cityRealm;
        this.context = context;
        this.onItemClick = onItemClick;
        sharedPreferenceDataManager = SharedPreferenceDataManager.getInstance(context);
        defaultId = sharedPreferenceDataManager.getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);

    }

    /**
     * @return city name
     */
    public String getCityName() {
        return cityRealm.getName();
    }


    /***
     * Allows recycling ItemCityViewModel within the recyclerView adapter
     *
     * @param cityRealm
     */
    public void setCityRealm(CityRealm cityRealm) {
        this.cityRealm = cityRealm;
        notifyChange();
    }

    /**
     * we save the default city id in the shared preference
     *
     * @param view
     */
    public void onItemClick(View view) {
        onItemClick.OnCitySelected(cityRealm);
        sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID, cityRealm.getId());
    }

    /**
     * after delete the city notifies the adapter
     *
     * @param view
     */
    public void onItemDeleteClick(View view) {
        onItemClick.OnCityDeletedFromAdapter(cityRealm);
        defaultId = sharedPreferenceDataManager.getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);
    }

    /***
     * return weather the city is default or not
     *
     * @return
     */
    public boolean isDefaultCity() {
        defaultId = sharedPreferenceDataManager.getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);
        return (defaultId == cityRealm.getId());
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
