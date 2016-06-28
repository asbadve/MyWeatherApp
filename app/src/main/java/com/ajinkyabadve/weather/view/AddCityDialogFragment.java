package com.ajinkyabadve.weather.view;

import android.app.Dialog;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatDialogFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;


import android.view.ViewGroup;
import android.view.Window;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.WeatherApplication;
import com.ajinkyabadve.weather.databinding.FragmentDialogAddCityBinding;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.model.Places;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.RealmUtil;
import com.ajinkyabadve.weather.viewmodel.AddCityViewModel;
import com.arlib.floatingsearchview.FloatingSearchView;

import java.util.HashMap;
import java.util.Map;

import io.realm.Realm;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Ajinkya on 28-06-2016.
 */
public class AddCityDialogFragment extends AppCompatDialogFragment {
    private static final String TAG = AddCityDialogFragment.class.getSimpleName();
    private AddCityViewModel addCityViewModel;
//    private FloatingSearchView floatingSearchView;
    FragmentDialogAddCityBinding fragmentDialogAddCityBinding;
    private Subscription subscription;


    public AddCityDialogFragment() {
    }

    public static AddCityDialogFragment newInstance(String title) {
        AddCityDialogFragment frag = new AddCityDialogFragment();
        Bundle args = new Bundle();
        args.putString("title", title);
        frag.setArguments(args);
        return frag;
    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setStyle(DialogFragment.STYLE_NO_TITLE, R.style.AppDialogTheme);
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        fragmentDialogAddCityBinding = DataBindingUtil.inflate(inflater, R.layout.fragment_dialog_add_city, container, false);
        addCityViewModel = new AddCityViewModel(getContext());
        View v = fragmentDialogAddCityBinding.getRoot();
//        fragmentDialogAddCityBinding.floatingSearchView.setOnQueryChangeListener(new FloatingSearchView.OnQueryChangeListener() {
//            @Override
//            public void onSearchTextChanged(String oldQuery, String newQuery) {
//
//                //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Pune&types=(cities)&sensor=false&key=AIzaSyAkpLDW9FyaYi5qKyTfl1x1115BoMZGm1I
//
//                if (subscription != null && !subscription.isUnsubscribed())
//                    subscription.unsubscribe();
//                WeatherApplication weatherApplication = WeatherApplication.get(getContext());
//                OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapPlacesService();
//                Map<String, String> queryParam = new HashMap<>();
//                queryParam.put("types", "(cities)");
//                queryParam.put("sensor", "false");
//                queryParam.put("key", "AIzaSyAn6kVvewAcUePDlmLa7Ll-ZdAhQrIMmU0");//
//
//
//                subscription = openWeatherMapService.getPlaces(newQuery, queryParam)
//                        .observeOn(AndroidSchedulers.mainThread())
//                        .subscribeOn(weatherApplication.defaultSubscribeScheduler())
//                        .subscribe(new Subscriber<Places>() {
//                            @Override
//                            public void onCompleted() {
//
//                                Log.d(TAG, "onCompleted() called with: " + "");
//
//                            }
//
//                            @Override
//                            public void onError(Throwable e) {
//                                Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
//
//                            }
//
//                            @Override
//                            public void onNext(Places Places) {
//                                Log.d(TAG, "onNext() called with: " + "openWeatherMap = [" + Places + "]");
//
//                            }
//                        });
//
//            }
//        });


        return v;
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }
}
