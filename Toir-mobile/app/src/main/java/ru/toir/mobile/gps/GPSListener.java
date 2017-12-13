package ru.toir.mobile.gps;

import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;

import java.util.Date;

import io.realm.Realm;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.db.realm.GpsTrack;

public class GPSListener implements LocationListener, GpsStatus.Listener {

    private String userUuid = null;

    @Override
    public void onGpsStatusChanged(int event) {
    }

    @Override
    public void onLocationChanged(Location location) {
        if (location != null) {
            RecordGPSData(location.getLatitude(), location.getLongitude());
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

                GpsTrack gpstrack = realmDB.createObject(GpsTrack.class);
                long next_id = GpsTrack.getLastId() + 1;
                gpstrack.set_id(next_id);
                gpstrack.setDate(new Date());
                gpstrack.setUserUuid(uuid);
                gpstrack.setLatitude(latitude);
                gpstrack.setLongitude(longitude);
            }
        });

        realmDB.close();
    }
}
