package com.adrey.ojekonline;

/**
 * Created by Muh Adrey Fatawallah on 11/9/2016.
 */

class Nearby {

    private String icon;
    private String name;
    private String vicinity;
    private String lat;
    private String log;

    public String getIcon() {
        return icon;
    }

    public void setIcon(String icon) {
        this.icon = icon;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    String getVicinity() {
        return vicinity;
    }

    void setVicinity(String vicinity) {
        this.vicinity = vicinity;
    }

    String getLat() {
        return lat;
    }

    void setLat(String lat) {
        this.lat = lat;
    }

    String getLog() {
        return log;
    }

    void setLog(String log) {
        this.log = log;
    }

    @Override
    public String toString() {
        return "Nearby{" +
                "icon='" + icon + '\'' +
                ", name='" + name + '\'' +
                ", vicinity='" + vicinity + '\'' +
                ", lat='" + lat + '\'' +
                ", log='" + log + '\'' +
                '}';
    }
}
