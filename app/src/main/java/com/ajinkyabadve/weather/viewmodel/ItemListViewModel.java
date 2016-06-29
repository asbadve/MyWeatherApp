package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import com.ajinkyabadve.weather.Util;
import com.ajinkyabadve.weather.model.realm.ListRealm;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class ItemListViewModel extends BaseObservable implements ViewModel {
    ListRealm listRealm;
    Context context;

    public ItemListViewModel(Context context, ListRealm listRealm) {
        this.listRealm = listRealm;
        this.context = context;
    }

    public String getDt() {
        return Util.getDateFormatByString(listRealm.getDt())+" in mili"+listRealm.getDt();
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

    // Allows recycling ItemRepoViewModels within the recyclerview adapter
    public void setListRealm(ListRealm listRealm) {
        this.listRealm = listRealm;
        notifyChange();
    }
}
