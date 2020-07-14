package ru.toir.mobile.multi.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;
import java.util.List;

import io.realm.Realm;
import io.realm.RealmResults;
import io.realm.Sort;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.adapters.ObjectAdapter;
import ru.toir.mobile.multi.db.realm.GpsTrack;
import ru.toir.mobile.multi.db.realm.Objects;
import ru.toir.mobile.multi.gps.TaskItemizedOverlay;

import static android.content.Context.LOCATION_SERVICE;

public class ObjectFragment extends Fragment {

    private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
    Location location;
    ArrayList<OverlayItem> aOverlayItemArray;
    Realm realmDB;
    private double curLatitude, curLongitude;
    private int LastItemPosition = -1;

    public ObjectFragment() {
    }

    public static ObjectFragment newInstance() {
        return (new ObjectFragment());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.gps_layout, container, false);

        final FragmentActivity activity = getActivity();
        if (activity == null) {
            return null;
        }

        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        final ObjectAdapter objectAdapter;
        final ListView objectsListView;

        toolbar.setSubtitle(getString(R.string.menu_objects));
        realmDB = Realm.getDefaultInstance();

        //User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        LocationManager lm = (LocationManager) activity.getSystemService(LOCATION_SERVICE);
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (lm != null && permission == PackageManager.PERMISSION_GRANTED) {
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            if (location == null) {
                location = getLastKnownLocation();
            }

            if (location != null) {
                curLatitude = location.getLatitude();
                curLongitude = location.getLongitude();
            }
        }

        final MapView mapView = rootView.findViewById(R.id.gps_mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        GeoPoint point2 = new GeoPoint(curLatitude, curLongitude);
        mapController.setCenter(point2);

        OverlayItem overlayItem = new OverlayItem("Вы сейчас здесь", "WAH",
                new GeoPoint(curLatitude, curLongitude));
        aOverlayItemArray = new ArrayList<>();
        aOverlayItemArray.add(overlayItem);
        ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<>(
                activity.getApplicationContext(), aOverlayItemArray, null);
        mapView.getOverlays().add(aItemizedIconOverlay);

        objectsListView = rootView.findViewById(R.id.gps_listView);

        final ArrayList<GeoPoint> waypoints = new ArrayList<>();
        GeoPoint currentPoint = new GeoPoint(curLatitude, curLongitude);
        waypoints.add(currentPoint);

        RealmResults<Objects> objects = realmDB.where(Objects.class).findAll();
        for (Objects object : objects) {
            curLatitude = object.getLatitude();
            curLongitude = object.getLongitude();

            GeoPoint endPoint = new GeoPoint(curLatitude, curLongitude);
            waypoints.add(endPoint);

            ObjectsOverlayItem olItem = new ObjectsOverlayItem(
                    object.getTitle(), "Объект",
                    new GeoPoint(curLatitude,
                            curLongitude));
            olItem.object = object;
            Drawable newMarker = ContextCompat.getDrawable(activity, R.drawable.equipment_32);
            olItem.setMarker(newMarker);
            overlayItemArray.add(olItem);
        }

        objectAdapter = new ObjectAdapter(objects);
        objectsListView.setAdapter(objectAdapter);
        objectsListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //equipmentListView.getItemAtPosition();
                return false;
            }
        });

        objectsListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {
                // TODO связать нажатие в списке с картой: изменить цвет маркера
                OverlayItem item = overlayItemArray.get(position);
                // Get the new Drawable
                Drawable marker = view.getResources().getDrawable(R.drawable.marker_equip);
                item.setMarker(marker);
                if (LastItemPosition >= 0) {
                    OverlayItem item2 = overlayItemArray.get(LastItemPosition);
                    marker = view.getResources().getDrawable(R.drawable.marker_equip);
                    item2.setMarker(marker);
                }
                LastItemPosition = position;
            }
        });

        TaskItemizedOverlay overlay = new TaskItemizedOverlay(activity.getApplicationContext(),
                overlayItemArray) {
            @Override
            protected boolean onLongPressHelper(int index, OverlayItem item) {
                Objects object = ((ObjectsOverlayItem) item).object;
                Toast.makeText(
                        mContext,
                        "Объект " + object.getTitle() + " - "
                                + object.getUuid(), Toast.LENGTH_SHORT)
                        .show();

                String object_uuid = object.getUuid();
                Intent objectInfo = new Intent(getActivity(),
                        EquipmentInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("object_uuid", object_uuid);
                objectInfo.putExtras(bundle);
                getActivity().startActivity(objectInfo);

                return super.onLongPressHelper(index, item);
            }
        };
        mapView.getOverlays().add(overlay);

        // Добавляем несколько слоев
        CompassOverlay compassOverlay = new CompassOverlay(activity.getApplicationContext(), mapView);
        compassOverlay.enableCompass();
        mapView.getOverlays().add(compassOverlay);
        /*
        MyLocationNewOverlay mLocationOverlay = new MyLocationNewOverlay(getActivity()
                .getApplicationContext(), mapView);
        mLocationOverlay.enableMyLocation();
        mapView.getOverlays().add(mLocationOverlay); */
        ScaleBarOverlay mScaleBarOverlay = new ScaleBarOverlay(mapView);
        mScaleBarOverlay.setCentred(true);
        //play around with these values to get the location on screen in the right place for your applicatio
        mScaleBarOverlay.setScaleBarOffset(200, 10);
        mapView.getOverlays().add(mScaleBarOverlay);

        new Thread(new Runnable() {
            public void run() {
                Road road;
                RoadManager roadManager = new OSRMRoadManager(getActivity()
                        .getApplicationContext());
                try {
                    road = roadManager.getRoad(waypoints);
                    roadManager.addRequestOption("routeType=pedestrian");
                    Polyline roadOverlay = RoadManager.buildRoadOverlay(road, Color.RED, 8);
                    mapView.getOverlays().add(roadOverlay);
                    mapView.invalidate();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }).start();

        // добавляем путь
        ArrayList<GeoPoint> trackpoints = new ArrayList<>();
        RealmResults<GpsTrack> gpsTrack;
        Polyline roadOverlay = new Polyline();
        roadOverlay.setColor(Color.DKGRAY);
        roadOverlay.setWidth(10.0f);
        gpsTrack = realmDB.where(GpsTrack.class).findAll().sort("date", Sort.DESCENDING);
        for (GpsTrack trackPoint : gpsTrack) {
            GeoPoint startPoint = new GeoPoint(trackPoint.getLatitude(), trackPoint.getLongitude());
            trackpoints.add(startPoint);
        }

        roadOverlay.setPoints(trackpoints);
        mapView.getOverlays().add(roadOverlay);
        mapView.invalidate();

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private Location getLastKnownLocation() {
        FragmentActivity activity = getActivity();
        Location bestLocation = null;

        if (activity != null) {
            LocationManager mLocationManager = (LocationManager) activity.getApplicationContext()
                    .getSystemService(LOCATION_SERVICE);
            int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
            if (mLocationManager != null && permission == PackageManager.PERMISSION_GRANTED) {
                List<String> providers = mLocationManager.getProviders(true);
                for (String provider : providers) {
                    Location l = mLocationManager.getLastKnownLocation(provider);
                    if (l == null) {
                        continue;
                    }
                    if (bestLocation == null || l.getAccuracy() < bestLocation.getAccuracy()) {
                        bestLocation = l;
                    }
                }
            }
        }

        return bestLocation;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }

    /**
     * Класс объекта объектов для отображения на крате
     *
     * @author olejek
     */
    private class ObjectsOverlayItem extends OverlayItem {
        public Objects object;

        ObjectsOverlayItem(String a, String b, GeoPoint p) {
            super(a, b, p);
        }
    }
}
