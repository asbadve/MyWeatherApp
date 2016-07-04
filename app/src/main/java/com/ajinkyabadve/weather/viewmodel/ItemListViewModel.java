package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;

import com.ajinkyabadve.weather.model.realm.ListRealm;
import com.ajinkyabadve.weather.util.Util;

/**
 * view model class for cityAdapter{@link com.ajinkyabadve.weather.view.adapter.ListAdapter}
 * Created by Ajinkya on 29/06/2016.
 */
public class ItemListViewModel extends BaseObservable implements ViewModel {
    ListRealm listRealm;
    Context context;

    /***
     * @param context   context
     * @param listRealm realm list
     */
    public ItemListViewModel(Context context, ListRealm listRealm) {
        this.listRealm = listRealm;
        this.context = context;
    }

    /**
     * @return get the date to be displayed
     */
    public String getDt() {
        return Util.getFormattedMonthDay(context, String.valueOf(listRealm.getDt()));
    }

    /**
     * @return get the forecast string
     */
    public String getMainForecast() {
        return listRealm.getWeatherRealm().get(0).getMain();
    }

    /**
     * @return get the high temperature in corrected format for day
     */
    public String getHighTemp() {
        return Util.formatTemperature(context, listRealm.getTempRealm().getMax());
    }

    /**
     * @return get the low temperature in corrected format for a day
     */
    public String getLowTemp() {
        return Util.formatTemperature(context, listRealm.getTempRealm().getMin());
    }

    /***
     * @return return the resource id for the weather
     */
    public int getWeatherImage() {
        return Util.getIconResourceForWeatherCondition(listRealm.getWeatherRealm().get(0).getId());
    }

    /***
     * @return return the resource id of the drawable for todays layout
     */
    public int getWeatherImageForToday() {
        return Util.getArtResourceForWeatherCondition(listRealm.getWeatherRealm().get(0).getId());
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

    /***
     * Allows recycling ItemListViewModel within the recycler View adapter
     *
     * @param listRealm
     */
    public void setListRealm(ListRealm listRealm) {
        this.listRealm = listRealm;
        notifyChange();
    }
}
