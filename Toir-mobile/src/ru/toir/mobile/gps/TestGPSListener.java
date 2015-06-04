package ru.toir.mobile.gps;

import java.util.Iterator;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.location.GpsStatus;
import android.location.GpsSatellite;
import android.os.Bundle;
import android.widget.TextView;

public class TestGPSListener implements LocationListener,GpsStatus.Listener{
	TextView gpsLog;
	String ls;
	String strGpsStats;
	private LocationManager lM;
	
	@Override
    public void onGpsStatusChanged(int event) {
        GpsStatus gpsStatus = lM.getGpsStatus(null);
        if(gpsStatus != null) {
            Iterable<GpsSatellite>satellites = gpsStatus.getSatellites();
            Iterator<GpsSatellite>sat = satellites.iterator();
            int i=0;
            while (sat.hasNext()) {
                GpsSatellite satellite = sat.next();
                strGpsStats+= (i++) + ": " + satellite.getPrn() + "," + satellite.usedInFix() + "," + satellite.getSnr() + "," + satellite.getAzimuth() + "," + satellite.getElevation()+ "\n\n";
            }
            gpsLog.append(strGpsStats);
        }
    }

	public TestGPSListener(TextView tv) {
		gpsLog = tv;
		ls = System.getProperty("line.separator");
	}
	
	public Location getLastLocation() {
		Location location;
		location = lM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location;
	}
	@Override
	public void onLocationChanged(Location location) {
		gpsLog.append("GPS locatiton changed" + ls + "Latitude: "+ location.getLatitude() + ls + "Longitude: " + location.getLongitude() + ls);
		System.out.println("GPS locatiton changed");
	}

	@Override
	public void onProviderDisabled(String arg0) {
		gpsLog.append("GPS disabled" + ls);
		System.out.println("GPS disabled");		
	}

	@Override
	public void onProviderEnabled(String arg0) {
		gpsLog.append("GPS enabled" + ls);
		System.out.println("GPS enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle boundle) {
		gpsLog.append("GPS status changed, arg: " + provider + ls);
		System.out.println("GPS status changed, arg: " + provider);
	}
	
}
