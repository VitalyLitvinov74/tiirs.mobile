package ru.toir.mobile.multi.gps;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.nfc.Tag;
import android.os.Build;
import android.os.Bundle;
import android.util.Log;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.Date;

import io.realm.Realm;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.db.realm.GpsTrack;

import static java.lang.Math.abs;

public class GPSListener implements LocationListener, GpsStatus.Listener {


    private static final String TAG = "GPSListener";
    private String userUuid = null;
    private Location prevLocation = null;
    private LocalDateTime lastLocationUpdate = null;
    @Override
    public void onGpsStatusChanged(int event) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (prevLocation != null && isTimeUpdateExpired()) {
            if (abs(prevLocation.getLatitude() - location.getLatitude()) > 0.001 || abs(prevLocation.getLongitude() - location.getLongitude()) > 0.001)
                RecordGPSData(location.getLatitude(), location.getLongitude());
        }
        if (location != null) {
            prevLocation = location;
            //RecordGPSData(location.getLatitude(), location.getLongitude());
        }
        else
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                lastLocationUpdate = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
            }
    }

    public boolean isTimeUpdateExpired()
    {
        Log.d(TAG, "Check location update exired: "+lastLocationUpdate);
        if(lastLocationUpdate==null || Build.VERSION.SDK_INT <= Build.VERSION_CODES.O)
            return true;
        if(lastLocationUpdate.isBefore(LocalDateTime.now(ZoneId.of("Europe/Moscow")).minusMinutes(1)))
            return true;
        else {
            Log.d(TAG, "Location update not exired: "+lastLocationUpdate);
            return false;
        }
    }

    @Override
    public void onProviderDisabled(String arg0) {
    }

    @Override
    public void onProviderEnabled(String arg0) {
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle boundle) {
    }

    private void RecordGPSData(Double Latitude, Double Longitude) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            lastLocationUpdate = LocalDateTime.now(ZoneId.of("Europe/Moscow"));
        }

        final Realm realmDB = Realm.getDefaultInstance();
        final Double latitude = Latitude;
        final Double longitude = Longitude;

        realmDB.executeTransaction(new Realm.Transaction() {
            @Override
            public void execute(Realm realm) {
                AuthorizedUser user = AuthorizedUser.getInstance();
                String uuid = user.getUuid();
                if (uuid != null) {
                    userUuid = uuid;
                } else {
                    if (userUuid != null) {
                        uuid = userUuid;
                    } else {
                        // нет ни текущего, ни "предыдущего" пользователя,
                        // координаты "привязать" не к кому.
                        return;
                    }
                }
                // примерно от Европы и до северных морей
                if (latitude > 10.0 && longitude > 10.0) {
                    long next_id = GpsTrack.getLastId() + 1;
                    GpsTrack gpstrack = realmDB.createObject(GpsTrack.class, next_id);
                    gpstrack.setDate(new Date());
                    gpstrack.setUserUuid(uuid);
                    gpstrack.setLatitude(latitude);
                    gpstrack.setLongitude(longitude);
                }
            }
        });

        realmDB.close();
    }
}
