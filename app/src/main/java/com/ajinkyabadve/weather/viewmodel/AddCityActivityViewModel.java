package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.databinding.ObservableInt;
import android.support.annotation.IntDef;
import android.support.annotation.Nullable;
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

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.HashMap;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * View model class fot the AddCity activity
 * Created by Ajinkya on 29/06/2016.
 */
public class AddCityActivityViewModel implements ViewModel, RealmChangeListener<RealmResults<CityRealm>> {
    private static final String TAG = AddCityActivityViewModel.class.getSimpleName();
    private SharedPreferenceDataManager sharedPreferenceDataManager;
    private Subscription subscription;
    private Context context;
    public ObservableInt progressVisibility;
    private ActivityModelCommunicationListener activityModelCommunicationListener;
    private RealmResults<CityRealm> cityRealms;
    public ObservableInt recyclerVisibility;

    /**
     * show progress bar and hides the recycler view
     */
    public void showProgressBar() {
        recyclerVisibility.set(View.INVISIBLE);
        progressVisibility.set(View.VISIBLE);
    }

    /**
     * hide the progress bar and shows the recycler view
     */
    public void hideProgressBar() {
        progressVisibility.set(View.INVISIBLE);
        recyclerVisibility.set(View.VISIBLE);
    }


    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FLAG_CITY_ALREADY_PRESENT, FLAG_CITY_WEATHER_NOT_AVAILABLE, FLAG_CITY_SOMETHING_WENT_WRONG, FLAG_CITY_NOT_FOUND, FLAG_CITY_NOT_MATCH, FLAG_INTERNET_NOT_AVAILABLE})
    /***
     *Error code for the add city
     */
    public @interface AddCityErrorFlag {

    }

    public static final int FLAG_CITY_ALREADY_PRESENT = 1;
    public static final int FLAG_CITY_WEATHER_NOT_AVAILABLE = 2;
    public static final int FLAG_CITY_SOMETHING_WENT_WRONG = 3;
    public static final int FLAG_CITY_NOT_FOUND = 4;
    public static final int FLAG_CITY_NOT_MATCH = 5;
    public static final int FLAG_INTERNET_NOT_AVAILABLE = 6;

    /***
     * @param context                            context
     * @param activityModelCommunicationListener {@link ActivityModelCommunicationListener}
     */
    public AddCityActivityViewModel(Context context, ActivityModelCommunicationListener activityModelCommunicationListener) {
        this.context = context;
        recyclerVisibility = new ObservableInt(View.VISIBLE);
        progressVisibility = new ObservableInt(View.INVISIBLE);
        this.activityModelCommunicationListener = activityModelCommunicationListener;
        sharedPreferenceDataManager = SharedPreferenceDataManager.getInstance(context);
    }


    /**
     * listener to communicate with the activity
     */
    public interface ActivityModelCommunicationListener {
        /**
         * called when their is an error regarding the add city
         *
         * @param errorFlag
         * @param message
         */
        void onCityAddedError(@AddCityErrorFlag int errorFlag, @Nullable String message);

        /***
         * called when city get added successfully
         *
         * @param cityRealms
         * @param sharedPreferenceDataManager
         */
        void onCityAdded(RealmResults<CityRealm> cityRealms, SharedPreferenceDataManager sharedPreferenceDataManager);

    }


    @Override
    public void onDestroy() {
        this.activityModelCommunicationListener = null;
        this.activityModelCommunicationListener = null;
    }


    @Override
    public void onStart() {
        Realm realm = Realm.getDefaultInstance();
        cityRealms = realm.where(CityRealm.class).findAll();
        if (activityModelCommunicationListener != null) {
            activityModelCommunicationListener.onCityAdded(cityRealms, sharedPreferenceDataManager);
        }
        cityRealms.addChangeListener(this);
    }


    @Override
    public void Stop() {
        cityRealms.removeChangeListener(this);
    }

    /**
     * Validate the place form autocomplete api
     * and forecast for the city
     *
     * @param cityName city name for which the weather can be fetch
     */
    public void checkIfPlaceIsValid(String cityName) {
        recyclerVisibility.set(View.INVISIBLE);
        progressVisibility.set(View.VISIBLE);
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
                        progressVisibility.set(View.INVISIBLE);
                        recyclerVisibility.set(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
                        if (!Util.isNetworkAvailable(context)) {
                            activityModelCommunicationListener.onCityAddedError(FLAG_INTERNET_NOT_AVAILABLE, null);
                        } else if (e != null) {
                            activityModelCommunicationListener.onCityAddedError(FLAG_CITY_SOMETHING_WENT_WRONG, e.getMessage());
                        } else {
                            activityModelCommunicationListener.onCityAddedError(FLAG_CITY_SOMETHING_WENT_WRONG, null);

                        }
                        progressVisibility.set(View.INVISIBLE);
                        recyclerVisibility.set(View.VISIBLE);


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
                                    activityModelCommunicationListener.onCityAddedError(FLAG_CITY_ALREADY_PRESENT, null);
                                } else if (!openWeatherMap.getCity().getName().equals(city)) {
                                    activityModelCommunicationListener.onCityAddedError(FLAG_CITY_NOT_MATCH, null);
                                }
                            }

                        } else {
                            activityModelCommunicationListener.onCityAddedError(FLAG_CITY_NOT_FOUND, null);

                        }


                    }
                });

    }

    /***
     * add weather for city by using latlong
     *
     * @param latitude
     * @param longitude
     */

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
                        Log.d(TAG, "onCompleted() called with: " + "");
                        progressVisibility.set(View.INVISIBLE);
                        recyclerVisibility.set(View.VISIBLE);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
                        if (!Util.isNetworkAvailable(context)) {
                            activityModelCommunicationListener.onCityAddedError(FLAG_INTERNET_NOT_AVAILABLE, null);
                        } else if (e != null) {
                            activityModelCommunicationListener.onCityAddedError(FLAG_CITY_SOMETHING_WENT_WRONG, e.getMessage());

                        } else {
                            activityModelCommunicationListener.onCityAddedError(FLAG_CITY_SOMETHING_WENT_WRONG, null);

                        }
                        progressVisibility.set(View.INVISIBLE);
                        recyclerVisibility.set(View.VISIBLE);

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
                                    activityModelCommunicationListener.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_ALREADY_PRESENT, null);
                                }
                            }

                        } else {
                            activityModelCommunicationListener.onCityAddedError(AddCityActivityViewModel.FLAG_CITY_NOT_FOUND, null);

                        }

                    }
                });

    }

    @Override
    public void onChange(RealmResults<CityRealm> element) {
        if (activityModelCommunicationListener != null) {
            cityRealms = element;
            activityModelCommunicationListener.onCityAdded(cityRealms, sharedPreferenceDataManager);

        }
    }
}
