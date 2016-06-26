
package com.ajinkyabadve.weather.model.realm;

import io.realm.RealmList;
import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class CityRealm extends RealmObject {

    @PrimaryKey
    private Integer id;
    private String name;
    private CoordRealm coordRealm;
    private String country;
    private Integer population;
    private String cod;
    private Double message;
    private Integer cnt;
    private RealmList<ListRealm> listRealm = new RealmList<ListRealm>();


    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public CoordRealm getCoordRealm() {
        return coordRealm;
    }

    public void setCoordRealm(CoordRealm coordRealm) {
        this.coordRealm = coordRealm;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public Integer getPopulation() {
        return population;
    }

    public void setPopulation(Integer population) {
        this.population = population;
    }

    public String getCod() {
        return cod;
    }

    public void setCod(String cod) {
        this.cod = cod;
    }

    public Double getMessage() {
        return message;
    }

    public void setMessage(Double message) {
        this.message = message;
    }

    public Integer getCnt() {
        return cnt;
    }

    public void setCnt(Integer cnt) {
        this.cnt = cnt;
    }

    public RealmList<ListRealm> getListRealm() {
        return listRealm;
    }

    public void setListRealm(RealmList<ListRealm> listRealm) {
        this.listRealm = listRealm;
    }

    @Override
    public String toString() {
        return "CityRealm{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", coordRealm=" + coordRealm +
                ", country='" + country + '\'' +
                ", population=" + population +
                ", cod='" + cod + '\'' +
                ", message=" + message +
                ", cnt=" + cnt +
                ", listRealm=" + listRealm +
                '}';
    }
}
