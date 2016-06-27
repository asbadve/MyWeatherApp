package com.ajinkyabadve.weather.model;


import android.databinding.tool.util.L;

import com.ajinkyabadve.weather.WeatherApplication;

import java.util.*;
import java.util.List;

import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;
import rx.functions.Func1;

/**
 * Created by Ajinkya on 26-06-2016.
 */
public interface OpenWeatherMapService {
    //http://api.openweathermap.org/data/2.5/forecast/daily?q=Pune&cnt=14&APPID=8be06227a313736007f84b540e2aed5f
    public static final String BASE_URL = "http://api.openweathermap.org/data/2.5/forecast/";

    //?q={city}&cnt=14&APPID=8be06227a313736007f84b540e2aed5f
    @GET("daily")
    Observable<OpenWeatherMap> getWeatherForeCastByCity(@Query("q") String city, @QueryMap Map<String, String> options);

    Observable getByCityList(List<String> cities, Map<String, String> queryParam);


    class Factory {
        public static OpenWeatherMapService create() {
            Retrofit retrofit = new Retrofit.Builder()
                    .baseUrl(BASE_URL)
                    .addConverterFactory(GsonConverterFactory.create())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .build();
            return retrofit.create(OpenWeatherMapService.class);
        }


    }


}

