package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.util.Log;

import com.arlib.floatingsearchview.FloatingSearchView;

/**
 * Created by Ajinkya on 28-06-2016.
 */
public class AddCityViewModel implements ViewModel {
    private static final String TAG = AddCityViewModel.class.getSimpleName();
    private Context context;

    @Override
    public void onDestroy() {

    }

    public AddCityViewModel(Context context) {
        this.context = context;
    }


}
