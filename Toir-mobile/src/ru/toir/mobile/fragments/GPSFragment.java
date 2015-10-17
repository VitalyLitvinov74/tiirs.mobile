package ru.toir.mobile.fragments;

import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;
import android.os.Bundle;

import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.api.IMapController;

import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.TaskStatusDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.tables.*;
import ru.toir.mobile.gps.TaskItemizedOverlay;
import ru.toir.mobile.gps.TestGPSListener;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.location.LocationManager;
import android.location.Location;

import org.osmdroid.views.overlay.ItemizedIconOverlay;

public class GPSFragment extends Fragment {
	private IMapController mapController;
	private MapView mapView;
	private double curLatitude, curLongitude;
	private ListView lv_equipment;	
	Location location;
	TextView gpsLog;
	ArrayList<OverlayItem> aOverlayItemArray;
	private final ArrayList<OverlayItem> overlayItemArray = new ArrayList<OverlayItem>();
	//private SimpleCursorAdapter equipmentAdapter;
	private	int LastItemPosition = -1;
	
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
		String tagId = AuthorizedUser.getInstance().getTagId();
		String equipmentUUID = "";
		Float	equipment_latitude=0f, equipment_longitude=0f;
		UsersDBAdapter users = new UsersDBAdapter(new ToirDatabaseContext(
				getActivity().getApplicationContext()));
		// запрашиваем данные текущего юзера, хотя нам нужен только его uuid
		// (если он будет храниться глобально, то запрашивать постоянно уже не
		// надо будет)
		Users user = users.getUserByTagId(tagId);
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
		//aOverlayItemArray = new ArrayList<OverlayItem>();
		OverlayItem overlayItem = new OverlayItem("We are here", "WAH",
				new GeoPoint(curLatitude, curLongitude));
		aOverlayItemArray = new ArrayList<OverlayItem>();
		aOverlayItemArray.add(overlayItem);
		ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(
				getActivity().getApplicationContext(), aOverlayItemArray, null);
		mapView.getOverlays().add(aItemizedIconOverlay);		

		TaskDBAdapter dbOrder = new TaskDBAdapter(new ToirDatabaseContext(
				getActivity().getApplicationContext()));
		ArrayList<Task> ordersList = dbOrder.getOrdersByUser(user.getUuid(),
				TaskStatusDBAdapter.STATUS_UUID_RECIEVED, "");

		List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
		String[] equipmentFrom = { "name", "location" };
		int[] equipmentTo = { R.id.lv_firstLine, R.id.lv_secondLine};
		lv_equipment = (ListView) rootView.findViewById(R.id.gps_listView);		

		Integer cnt = 0, cnt2 = 0;
		while (cnt < ordersList.size()) {
			EquipmentOperationDBAdapter operationDBAdapter = new EquipmentOperationDBAdapter(
					new ToirDatabaseContext(getActivity()
							.getApplicationContext()));
			ArrayList<EquipmentOperation> equipOperationList = operationDBAdapter
					.getItems(ordersList.get(cnt).getUuid());
			// запрашиваем перечень оборудования
			cnt2 = 0;
			if (equipOperationList!=null)
			while (cnt2 < equipOperationList.size()) {
				// equipOpList.get(cnt2).getUuid();
				EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
						new ToirDatabaseContext(getActivity()
								.getApplicationContext()));
				equipmentUUID = equipOperationList.get(cnt2).getEquipment_uuid();
				if (equipmentUUID != null) {
					//location = eqDBAdapter.getLocationCoordinatesByUUID(equipmentUUID);
					HashMap<String, String> hm = new HashMap<String, String>();
					hm.put("name", equipmentDBAdapter.getEquipsNameByUUID(equipmentUUID)
							//+ " ["
							//+ eqDBAdapter.getInventoryNumberByUUID(equipmentUUID)
							//+ "]"
							);
					// default
					hm.put("location", equipmentDBAdapter.getLocationByUUID(equipmentUUID));
					aList.add(hm);					
					// String coordinates[] = location.split("[NSWE]");
					//equipment_latitude=equipmentDBAdapter.getLatitudeByUUID(equipmentUUID);
					//equipment_longitude=equipmentDBAdapter.getLongitudeByUUID(equipmentUUID);
					curLatitude = curLatitude - 0.0001 * cnt2;
					curLongitude = curLongitude - 0.0001 * cnt2;

					Equipment equipment = equipmentDBAdapter
							.getItem(equipOperationList.get(cnt2)
									.getEquipment_uuid());
					//EquipmentOverlayItem olItem = new EquipmentOverlayItem(
					//		equipment.getTitle(), "Device", new GeoPoint(
					//				curLatitude, curLongitude));
					EquipmentOverlayItem olItem = new EquipmentOverlayItem(
							equipment.getTitle(), "Device", new GeoPoint(
									equipment_latitude, equipment_longitude));
					olItem.equipment = equipment;
					Drawable newMarker = this.getResources().getDrawable(
							R.drawable.marker_equip);
					olItem.setMarker(newMarker);
					overlayItemArray.add(olItem);
					// aOverlayItemArray.add(olItem);
				}
				cnt2 = cnt2 + 1;
			}
			cnt = cnt + 1;
		}
		SimpleAdapter adapter = new SimpleAdapter(getActivity()
				.getApplicationContext(), aList, R.layout.listview,
				equipmentFrom, equipmentTo);
		// Setting the adapter to the listView
		lv_equipment.setAdapter(adapter);
		//lv_equipment.setOnItemClickListener(clickListener);
		lv_equipment.setOnItemClickListener(new OnItemClickListener() {
		      public void onItemClick(AdapterView<?> parent, View view,
		          int position, long id) {
		    	  // TODO связать нажатие в списке с картой: изменить цвет маркера
		  			OverlayItem item = overlayItemArray.get(position);
		  		  // Get the new Drawable
		  			Drawable marker =view.getResources().getDrawable(R.drawable.marker_equip_selected);
		  		  // Set the new marker
		  			item.setMarker(marker);		
		  			if (LastItemPosition>=0)
		  				{
			  			 OverlayItem item2 = overlayItemArray.get(LastItemPosition);
			  			 marker =view.getResources().getDrawable(R.drawable.marker_equip);
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

				// пример тупой, но полагю это почти то что тебе было нужно
				ViewPager pager = (ViewPager) getActivity().findViewById(
						R.id.pager);
				pager.setCurrentItem(PageAdapter.TASK_FRAGMENT);

				return super.onLongPressHelper(index, item);
			}
		};
		mapView.getOverlays().add(overlay);
/*
		String[] equipmentFrom = { "", "equipment_uuid" };
		int[] equipmentTo = { R.id.lv_firstLine, R.id.lv_secondLine};
		EquipmentOperationDBAdapter equipmentOperationDBAdapter = new EquipmentOperationDBAdapter(
				new TOiRDatabaseContext(getActivity()
						.getApplicationContext()));
		EquipmentDBAdapter equipmentDBAdapter = new EquipmentDBAdapter(
				new TOiRDatabaseContext(getActivity()
						.getApplicationContext()));

		lv_equipment = (ListView) rootView.findViewById(R.id.gps_listView);		
		equipmentAdapter = new SimpleCursorAdapter(getActivity(),
				R.layout.listview, null, equipmentFrom, equipmentTo,
				CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);
		// Setting the adapter to the listView
		lv_equipment.setAdapter(equipmentAdapter);
		lv_equipment.setOnItemClickListener(clickListener);		
		equipmentAdapter.changeCursor(equipmentOperationDBAdapter.getOperationWithInfo());
		*/				
		onInit(rootView);
		
		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	public void onInit(View view) {

	}
}
