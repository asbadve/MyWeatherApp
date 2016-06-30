package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.BindingAdapter;
import android.widget.ImageView;

import com.ajinkyabadve.weather.util.Util;
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
        return Util.getFormattedMonthDay(context, String.valueOf(listRealm.getDt()));
    }

    public String getMainForecast() {
        return listRealm.getWeatherRealm().get(0).getMain();
    }

    public String getHighTemp() {
        return Util.formatTemperature(context, listRealm.getTempRealm().getMax());
    }

    public String getLowTemp() {
        return Util.formatTemperature(context, listRealm.getTempRealm().getMin());
    }

    public int getWeatherImage() {
        return Util.getIconResourceForWeatherCondition(listRealm.getWeatherRealm().get(0).getId());
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
