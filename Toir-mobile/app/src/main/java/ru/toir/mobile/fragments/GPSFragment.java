package ru.toir.mobile.fragments;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import io.realm.RealmResults;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.db.adapters.EquipmentAdapter;
import ru.toir.mobile.db.realm.Equipment;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.Tasks;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.gps.TaskItemizedOverlay;
import ru.toir.mobile.gps.TestGPSListener;

//import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
//import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
//import ru.toir.mobile.db.adapters.EquipmentTypeAdapter;
//import ru.toir.mobile.db.adapters.TaskDBAdapter;
//import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;

public class GPSFragment extends Fragment {

	private IMapController mapController;
	private MapView mapView;
	private double curLatitude, curLongitude;
	private ListView lv_equipment;
	Location location;
	TextView gpsLog;
    private Realm realmDB;
    private User user;
    private Tasks task;
    private RealmResults<Tasks> tasks;
    private Orders order;
    private RealmResults<Orders> orders;
    private RealmResults<Equipment> equipment;

	ArrayList<OverlayItem> aOverlayItemArray;
	private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
	// private SimpleCursorAdapter equipmentAdapter;
	private int LastItemPosition = -1;

    public GPSFragment() {
    }

    public static GPSFragment newInstance() {
        return (new GPSFragment());
    }

    /**
	 * Класс объекта оборудования для отображения на крате
	 * 
	 * @author koputo
	 * 
	 */
	class EquipmentOverlayItem extends OverlayItem {
		public Equipment equipment;

		public EquipmentOverlayItem(String a, String b, GeoPoint p) {
			super(a, b, p);
		}
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
        realmDB = Realm.getDefaultInstance();
        EquipmentAdapter equipmentAdapter;
        Equipment equipment;
        RealmResults<Equipment> equipments2 = realmDB.where(Equipment.class).equalTo("uuid", "").findAll();
        RealmResults<Equipment> equipments = realmDB.where(Equipment.class).equalTo("uuid", "").findAll();
        ListView equipmentListView;

		String tagId = AuthorizedUser.getInstance().getTagId();
		// String equipmentUUID = "";
		Float equipment_latitude = 0f, equipment_longitude = 0f;
		//UsersDBAdapter users = new UsersDBAdapter(new ToirDatabaseContext(
		//		getActivity().getApplicationContext()));
		// запрашиваем данные текущего юзера, хотя нам нужен только его uuid
		// (если он будет храниться глобально, то запрашивать постоянно уже не
		// надо будет)
		//Users user = users.getUserByTagId(tagId);
        user = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findFirst();
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
				gpsLog.append("Longtitude:"
						+ String.valueOf(location.getLongitude()) + "\n");
			}
		}

		mapView = (MapView) rootView.findViewById(R.id.gps_mapview);
		// mapView.setTileSource(TileSourceFactory.MAPNIK);
		mapView.setUseDataConnection(false);
		mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
		mapView.setBuiltInZoomControls(true);
		mapController = mapView.getController();
		mapController.setZoom(17);
		GeoPoint point2 = new GeoPoint(curLatitude, curLongitude);
		mapController.setCenter(point2);

		// добавляем тестовый маркер
		// aOverlayItemArray = new ArrayList<OverlayItem>();
		OverlayItem overlayItem = new OverlayItem("We are here", "WAH",
				new GeoPoint(curLatitude, curLongitude));
		aOverlayItemArray = new ArrayList<OverlayItem>();
		aOverlayItemArray.add(overlayItem);
		ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(
				getActivity().getApplicationContext(), aOverlayItemArray, null);
		mapView.getOverlays().add(aItemizedIconOverlay);

		//!!!!
        equipmentListView = (ListView) rootView
                .findViewById(R.id.gps_listView);

        orders = realmDB.where(Orders.class).equalTo("userUuid", AuthorizedUser.getInstance().getUuid()).equalTo("orderStatusUuid",OrderStatus.Status.IN_WORK).findAll();
        for (Orders itemOrder : orders) {
            tasks = realmDB.where(Tasks.class).equalTo("orderUuid", itemOrder.getUuid()).findAll();
            for (Tasks itemTask : tasks) {
                equipments = realmDB.where(Equipment.class).equalTo("uuid", itemTask.getEquipmentUuid()).findAll();
                equipment = realmDB.where(Equipment.class).equalTo("uuid", itemTask.getEquipmentUuid()).findFirst();
                equipments2.add(equipment);
                curLatitude = curLatitude - 0.0001;
                curLongitude = curLongitude - 0.0001;

                EquipmentOverlayItem olItem = new EquipmentOverlayItem(
                        equipment.getTitle(), "Device",
                        new GeoPoint(equipment_latitude,
                                equipment_longitude));
                olItem.equipment = equipment;
                Drawable newMarker = this.getResources()
                        .getDrawable(R.drawable.marker_equip);
                olItem.setMarker(newMarker);
                overlayItemArray.add(olItem);
            }
        }
        if (equipments!=null) {
            equipmentAdapter = new EquipmentAdapter(getContext(), R.id.gps_listView, equipments);
            equipmentListView.setAdapter(equipmentAdapter);
        }

        /*
		TaskDBAdapter taskDBAdapter = new TaskDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
        ArrayList<Task> taskList = taskDBAdapter.getOrdersByUser(
				user.getUuid(), TaskStatusDBAdapter.Status.IN_WORK, "");
		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		String[] equipmentFrom = { "name", "location" };
		int[] equipmentTo = { R.id.lv_firstLine, R.id.lv_secondLine };

		lv_equipment = (ListView) rootView.findViewById(R.id.gps_listView);

		EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new ToirDatabaseContext(getActivity().getApplicationContext()));

		for (Task task : taskList) {
			ArrayList<EquipmentOperation> operationList = operationDBAdapter
					.getItems(task.getUuid());
			// запрашиваем перечень оборудования
			if (operationList != null) {
				for (EquipmentOperation operation : operationList) {

					if (operation.getEquipment_uuid() != null) {
						HashMap<String, String> hm = new HashMap<String, String>();
						Equipment equipment = equipmentDBAdapter
								.getItem(operation.getEquipment_uuid());
						if (equipment != null) {

							hm.put("name", equipment.getTitle()
							// + " ["
							// +
							// eqDBAdapter.getInventoryNumberByUUID(equipmentUUID)
							// + "]"
							);
							// default
							hm.put("location", equipment.getLocation());
							aList.add(hm);
							// String coordinates[] = location.split("[NSWE]");
							// equipment_latitude=equipmentDBAdapter.getLatitudeByUUID(equipmentUUID);
							// equipment_longitude=equipmentDBAdapter.getLongitudeByUUID(equipmentUUID);
							curLatitude = curLatitude - 0.0001;
							curLongitude = curLongitude - 0.0001;

							// EquipmentOverlayItem olItem = new
							// EquipmentOverlayItem(
							// equipment.getTitle(), "Device", new GeoPoint(
							// curLatitude, curLongitude));
							EquipmentOverlayItem olItem = new EquipmentOverlayItem(
									equipment.getTitle(), "Device",
									new GeoPoint(equipment_latitude,
											equipment_longitude));
							olItem.equipment = equipment;
							Drawable newMarker = this.getResources()
									.getDrawable(R.drawable.marker_equip);
							olItem.setMarker(newMarker);
							overlayItemArray.add(olItem);
							// aOverlayItemArray.add(olItem);
						}
					}
				}
			}
		}

		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview,
				equipmentFrom, equipmentTo);
		// Setting the adapter to the listView
		lv_equipment.setAdapter(adapter);
		// lv_equipment.setOnItemClickListener(clickListener);
		lv_equipment.setOnItemClickListener(new OnItemClickListener() {
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
        */
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

				// пример тупой, но полагю это почти то что тебе было нужно
				ViewPager pager = (ViewPager) getActivity().findViewById(
						R.id.pager);
				//pager.setCurrentItem(PageAdapter.TASK_FRAGMENT);

				return super.onLongPressHelper(index, item);
			}
		};
		mapView.getOverlays().add(overlay);

		// String[] equipmentFrom = { "", "equipment_uuid" };
		// int[] equipmentTo = { R.id.lv_firstLine, R.id.lv_secondLine};
		// EquipmentOperationDBAdapter equipmentOperationDBAdapter = new
		// EquipmentOperationDBAdapter(
		// new TOiRDatabaseContext(getActivity()
		// .getApplicationContext()));
		// EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
		// new TOiRDatabaseContext(getActivity()
		// .getApplicationContext()));
		//
		// lv_equipment = (ListView) rootView.findViewById(R.id.gps_listView);
		// equipmentAdapter = new SimpleCursorAdapter(getActivity(),
		// R.layout.listview, null, equipmentFrom, equipmentTo,
		// CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// // Setting the adapter to the listView
		// lv_equipment.setAdapter(equipmentAdapter);
		// lv_equipment.setOnItemClickListener(clickListener);
		// equipmentAdapter.changeCursor(equipmentOperationDBAdapter.getOperationWithInfo());

		onInit(rootView);

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	public void onInit(View view) {

	}
}
