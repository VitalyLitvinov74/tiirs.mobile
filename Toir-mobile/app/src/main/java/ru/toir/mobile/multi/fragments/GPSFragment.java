package ru.toir.mobile.multi.fragments;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.res.Resources;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
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
import android.widget.Spinner;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.routing.OSRMRoadManager;
import org.osmdroid.bonuspack.routing.Road;
import org.osmdroid.bonuspack.routing.RoadManager;
import org.osmdroid.tileprovider.constants.OpenStreetMapTileProviderConstants;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.views.overlay.Polyline;
import org.osmdroid.views.overlay.ScaleBarOverlay;
import org.osmdroid.views.overlay.compass.CompassOverlay;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import io.realm.Realm;
import io.realm.RealmList;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.toir.mobile.multi.BuildConfig;
import ru.toir.mobile.multi.EquipmentInfoActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.adapters.EquipmentAdapter;
import ru.toir.mobile.multi.db.adapters.EquipmentTypeAdapter;
import ru.toir.mobile.multi.db.realm.Equipment;
import ru.toir.mobile.multi.db.realm.EquipmentType;
import ru.toir.mobile.multi.db.realm.Orders;
import ru.toir.mobile.multi.db.realm.Stage;
import ru.toir.mobile.multi.db.realm.Task;
import ru.toir.mobile.multi.gps.TaskItemizedOverlay;

import static android.content.Context.LOCATION_SERVICE;

public class GPSFragment extends Fragment {

    private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
    Location location;
    ArrayList<OverlayItem> aOverlayItemArray;
    Realm realmDB;
    private double curLatitude, curLongitude;
    private Spinner typeSpinner;
    private ListView equipmentListView;
    private EquipmentAdapter equipmentAdapter;

    public GPSFragment() {
    }

    public static GPSFragment newInstance() {
        return (new GPSFragment());
    }

    /*
     * (non-Javadoc)
     *
     * @see
     * android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater,
     * android.view.ViewGroup, android.os.Bundle)
     */
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        FragmentActivity activity = getActivity();
        if (activity == null) {
            return null;
        }

        View rootView = inflater.inflate(R.layout.gps_layout, container, false);
        Toolbar toolbar = activity.findViewById(R.id.toolbar);
        equipmentListView = rootView.findViewById(R.id.gps_listView);

        toolbar.setSubtitle(getString(R.string.menu_map));
        realmDB = Realm.getDefaultInstance();

        SpinnerListener spinnerListener = new SpinnerListener();
        RealmResults<Equipment> equipments = realmDB.where(Equipment.class).findAll();
        Set<String> equipmentTypeUuids = new HashSet<>();
        for (Equipment item : equipments) {
            equipmentTypeUuids.add(item.getEquipmentModel().getEquipmentType().getUuid());
        }
        RealmResults<EquipmentType> equipmentTypes = realmDB.where(EquipmentType.class)
                .in("uuid", equipmentTypeUuids.toArray(new String[]{}))
                .findAll();
        typeSpinner = rootView.findViewById(R.id.simple_spinner);
        EquipmentTypeAdapter typeSpinnerAdapter = new EquipmentTypeAdapter(equipmentTypes);
        typeSpinnerAdapter.notifyDataSetChanged();
        typeSpinner.setAdapter(typeSpinnerAdapter);
        typeSpinner.setOnItemSelectedListener(spinnerListener);

        // создаём "пустой" адаптер для отображения списка оборудования
        /*
		equipmentAdapter = new SimpleCursorAdapter(getContext(),
				R.layout.equipment_reference_item_layout, null, equipmentFrom,
				equipmentTo, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		equipmentListView.setAdapter(equipmentAdapter);
        */
        // это нужно для отображения произвольных изображений и конвертации в
        // строку дат
        /*
		equipmentAdapter.setViewBinder(new SimpleCursorAdapter.ViewBinder() {

			@Override
			public boolean setViewValue(View view, Cursor cursor,
					int columnIndex) {

				int viewId = view.getId();

				if (viewId == R.id.eril_last_operation_date) {
					long lDate = cursor.getLong(columnIndex);
					String sDate = DataUtils.getDate(lDate, "dd.MM.yyyy HH:ss");
					((TextView) view).setText(sDate);
					return true;
				}

				return false;
			}
		});
        */

        LocationManager lm = (LocationManager) getActivity().getSystemService(LOCATION_SERVICE);
        int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        if (lm != null && permission == PackageManager.PERMISSION_GRANTED) {
            //GPSListener tgpsl = new GPSListener(getActivity().getApplicationContext(), user.getUuid());
            //lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, tgpsl);
            location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            //gpsLog = (TextView) rootView.findViewById(R.id.gps_TextView);
            // последняя попытка
            if (location == null) {
                location = getLastKnownLocation();
            }

            if (location != null) {
                curLatitude = location.getLatitude();
                curLongitude = location.getLongitude();
                /*
                gpsLog.append("Altitude:" + String.valueOf(location.getAltitude()) + "\n");
				gpsLog.append("Latitude:" + String.valueOf(location.getLatitude()) + "\n");
				gpsLog.append("Longitude:" + String.valueOf(location.getLongitude()) + "\n");*/
            }
        }

        final MapView mapView = rootView.findViewById(R.id.gps_mapview);
        OpenStreetMapTileProviderConstants.setUserAgentValue(BuildConfig.APPLICATION_ID);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        //MAPQUESTOSM
        IMapController mapController = mapView.getController();
        mapController.setZoom(17);
        GeoPoint point2 = new GeoPoint(curLatitude, curLongitude);
        mapController.setCenter(point2);

        // добавляем тестовый маркер
        // aOverlayItemArray = new ArrayList<OverlayItem>();
        OverlayItem overlayItem = new OverlayItem("We are here", "WAH",
                new GeoPoint(curLatitude, curLongitude));
        aOverlayItemArray = new ArrayList<>();
        aOverlayItemArray.add(overlayItem);
        final ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<>(
                getActivity().getApplicationContext(), aOverlayItemArray, null);
        mapView.getOverlays().add(aItemizedIconOverlay);

        //orders = realmDB.where(Orders.class).equalTo("user.uuid", AuthorizedUser.getInstance().getUuid()).equalTo("orderStatusUuid",OrderStatus.Status.IN_WORK).findAll();
        RealmQuery<Equipment> q = realmDB.where(Equipment.class);

        final ArrayList<GeoPoint> waypoints = new ArrayList<>();
        GeoPoint currentPoint = new GeoPoint(curLatitude, curLongitude);
        waypoints.add(currentPoint);

        RealmResults<Orders> orders = realmDB.where(Orders.class).findAll();
        final List<Long> eqIdList = new ArrayList<>();
        for (Orders order : orders) {
            RealmList<Task> tasks = order.getTasks();
            for (Task task : tasks) {
                for (Stage stage : task.getStages()) {
                    Equipment equipment = stage.getEquipment();

                    eqIdList.add(equipment.get_id());
                    curLatitude = equipment.getLatitude();
                    curLongitude = equipment.getLongitude();

                    GeoPoint endPoint = new GeoPoint(curLatitude, curLongitude);
                    waypoints.add(endPoint);

                    EquipmentOverlayItem olItem = new EquipmentOverlayItem(equipment.getTitle(),
                            "Device", new GeoPoint(curLatitude, curLongitude));
                    olItem.equipment = equipment;
                    //TODO реальные уровни критичности в качестве маркеров
                    Drawable newMarker;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        Resources.Theme theme = getActivity().getApplicationContext().getTheme();
                        newMarker = this.getResources().getDrawable(R.drawable.equipment_32, theme);
                    } else {
                        newMarker = this.getResources().getDrawable(R.drawable.equipment_32);
                    }

                    olItem.setMarker(newMarker);
                    overlayItemArray.add(olItem);
                }
            }
        }

        if (!eqIdList.isEmpty()) {
            equipments = q.in("_id", eqIdList.toArray(new Long[]{})).findAll();
            equipmentAdapter = new EquipmentAdapter(equipments);
            equipmentListView.setAdapter(equipmentAdapter);
            equipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {
                    Equipment equipment = equipmentAdapter.getItem(position);
                    if (equipment != null) {
                        GeoPoint point = new GeoPoint(equipment.getLatitude(), equipment.getLongitude());
                        OverlayItem equipmentItem = new OverlayItem("We are here", "WAH",
                                point);
                        aItemizedIconOverlay.removeAllItems();
                        aItemizedIconOverlay.addItem(equipmentItem);
                        mapView.getController().animateTo(point);
                    }
                    // TODO связать нажатие в списке с картой: изменить цвет маркера
/*
                OverlayItem item = overlayItemArray.get(position);
                // Get the new Drawable
                Drawable marker = view.getResources().getDrawable(R.drawable.equipment_32);
                // Set the new marker
                item.setMarker(marker);
                if (LastItemPosition >= 0) {
                    OverlayItem item2 = overlayItemArray.get(LastItemPosition);
                    marker = view.getResources().getDrawable(R.drawable.marker_equip);
                    item2.setMarker(marker);
                }
*/

                    //LastItemPosition = position;
                }
            });
        }
        equipmentListView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                //equipmentListView.getItemAtPosition();
                return false;
            }
        });

        TaskItemizedOverlay overlay = new TaskItemizedOverlay(getActivity().getApplicationContext(),
                overlayItemArray) {
            @Override
            protected boolean onLongPressHelper(int index, OverlayItem item) {
                Equipment equipment = ((EquipmentOverlayItem) item).equipment;
                Toast.makeText(
                        mContext,
                        "UUID оборудования " + equipment.getTitle() + " - "
                                + equipment.getUuid(), Toast.LENGTH_SHORT)
                        .show();


                String equipment_uuid = equipment.getUuid();
                Intent equipmentInfo = new Intent(getActivity(), EquipmentInfoActivity.class);
                Bundle bundle = new Bundle();
                bundle.putString("equipment_uuid", equipment_uuid);
                equipmentInfo.putExtras(bundle);
                getActivity().startActivity(equipmentInfo);

                return super.onLongPressHelper(index, item);
            }
        };
        mapView.getOverlays().add(overlay);

        // Добавляем несколько слоев
        CompassOverlay compassOverlay = new CompassOverlay(getActivity()
                .getApplicationContext(), mapView);
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
                RoadManager roadManager = new OSRMRoadManager(getActivity().getApplicationContext());
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

        rootView.setFocusableInTouchMode(true);
        rootView.requestFocus();

        return rootView;
    }

    private Location getLastKnownLocation() {
        FragmentActivity activity = getActivity();
        Location bestLocation = null;

        if (activity != null) {
            LocationManager mLocationManager;
            mLocationManager = (LocationManager) activity.getApplicationContext()
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

    private void FillListViewEquipments(String equipmentTypeUuid) {
        RealmResults<Equipment> equipments;
        if (equipmentTypeUuid != null) {
            equipments = realmDB.where(Equipment.class)
                    .equalTo("equipmentModel.equipmentType.uuid", equipmentTypeUuid)
                    .findAll();
        } else {
            equipments = realmDB.where(Equipment.class).findAll();
        }

        equipmentAdapter = new EquipmentAdapter(equipments);
        equipmentListView.setAdapter(equipmentAdapter);
    }

    /**
     * Класс объекта оборудования для отображения на крате
     *
     * @author koputo
     */
    private class EquipmentOverlayItem extends OverlayItem {
        public Equipment equipment;

        EquipmentOverlayItem(String a, String b, GeoPoint p) {
            super(a, b, p);
        }
    }

    private class SpinnerListener implements AdapterView.OnItemSelectedListener {
        //boolean userSelect = false;

        @Override
        public void onNothingSelected(AdapterView<?> parentView) {
        }

        @Override
        public void onItemSelected(AdapterView<?> parentView,
                                   View selectedItemView, int position, long id) {

            String type = null;

            EquipmentType typeSelected = (EquipmentType) typeSpinner.getSelectedItem();
            if (typeSelected != null) {
                type = typeSelected.getUuid();
                //if (typeSelected.get_id() == 1) type = null;
            }

            FillListViewEquipments(type);
        }
    }
}
