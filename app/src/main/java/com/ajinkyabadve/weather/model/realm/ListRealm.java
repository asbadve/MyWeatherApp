
package com.ajinkyabadve.weather.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class ListRealm extends RealmObject {

    @PrimaryKey
    private Integer dt;
    private TempRealm tempRealm;
    private Double pressure;
    private Integer humidity;
    private RealmList<WeatherRealm> weatherRealm = new RealmList<WeatherRealm>();
    private Double speed;
    private Integer deg;
    private Integer clouds;
    private Double rain;

    /**
     * @return The dt
     */
    public Integer getDt() {
        return dt;
    }

    /**
     * @param dt The dt
     */
    public void setDt(Integer dt) {
        this.dt = dt;
    }

    /**
     * @return The tempRealm
     */
    public TempRealm getTempRealm() {
        return tempRealm;
    }

    /**
     * @param tempRealm The tempRealm
     */
    public void setTempRealm(TempRealm tempRealm) {
        this.tempRealm = tempRealm;
    }

    /**
     * @return The pressure
     */
    public Double getPressure() {
        return pressure;
    }

    /**
     * @param pressure The pressure
     */
    public void setPressure(Double pressure) {
        this.pressure = pressure;
    }

    /**
     * @return The humidity
     */
    public Integer getHumidity() {
        return humidity;
    }

    /**
     * @param humidity The humidity
     */
    public void setHumidity(Integer humidity) {
        this.humidity = humidity;
    }

    /**
     * @return The weatherRealm
     */
    public RealmList<WeatherRealm> getWeatherRealm() {
        return weatherRealm;
    }

    /**
     * @param weatherRealm The weatherRealm
     */
    public void setWeatherRealm(RealmList<WeatherRealm> weatherRealm) {
        this.weatherRealm = weatherRealm;
    }

    /**
     * @return The speed
     */
    public Double getSpeed() {
        return speed;
    }

    /**
     * @param speed The speed
     */
    public void setSpeed(Double speed) {
        this.speed = speed;
    }

    /**
     * @return The deg
     */
    public Integer getDeg() {
        return deg;
    }

    /**
     * @param deg The deg
     */
    public void setDeg(Integer deg) {
        this.deg = deg;
    }

    /**
     * @return The clouds
     */
    public Integer getClouds() {
        return clouds;
    }

    /**
     * @param clouds The clouds
     */
    public void setClouds(Integer clouds) {
        this.clouds = clouds;
    }

    /**
     * @return The rain
     */
    public Double getRain() {
        return rain;
    }

    /**
     * @param rain The rain
     */
    public void setRain(Double rain) {
        this.rain = rain;
    }

    @Override
    public String toString() {
        return "ListRealm{" +
                "dt=" + dt +
                ", tempRealm=" + tempRealm +
                ", pressure=" + pressure +
                ", humidity=" + humidity +
                ", weatherRealm=" + weatherRealm +
                ", speed=" + speed +
                ", deg=" + deg +
                ", clouds=" + clouds +
                ", rain=" + rain +
                '}';
    }
}
