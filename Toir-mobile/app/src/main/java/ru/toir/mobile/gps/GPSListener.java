package ru.toir.mobile.gps;

import android.content.Context;
import android.content.Intent;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;

import java.util.Date;
import java.util.Iterator;

import io.realm.Realm;
import ru.toir.mobile.db.realm.GpsTrack;

public class GPSListener implements LocationListener, GpsStatus.Listener {

	String ls;
	String strGpsStats;
	private Context context;
	private String user_uuid;
	private LocationManager lM;

    public GPSListener(Context context, String uuid) {
        this.context = context;
        this.user_uuid = uuid;
    }

	@Override
	public void onGpsStatusChanged(int event) {
		GpsStatus gpsStatus = lM.getGpsStatus(null);
		if (gpsStatus != null) {
			Iterable<GpsSatellite> satellites = gpsStatus.getSatellites();
			Iterator<GpsSatellite> sat = satellites.iterator();
			int i = 0;
			while (sat.hasNext()) {
				GpsSatellite satellite = sat.next();
				strGpsStats += (i++) + ": " + satellite.getPrn() + ","
						+ satellite.usedInFix() + "," + satellite.getSnr()
						+ "," + satellite.getAzimuth() + ","
						+ satellite.getElevation() + "\n\n";
			}
		}
	}

	public Location getLastLocation() {
		Location location;
		location = lM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location;
	}

	@Override
	public void onLocationChanged(Location location) {
        if (location != null) {
            RecordGPSData(location.getLatitude(), location.getLongitude());
        }
    }

	@Override
	public void onProviderDisabled(String arg0) {
		System.out.println("GPS disabled");
        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        context.startActivity(intent);
    }

	@Override
	public void onProviderEnabled(String arg0) {
		System.out.println("GPS enabled");
	}

	@Override
	public void onStatusChanged(String provider, int status, Bundle boundle) {
		System.out.println("GPS status changed, arg: " + provider);
	}

	public void RecordGPSData(Double Latitude, Double Longitude) {
		final Realm realmDB = Realm.getDefaultInstance();
		final Double latitude=Latitude;
		final Double longitude=Longitude;

		realmDB.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				GpsTrack gpstrack = realmDB.createObject(GpsTrack.class);
                long next_id = GpsTrack.getLastId() + 1;
                gpstrack.set_id(next_id);
				gpstrack.setDate(new Date());
				gpstrack.setUserUuid(user_uuid);
				gpstrack.setLatitude(latitude);
				gpstrack.setLongitude(longitude);
			}
		});

        realmDB.close();
    }
}
