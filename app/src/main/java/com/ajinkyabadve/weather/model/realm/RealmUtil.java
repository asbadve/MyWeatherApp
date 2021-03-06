package com.ajinkyabadve.weather.model.realm;

import com.ajinkyabadve.weather.model.City;
import com.ajinkyabadve.weather.model.Coord;
import com.ajinkyabadve.weather.model.List;
import com.ajinkyabadve.weather.model.OpenWeatherMap;
import com.ajinkyabadve.weather.model.Temp;
import com.ajinkyabadve.weather.model.Weather;
import com.ajinkyabadve.weather.util.Util;

import java.util.ArrayList;
import java.util.Date;

import io.realm.RealmList;

/**
 * TODO: 27/06/2016 create builder method for the non realm class to convert it to relam class
 * Created by Ajinkya on 26-06-2016.
 */
public class RealmUtil {
    public static CityRealm getCityWithWeather(OpenWeatherMap openWeatherMap) {
        City city = openWeatherMap.getCity();
        Coord coord = city.getCoord();


        CityRealm cityRealm = new CityRealm();
        cityRealm.setId(city.getId());
        cityRealm.setName(city.getName());

        CoordRealm coordRealm = new CoordRealm();
        coordRealm.setLat(coord.getLat());
        coordRealm.setLon(coord.getLon());
        coordRealm.setId(city.getId());//explicit given proimary key

        cityRealm.setCountry(city.getCountry());
        cityRealm.setPopulation(city.getPopulation());
        cityRealm.setCod(openWeatherMap.getCod());
        cityRealm.setMessage(openWeatherMap.getMessage());
        cityRealm.setCnt(openWeatherMap.getCnt());
        cityRealm.setCoordRealm(coordRealm);
        cityRealm.setListRealm(getListRealm(openWeatherMap.getList(), cityRealm));
        return cityRealm;
    }

    private static RealmList<ListRealm> getListRealm(java.util.List<List> listFromOpenWeatherMap, CityRealm cityRealm) {
        RealmList<ListRealm> listRealmRealmList = new RealmList<ListRealm>();
        for (int i = 0; i < listFromOpenWeatherMap.size(); i++) {
            List listObjectFromOpenWM = listFromOpenWeatherMap.get(i);
            ListRealm listRealm = new ListRealm();


            int dateToSave = Util.getDbDateLong(new Date(listObjectFromOpenWM.getDt() * 1000L));


            listRealm.setDtAndCityId(dateToSave + "," + cityRealm.getId());


            listRealm.setDt(dateToSave);
            Temp temp = listObjectFromOpenWM.getTemp();
            TempRealm tempRealm = new TempRealm();
            tempRealm.setId(listObjectFromOpenWM.getDt() + "," + cityRealm.getId());
            tempRealm.setDay(temp.getDay());
            tempRealm.setMin(temp.getMin());
            tempRealm.setMax(temp.getMax());
            tempRealm.setNight(temp.getNight());
            tempRealm.setEve(temp.getEve());
            tempRealm.setMorn(temp.getMorn());
            listRealm.setTempRealm(tempRealm);
            listRealm.setPressure(listObjectFromOpenWM.getPressure());
            listRealm.setHumidity(listObjectFromOpenWM.getHumidity());

            java.util.List<Weather> listWeather = listObjectFromOpenWM.getWeather();
            RealmList<WeatherRealm> weatherRealmRealmList = new RealmList<WeatherRealm>();
            for (int i1 = 0; i1 < listWeather.size(); i1++) {
                Weather weather = listWeather.get(i1);
                WeatherRealm weatherRealm = new WeatherRealm();
                weatherRealm.setPrimaryKeyId(listObjectFromOpenWM.getDt() + "," + cityRealm.getId());
                weatherRealm.setId(weather.getId());
                weatherRealm.setMain(weather.getMain());
                weatherRealm.setDescription(weather.getDescription());
                weatherRealm.setIcon(weather.getIcon());
                weatherRealmRealmList.add(weatherRealm);
            }
            listRealm.setWeatherRealm(weatherRealmRealmList);
            listRealm.setSpeed(listObjectFromOpenWM.getSpeed());
            listRealm.setDeg(listObjectFromOpenWM.getDeg());
            listRealm.setClouds(listObjectFromOpenWM.getClouds());
            listRealm.setRain(listObjectFromOpenWM.getRain());
            listRealmRealmList.add(listRealm);
        }
        return listRealmRealmList;
    }

    public static java.util.List<CityRealm> getCityWithWeather(java.util.List<OpenWeatherMap> openWeatherMapList) {
        java.util.List<CityRealm> cityRealmList = new ArrayList<>();
        for (int i = 0; i < openWeatherMapList.size(); i++) {
            OpenWeatherMap openWeatherMap = openWeatherMapList.get(i);
            City city = openWeatherMap.getCity();
            Coord coord = city.getCoord();


            CityRealm cityRealm = new CityRealm();
            cityRealm.setId(city.getId());
            cityRealm.setName(city.getName());

            CoordRealm coordRealm = new CoordRealm();
            coordRealm.setLat(coord.getLat());
            coordRealm.setLon(coord.getLon());
            coordRealm.setId(city.getId());//explicit given proimary key

            cityRealm.setCountry(city.getCountry());
            cityRealm.setPopulation(city.getPopulation());
            cityRealm.setCod(openWeatherMap.getCod());
            cityRealm.setMessage(openWeatherMap.getMessage());
            cityRealm.setCnt(openWeatherMap.getCnt());
            cityRealm.setCoordRealm(coordRealm);
            cityRealm.setListRealm(getListRealm(openWeatherMap.getList(), cityRealm));
            cityRealmList.add(cityRealm);
        }

        return cityRealmList;
    }
}
