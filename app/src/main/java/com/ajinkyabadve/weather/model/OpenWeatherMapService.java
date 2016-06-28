package com.ajinkyabadve.weather.model;


import java.util.*;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Ajinkya on 26-06-2016.
 */
public interface OpenWeatherMapService {


    //http://api.openweathermap.org/data/2.5/forecast/daily?q=Pune&cnt=14&APPID=8be06227a313736007f84b540e2aed5f
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/";

    //?q={city}&cnt=14&APPID=8be06227a313736007f84b540e2aed5f
    @GET("daily")
    Observable<OpenWeatherMap> getWeatherForeCastByCity(@Query("q") String city, @QueryMap Map<String, String> options);


    //https://maps.googleapis.com/maps/api/place/autocomplete/json?input=Pune&types=(cities)&sensor=false&key=AIzaSyAkpLDW9FyaYi5qKyTfl1x1115BoMZGm1I
    public static final String BASE_URL_LOCATION = "https://maps.googleapis.com/maps/api/place/autocomplete/";

    @GET("json")
    Observable<Places> getPlaces(@Query("input") String query, @QueryMap Map<String, String> options);


    class Factory {
        public static OpenWeatherMapService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(OpenWeatherMapService.class);


        }

        public static OpenWeatherMapService cretePlaceService() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL_LOCATION)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(OpenWeatherMapService.class);
        }


    }


}

