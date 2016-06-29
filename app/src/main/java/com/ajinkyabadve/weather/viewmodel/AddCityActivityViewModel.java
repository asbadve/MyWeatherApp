package com.ajinkyabadve.weather.viewmodel;

import android.content.Context;
import android.support.annotation.IntDef;
import android.util.Log;

import com.ajinkyabadve.weather.R;
import com.ajinkyabadve.weather.WeatherApplication;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.OpenWeatherMapService;
import com.ajinkyabadve.weather.model.realm.CityRealm;
import com.ajinkyabadve.weather.model.realm.ListRealm;
import com.ajinkyabadve.weather.model.realm.RealmUtil;
import com.google.android.gms.location.places.Place;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import io.realm.Case;
import io.realm.Realm;
import io.realm.RealmChangeListener;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import io.realm.Sort;
import rx.Subscriber;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;

/**
 * Created by Ajinkya on 29/06/2016.
 */
public class AddCityActivityViewModel implements ViewModel, RealmChangeListener<RealmResults<CityRealm>> {
    private static final String TAG = AddCityActivityViewModel.class.getSimpleName();
    private Subscription subscription;
    private Context context;
    private ActivityModelCommunicationListener activityModelCommunicationListener;
    private RealmResults<CityRealm> cityRealms;

    @Retention(RetentionPolicy.SOURCE)
    @IntDef({FLAG_CITY_ALREADY_PRESENT, FLAG_CITY_WEATHER_NOT_AVAILABLE, FLAG_CITY_SOMETHING_WENT_WRONG})
    public @interface AddCityErrorFlag {

    }

    public static final int FLAG_CITY_ALREADY_PRESENT = 1;

    public static final int FLAG_CITY_WEATHER_NOT_AVAILABLE = 2;

    public static final int FLAG_CITY_SOMETHING_WENT_WRONG = 3;


    @Override
    public void onChange(RealmResults<CityRealm> element) {
        if (activityModelCommunicationListener != null) {
            Realm realm = Realm.getDefaultInstance();
            RealmResults<CityRealm> cityRealms = realm.where(CityRealm.class).findAll();
            activityModelCommunicationListener.onCityAdded(cityRealms);

        }
    }

    public interface ActivityModelCommunicationListener {
        void onCityAddedError(@AddCityErrorFlag int errorFlag);

        void onCityAdded(RealmResults<CityRealm> cityRealms);

    }

    public AddCityActivityViewModel(Context context, ActivityModelCommunicationListener activityModelCommunicationListener) {
        this.context = context;
        this.activityModelCommunicationListener = activityModelCommunicationListener;
    }

    @Override
    public void onDestroy() {

    }


    @Override
    public void onStart() {
        Realm realm = Realm.getDefaultInstance();
        cityRealms = realm.where(CityRealm.class).findAll();
        if (activityModelCommunicationListener != null) {
            activityModelCommunicationListener.onCityAdded(cityRealms);
        }
        cityRealms.addChangeListener(this);
    }

    @Override
    public void Stop() {
        cityRealms.removeChangeListener(this);
    }

    /**
     * Validate the place form autocomplete api
     *
     * @param place
     */
    public void checkIfPlaceIsValid(final Place place) {

        if (subscription != null && !subscription.isUnsubscribed()) subscription.unsubscribe();
        WeatherApplication weatherApplication = WeatherApplication.get(context);
        OpenWeatherMapService openWeatherMapService = weatherApplication.getOpenWeatherMapService();
        Map<String, String> queryParam = new HashMap<>();
        queryParam.put("cnt", context.getString(R.string.cnt_parameter_for_days));
        queryParam.put("APPID", context.getString(R.string.open_weather_map));

        subscription = openWeatherMapService.getWeatherForeCastByCity(place.getName().toString(), queryParam)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(weatherApplication.defaultSubscribeScheduler())
                .subscribe(new Subscriber<OpenWeatherMap>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted() called with: " + "");

                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError() called with: " + "e = [" + e + "]");
                        activityModelCommunicationListener.onCityAddedError(FLAG_CITY_SOMETHING_WENT_WRONG);
                    }

                    @Override
                    public void onNext(OpenWeatherMap openWeatherMap) {
                        Log.d(TAG, "onNext() called with: " + "openWeatherMap = [" + openWeatherMap + "]");
                        Realm realm = Realm.getDefaultInstance();
                        CityRealm cityRealmTemp = RealmUtil.getCityWithWeather(openWeatherMap);
                        RealmQuery<CityRealm> cityRealms = realm.where(CityRealm.class).contains("name", cityRealmTemp.getName(), Case.SENSITIVE);
                        if (openWeatherMap.getCity().getName().equals(place.getName().toString()) && cityRealms.count() == 0) {
                            realm.beginTransaction();
                            cityRealmTemp.setDefault(true);
                            CityRealm cityRealm = realm.copyToRealmOrUpdate(cityRealmTemp);


                            RealmResults<CityRealm> cityRealmRealmQuery = realm.where(CityRealm.class).findAll().where().equalTo("id", cityRealm.getId()).not().findAll();
                            RealmList<CityRealm> cityRealms1 = new RealmList<CityRealm>();
                            cityRealms1.addAll(cityRealmRealmQuery.subList(0, cityRealmRealmQuery.size()));
                            for (int i = 0; i < cityRealms1.size(); i++) {
                                cityRealms1.get(i).setDefault(false);
                            }


                            realm.commitTransaction();

                        } else {
                            if (cityRealms.count() > 0) {
                                activityModelCommunicationListener.onCityAddedError(FLAG_CITY_ALREADY_PRESENT);
                            } else if (!openWeatherMap.getCity().getName().equals(place.getName().toString())) {
                                activityModelCommunicationListener.onCityAddedError(FLAG_CITY_WEATHER_NOT_AVAILABLE);
                            }
                        }


                    }
                });

    }
}
