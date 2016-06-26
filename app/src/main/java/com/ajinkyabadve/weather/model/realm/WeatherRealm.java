
package com.ajinkyabadve.weather.model.realm;

import io.realm.RealmObject;
import io.realm.annotations.PrimaryKey;

public class WeatherRealm extends RealmObject {
    @PrimaryKey
    private Integer primaryKeyId;

    private Integer id;
    private String main;
    private String description;
    private String icon;

    /**
     * @return The id
     */
    public Integer getId() {
        return id;
    }

    /**
     * @param id The id
     */
    public void setId(Integer id) {
        this.id = id;
    }

    /**
     * @return The main
     */
    public String getMain() {
        return main;
    }

    /**
     * @param main The main
     */
    public void setMain(String main) {
        this.main = main;
    }

    /**
     * @return The description
     */
    public String getDescription() {
        return description;
    }

    /**
     * @param description The description
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * @return The icon
     */
    public String getIcon() {
        return icon;
    }

    /**
     * @param icon The icon
     */
    public void setIcon(String icon) {
        this.icon = icon;
    }


    @Override
    public String toString() {
        return "WeatherRealm{" +
                "id=" + id +
                ", main='" + main + '\'' +
                ", description='" + description + '\'' +
                ", icon='" + icon + '\'' +
                '}';
    }

    public Integer getPrimaryKeyId() {
        return primaryKeyId;
    }

    public void setPrimaryKeyId(Integer primaryKeyId) {
        this.primaryKeyId = primaryKeyId;
    }
}
