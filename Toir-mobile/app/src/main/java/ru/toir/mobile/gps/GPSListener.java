package ru.toir.mobile.gps;

import android.content.Context;
import android.location.GpsSatellite;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;

import java.util.Date;
import java.util.Iterator;

import io.realm.Realm;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.GPSDBAdapter;
import ru.toir.mobile.db.realm.GpsTrack;

public class GPSListener implements LocationListener, GpsStatus.Listener {

	String ls;
	String strGpsStats;
	private Context context;
	private String user_uuid;
	private LocationManager lM;

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

	public GPSListener(Context context, String uuid) {
		this.context = context;
		this.user_uuid = uuid;
	}

	public Location getLastLocation() {
		Location location;
		location = lM.getLastKnownLocation(LocationManager.GPS_PROVIDER);
		return location;
	}

	@Override
	public void onLocationChanged(Location location) {
		if (location != null)
			RecordGPSData(location.getLatitude(), location.getLongitude());
	}

	@Override
	public void onProviderDisabled(String arg0) {
		System.out.println("GPS disabled");
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
		GPSDBAdapter gps = new GPSDBAdapter(new ToirDatabaseContext(context));
		final Realm realmDB = Realm.getDefaultInstance();
		final Double latitude=Latitude;
		final Double longitude=Longitude;
		realmDB.executeTransaction(new Realm.Transaction() {
			@Override
			public void execute(Realm realm) {
				GpsTrack gpstrack = realmDB.createObject(GpsTrack.class);
				gpstrack.setDate(new Date());
				gpstrack.setUserUuid(user_uuid);
				gpstrack.setLatitude(latitude);
				gpstrack.setLongitude(longitude);
			}
		});
	}
}
