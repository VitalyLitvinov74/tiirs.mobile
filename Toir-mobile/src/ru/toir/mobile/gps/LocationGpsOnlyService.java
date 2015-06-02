package ru.toir.mobile.gps;

import java.util.Timer;
import java.util.TimerTask;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRApplication;
import ru.toir.mobile.utils.ToastUtil;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.widget.Toast;

/**
* GPS Location Listener.
*/
public class LocationGpsOnlyService implements LocationListener {

private static final int BEST_PROVIDER_SHCEDULE = 5000;
private LocationManager locationManager;
private Location gpsLocation;
private Location firstLocation;
private Context context;
private TimerTask locationCheckingTask;
private Timer t = new Timer();
long updatedGpsTime = System.currentTimeMillis();

public LocationGpsOnlyService(Context _context)
	{ 
	 context = _context;
	 locationManager = (LocationManager) context.getSystemService(Context.LOCATION_SERVICE);
	 //Toast.makeText(context, "update!", Toast.LENGTH_SHORT).show();
	 enableLocationListening(); 
	}

public void cancelChecking() {
if (locationCheckingTask != null)
	{ locationCheckingTask.cancel(); }
}

public void disableLocationListening()
{ 
	locationManager.removeUpdates(this);
	cancelChecking(); 
}

public void enableChecking() {
locationCheckingTask = new TimerTask() {
@Override
public void run() {
if (gpsLocation != null)
{ gpsLocation = null;
notifyAboutUpdatedLocation();
}
}
};
t.schedule(locationCheckingTask, BEST_PROVIDER_SHCEDULE, BEST_PROVIDER_SHCEDULE);
}

public void enableLocationListening()
{ 
 initBestProvider();
 enableChecking(); 
}

public Location getInaccurateLocation()
{ return firstLocation; }

public Location getLatestLocation()
{ 
	return gpsLocation; 
}

@Override
public void onLocationChanged(Location location) {
updatedGpsTime = System.currentTimeMillis();
gpsLocation = location;
if (firstLocation == null)
{ firstLocation = gpsLocation; }

notifyAboutUpdatedLocation();
}

@Override
public void onProviderDisabled(String provider) {
if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER))
{ initBestProvider(); }

}

@Override
public void onProviderEnabled(String provider) {
if (provider.equalsIgnoreCase(LocationManager.GPS_PROVIDER))
{ initBestProvider(); }

}

@Override
public void onStatusChanged(String provider, int status, Bundle extras)
{ // do nothing 
	Toast.makeText(context, "status!", Toast.LENGTH_SHORT).show();	
}

private void initBestProvider() {
locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 0, 0 /* m */, this);
if (firstLocation == null) {
gpsLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
if (gpsLocation != null)
{ notifyAboutUpdatedLocation(); }
firstLocation = gpsLocation;
}
}

private void notifyAboutUpdatedLocation()
{ 	
	//Intent intent = new Intent(MessageManager.PLAYER_LOCATION_CHANGED);
	//context.sendBroadcast(intent);
	Toast.makeText(context, "update!", Toast.LENGTH_SHORT).show();
}
}