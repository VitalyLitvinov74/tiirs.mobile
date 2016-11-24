package ru.toir.mobile.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import org.osmdroid.api.IMapController;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;

import io.realm.Realm;
import io.realm.RealmQuery;
import io.realm.RealmResults;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.gps.TaskItemizedOverlay;
import ru.toir.mobile.gps.TestGPSListener;

public class GPSFragment extends Fragment {

	private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<>();
	Location location;
	TextView gpsLog;
	ArrayList<OverlayItem> aOverlayItemArray;
    private double curLatitude, curLongitude;
	//private ListView lv_equipment;
    //private Tasks task;
    //private Orders order;
    //private RealmResults<Equipment> equipment;
	private int LastItemPosition = -1;

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
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gps_layout, container, false);

        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        EquipmentAdapter equipmentAdapter;
        ListView equipmentListView;

        toolbar.setSubtitle("Карта");
        Realm realmDB = Realm.getDefaultInstance();
        RealmResults<Equipment> equipments; // = realmDB.where(Equipment.class).equalTo("uuid", "").findAll();

		//Float equipment_latitude = 0f, equipment_longitude = 0f;
        User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
		LocationManager lm = (LocationManager) getActivity().getSystemService(
				Context.LOCATION_SERVICE);

        if (lm != null) {
			TestGPSListener tgpsl = new TestGPSListener(
					(TextView) rootView.findViewById(R.id.gps_TextView),
					getActivity().getApplicationContext(), user.getUuid());
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1,
					tgpsl);
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);
			gpsLog = (TextView) rootView.findViewById(R.id.gps_TextView);
			if (location != null) {
				curLatitude = location.getLatitude();
				curLongitude = location.getLongitude();
				gpsLog.append("Altitude:"
						+ String.valueOf(location.getAltitude()) + "\n");
				gpsLog.append("Latitude:"
						+ String.valueOf(location.getLatitude()) + "\n");
				gpsLog.append("Longitude:"
						+ String.valueOf(location.getLongitude()) + "\n");
			}
		}

        MapView mapView = (MapView) rootView.findViewById(R.id.gps_mapview);
		// mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setUseDataConnection(false);
		mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
		mapView.setBuiltInZoomControls(true);
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
		ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<>(
				getActivity().getApplicationContext(), aOverlayItemArray, null);
		mapView.getOverlays().add(aItemizedIconOverlay);

		//!!!!
        equipmentListView = (ListView) rootView
                .findViewById(R.id.gps_listView);

        //orders = realmDB.where(Orders.class).equalTo("userUuid", AuthorizedUser.getInstance().getUuid()).equalTo("orderStatusUuid",OrderStatus.Status.IN_WORK).findAll();
        RealmQuery<Equipment> q = realmDB.where(Equipment.class);

        RealmResults<Orders> orders = realmDB.where(Orders.class).findAll();
        for (Orders itemOrder : orders) {
            RealmResults<Tasks> tasks = realmDB.where(Tasks.class).equalTo("orderUuid", itemOrder.getUuid()).findAll();
            //tasks = realmDB.where(Tasks.class).equalTo("orderUuid", realmDB.where(Orders.class).equalTo("userUuid", AuthorizedUser.getInstance().getUuid()).equalTo("orderStatusUuid",OrderStatus.Status.IN_WORK).findAll()).findAll();
            for (Tasks itemTask : tasks) {
                equipments = realmDB.where(Equipment.class).equalTo("uuid", itemTask.getEquipment().getUuid()).findAll();
                for (Equipment equipment : equipments) {
                    q = q.or().equalTo("_id", equipment.get_id());
                    curLatitude = curLatitude - 0.0002;
                    curLongitude = curLongitude - 0.0002;
                    EquipmentOverlayItem olItem = new EquipmentOverlayItem(
                            equipment.getTitle(), "Device",
                            new GeoPoint(curLatitude,
                                    curLongitude));
                    olItem.equipment = equipment;
                    //TODO реальные уровни критичности в качестве маркеров
                    Drawable newMarker;
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
                        newMarker = this.getResources().getDrawable(R.drawable.critical_level_3, getActivity().getApplicationContext().getTheme());
                    } else {
                        newMarker = this.getResources().getDrawable(R.drawable.critical_level_3);
                    }
                    olItem.setMarker(newMarker);
                    overlayItemArray.add(olItem);
                }
            }
        }
        equipments = q.findAll();

        if (equipments!=null) {
			equipmentAdapter = new EquipmentAdapter(getContext(), equipments);
			equipmentListView.setAdapter(equipmentAdapter);
		}

        equipmentListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO связать нажатие в списке с картой: изменить цвет маркера
				OverlayItem item = overlayItemArray.get(position);
				// Get the new Drawable
				Drawable marker = view.getResources().getDrawable(
						R.drawable.marker_equip_selected);
				// Set the new marker
				item.setMarker(marker);
				if (LastItemPosition >= 0) {
					OverlayItem item2 = overlayItemArray.get(LastItemPosition);
					marker = view.getResources().getDrawable(
							R.drawable.marker_equip);
					item2.setMarker(marker);
				}
				LastItemPosition = position;
			}
		});

        TaskItemizedOverlay overlay = new TaskItemizedOverlay(getActivity()
				.getApplicationContext(), overlayItemArray) {
			@Override
			protected boolean onLongPressHelper(int index, OverlayItem item) {
				Equipment equipment = ((EquipmentOverlayItem) item).equipment;
				Toast.makeText(
						mContext,
						"UUID оборудования " + equipment.getTitle() + " - "
								+ equipment.getUuid(), Toast.LENGTH_SHORT)
						.show();

                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, EquipmentsFragment.newInstance()).commit();
				return super.onLongPressHelper(index, item);
			}
		};
		mapView.getOverlays().add(overlay);
		//onInit(rootView);

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}


	/**
	 * Класс объекта оборудования для отображения на крате
	 *
	 * @author koputo
	 */
	class EquipmentOverlayItem extends OverlayItem {
		public Equipment equipment;

		public EquipmentOverlayItem(String a, String b, GeoPoint p) {
			super(a, b, p);
		}
	}
}
