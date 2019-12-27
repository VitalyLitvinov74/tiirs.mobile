
package ru.toir.mobile.serverapi;

import com.google.gson.annotations.Expose;

/**
 * Представления географических координат
 * @author Dmitriy Logachov
 *
 */
public class GeoCoordinatesSrv {

    @Expose
    private float Latitude;
    @Expose
    private float Longitude;

    /**
     * 
     * @return
     *     The Latitude
     */
    public float getLatitude() {
        return Latitude;
    }

    /**
     * 
     * @param Latitude
     *     The Latitude
     */
    public void setLatitude(float Latitude) {
        this.Latitude = Latitude;
    }

    /**
     * 
     * @return
     *     The Longitude
     */
    public float getLongitude() {
        return Longitude;
    }

    /**
     * 
     * @param Longitude
     *     The Longitude
     */
    public void setLongitude(float Longitude) {
        this.Longitude = Longitude;
    }

}
