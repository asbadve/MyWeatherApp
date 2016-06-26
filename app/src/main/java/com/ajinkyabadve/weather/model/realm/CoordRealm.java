
package com.ajinkyabadve.weather.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CoordRealm extends RealmObject {

    @PrimaryKey
    private Integer id;

    private Double lon;
    private Double lat;

    /**
     * 
     * @return
     *     The lon
     */
    public Double getLon() {
        return lon;
    }

    /**
     * 
     * @param lon
     *     The lon
     */
    public void setLon(Double lon) {
        this.lon = lon;
    }

    /**
     * 
     * @return
     *     The lat
     */
    public Double getLat() {
        return lat;
    }

    /**
     * 
     * @param lat
     *     The lat
     */
    public void setLat(Double lat) {
        this.lat = lat;
    }

    @Override
    public String toString() {
        return "CoordRealm{" +
                "lon=" + lon +
                ", lat=" + lat +
                '}';
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
