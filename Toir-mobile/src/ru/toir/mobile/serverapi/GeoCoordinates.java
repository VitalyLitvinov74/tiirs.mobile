
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

public class GeoCoordinates {

    @Expose
    private Double Latitude;
    @Expose
    private Double Longitude;

    /**
     * 
     * @return
     *     The Latitude
     */
    public Double getLatitude() {
        return Latitude;
    }

    /**
     * 
     * @param Latitude
     *     The Latitude
     */
    public void setLatitude(Double Latitude) {
        this.Latitude = Latitude;
    }

    /**
     * 
     * @return
     *     The Longitude
     */
    public Double getLongitude() {
        return Longitude;
    }

    /**
     * 
     * @param Longitude
     *     The Longitude
     */
    public void setLongitude(Double Longitude) {
        this.Longitude = Longitude;
    }

}
