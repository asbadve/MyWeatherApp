package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.BaseObservable;
import android.databinding.ObservableInt;
import android.util.Log;
import android.view.View;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.WeatherApplication;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.RealmUtil;
import com.ajinkyabadve.weather.util.SharedPreferenceDataManager;
import com.ajinkyabadve.weather.util.Util;

import java.util.HashMap;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmQuery;
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
    private SharedPreferenceDataManager sharedPreferenceDataManager;
    public ObservableInt recyclerViewVisibility;
    public ObservableInt progressBarVisibility;

    private Subscription subscription;
    private Context context;
    private MainViewModel.onCityAddeByLatLong onCityAddeByLatLong;

    public MainViewModel(Context context, OnDialogShow onDialogShow, onCityAddeByLatLong onCityAddeByLatLong) {
        this.onCityAddeByLatLong = onCityAddeByLatLong;
        recyclerViewVisibility = new ObservableInt(View.VISIBLE);
        progressBarVisibility = new ObservableInt(View.INVISIBLE);
        realm = Realm.getDefaultInstance();
        this.context = context;
        this.onDialogShow = onDialogShow;
        sharedPreferenceDataManager = SharedPreferenceDataManager.getInstance(context);
        checkAnyCityAddedOrNot();
        //loadWeather();
    }

    public void hideProgressBar() {
        recyclerViewVisibility.set(View.VISIBLE);
        progressBarVisibility.set(View.INVISIBLE);


    }

    public void showProgressBar() {
        recyclerViewVisibility.set(View.INVISIBLE);
        progressBarVisibility.set(View.VISIBLE);
    }

    public void addCityByLatLong(double latitude, double longitude) {
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        WeatherApplication weatherApplication = WeatherApplication.get(context);
        OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();
        Map<String, String> queryParam = new HashMap<>();

        queryParam.put("cnt", context.getString(R.string.cnt_parameter_for_days));
        queryParam.put("APPID", context.getString(R.string.open_weather_map));
        queryParam.put("units", context.getString(R.string.unit_param));


        subscription = openWeatherMapService.getWeatherForecastByLatLong(String.valueOf(latitude), String.valueOf(longitude), queryParam)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(weatherApplication.defaultSubscribeScheduler())
                .subscribe(new Subscriber<OpenWeatherMap>() {
                    @Override
                    public void onCompleted() {
                        hideProgressBar();
                    }

                    @Override
                    public void onError(Throwable e) {
                        hideProgressBar();

                        if (!Util.isNetworkAvailable(context)) {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_INTERNET_NOT_AVAILABLE, null);
                        } else if (e != null) {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_SOMETHING_WENT_WRONG, e.getMessage());
                        } else {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_SOMETHING_WENT_WRONG, null);

                        }

                    }

                    @Override
                    public void onNext(OpenWeatherMap openWeatherMap) {


                        if (!openWeatherMap.getCod().equalsIgnoreCase("404")) {
                            Realm realm = Realm.getDefaultInstance();
                            CityRealm cityRealmTemp = RealmUtil.getCityWithWeather(openWeatherMap);
                            RealmQuery<CityRealm> cityRealms = realm.where(CityRealm.class).contains("name", cityRealmTemp.getName(), Case.SENSITIVE);
                            if (cityRealms.count() == 0) {
                                realm.beginTransaction();
                                CityRealm cityRealm = realm.copyToRealmOrUpdate(cityRealmTemp);
                                if (sharedPreferenceDataManager != null) {
                                    sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID, cityRealm.getId());
                                    sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.FIREST_LAUCH, 2);

                                } else {
                                    sharedPreferenceDataManager = SharedPreferenceDataManager.getInstance(context);
                                    sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID, cityRealm.getId());
                                    sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.FIREST_LAUCH, 2);
                                }

                                realm.commitTransaction();

                            } else {
                                if (cityRealms.count() > 0) {
                                    onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_ALREADY_PRESENT, null);
                                }
                            }

                        } else {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_NOT_FOUND, null);

                        }

                    }
                });


    }

    /**
     * Validate the place form autocomplete api
     *
     * @param cityName
     */
    public void checkIfPlaceIsValid(String cityName) {
        showProgressBar();
        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        WeatherApplication weatherApplication = WeatherApplication.get(context);
        OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("cnt", context.getString(R.string.cnt_parameter_for_days));
        queryParam.put("APPID", context.getString(R.string.open_weather_map));
        queryParam.put("units", context.getString(R.string.unit_param));
        final String city = cityName;
        subscription = openWeatherMapService.getWeatherForeCastByCity(city, queryParam)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(weatherApplication.defaultSubscribeScheduler())
                .subscribe(new Subscriber<OpenWeatherMap>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called with: " + "");
                        hideProgressBar();


                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
                        if (!Util.isNetworkAvailable(context)) {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_INTERNET_NOT_AVAILABLE, null);
                        } else if (e != null) {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_SOMETHING_WENT_WRONG, e.getMessage());
                        } else {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_SOMETHING_WENT_WRONG, null);

                        }
                        hideProgressBar();

                    }

                    @Override
                    public void onNext(OpenWeatherMap openWeatherMap) {
                        Log.d(TAG, "onNext() called with: " + "openWeatherMap = [" + openWeatherMap + "]");
                        if (!openWeatherMap.getCod().equalsIgnoreCase("404")) {
                            Realm realm = Realm.getDefaultInstance();
                            CityRealm cityRealmTemp = RealmUtil.getCityWithWeather(openWeatherMap);
                            RealmQuery<CityRealm> cityRealms = realm.where(CityRealm.class).contains("name", cityRealmTemp.getName(), Case.SENSITIVE);
                            if (openWeatherMap.getCity().getName().equalsIgnoreCase(city) && cityRealms.count() == 0) {
                                realm.beginTransaction();
                                CityRealm cityRealm = realm.copyToRealmOrUpdate(cityRealmTemp);
                                if (sharedPreferenceDataManager != null) {
                                    sharedPreferenceDataManager.savePreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID, cityRealm.getId());
                                } else {
                                    SharedPreferenceDataManager.getInstance(context).savePreference(SharedPreferenceDataManager.SF_KEY_DEFAULT_CITY_ID, cityRealm.getId());
                                }
                                realm.commitTransaction();

                            } else {
                                if (cityRealms.count() > 0) {
                                    onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_ALREADY_PRESENT, null);
                                } else if (!openWeatherMap.getCity().getName().equals(city)) {
                                    onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_NOT_FOUND, null);
                                }
                            }

                        } else {
                            onCityAddeByLatLong.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_NOT_MATCH, null);

                        }


                    }
                });

    }

    public interface OnDialogShow {
        void onAddCityDialogShow(boolean showDialog);

    }

    public interface onCityAddeByLatLong {
        void onCityAddedByLatLong();

        //@AddCityErrorFlag
        void onCityAddedError(@AddCityActivityViewModel.AddCityErrorFlag int errorFlag, String message);


    }


    /***
     * checks any city has been added or not if not then open dialog fragment to add the city
     */
    private void checkAnyCityAddedOrNot() {
        long cityCount = realm.where(CityRealm.class).count();
        if (cityCount == 0) {
            if (onDialogShow != null) {
                int firstLunch = sharedPreferenceDataManager.getSavedIntPreference(SharedPreferenceDataManager.FIREST_LAUCH);

                if (firstLunch == 1) {
                    onDialogShow.onAddCityDialogShow(true);

                } else {
                    onDialogShow.onAddCityDialogShow(false);

                }
            }
        }

    }

    public void onFabClick(View view) {
        if (onDialogShow != null) {
            onDialogShow.onAddCityDialogShow(false);
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
