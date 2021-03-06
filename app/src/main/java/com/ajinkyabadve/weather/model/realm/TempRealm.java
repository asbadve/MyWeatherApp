
package com.ajinkyabadve.weather.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class TempRealm extends RealmObject {
    @PrimaryKey
    private String id;//combination of dt and city id

    private Double day;
    private Double min;
    private Double max;
    private Double night;
    private Double eve;
    private Double morn;

    /**
     * @return The day
     */
    public Double getDay() {
        return day;
    }

    /**
     * @param day The day
     */
    public void setDay(Double day) {
        this.day = day;
    }

    /**
     * @return The min
     */
    public Double getMin() {
        return min;
    }

    /**
     * @param min The min
     */
    public void setMin(Double min) {
        this.min = min;
    }

    /**
     * @return The max
     */
    public Double getMax() {
        return max;
    }

    /**
     * @param max The max
     */
    public void setMax(Double max) {
        this.max = max;
    }

    /**
     * @return The night
     */
    public Double getNight() {
        return night;
    }

    /**
     * @param night The night
     */
    public void setNight(Double night) {
        this.night = night;
    }

    /**
     * @return The eve
     */
    public Double getEve() {
        return eve;
    }

    /**
     * @param eve The eve
     */
    public void setEve(Double eve) {
        this.eve = eve;
    }

    /**
     * @return The morn
     */
    public Double getMorn() {
        return morn;
    }

    /**
     * @param morn The morn
     */
    public void setMorn(Double morn) {
        this.morn = morn;
    }

    @Override
    public String toString() {
        return "TempRealm{" +
                "id='" + id + '\'' +
                ", day=" + day +
                ", min=" + min +
                ", max=" + max +
                ", night=" + night +
                ", eve=" + eve +
                ", morn=" + morn +
                '}';
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }
}
