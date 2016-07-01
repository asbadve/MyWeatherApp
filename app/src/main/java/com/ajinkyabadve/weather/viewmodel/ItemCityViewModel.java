package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.view.View;

import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.util.SharedPreferenceDataManager;
import com.ajinkyabadve.weather.view.adapter.CitiesAdapter;

import io.realm.Realm;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class ItemCityViewModel extends BaseObservable implements ViewModel {
    private SharedPreferenceDataManager sharedPreferenceDataManager;
    private CityRealm cityRealm;
    private Context context;
    private CitiesAdapter.OnItemClick onItemClick;
    private int defaultId;

    public ItemCityViewModel(Context context, CityRealm cityRealm, CitiesAdapter.OnItemClick onItemClick) {
        this.cityRealm = cityRealm;
        this.context = context;
        this.onItemClick = onItemClick;
        sharedPreferenceDataManager = SharedPreferenceDataManager.getInstance(context);
        defaultId = sharedPreferenceDataManager.getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);

    }

    public String getCityName() {
        return cityRealm.getName();
    }

    public boolean getIsDefault() {
        return cityRealm.isDefault();
    }


    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setCityRealm(CityRealm cityRealm) {
        this.cityRealm = cityRealm;
        notifyChange();
    }

    public void onItemClick(View view) {
        onItemClick.OnCitySelected(cityRealm);
        sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID, cityRealm.getId());
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        cityRealm.setDefault(true);
//        realm.commitTransaction();
//
//        realm.beginTransaction();
//        RealmResults<CityRealm> cityRealms = realm.where(CityRealm.class).notEqualTo("id", cityRealm.getId()).not().findAll();
//        for (int i = 0; i < cityRealms.size(); i++) {
//            cityRealms.get(i).setDefault(false);
//        }
//        realm.commitTransaction();

    }

    public void onItemDeleteClick(View view) {
//        Realm realm = Realm.getDefaultInstance();
//        realm.beginTransaction();
//        cityRealm.deleteFromRealm();
//        realm.commitTransaction();
        onItemClick.OnCityDeletedFromAdapter(cityRealm);
        defaultId = sharedPreferenceDataManager.getSavedDefaultCityIdPreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID);
//        notifyAll();

//
    }

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
