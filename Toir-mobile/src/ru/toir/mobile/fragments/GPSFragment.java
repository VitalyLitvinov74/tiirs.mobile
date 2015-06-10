package ru.toir.mobile.fragments;

import ru.toir.mobile.MainActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRApplication;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.utils.ToastUtil;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;

import java.util.ArrayList;
import java.util.UUID;

import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.api.IMapController;

import ru.toir.mobile.db.adapters.EquipmentDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.adapters.GPSDBAdapter;
import ru.toir.mobile.db.tables.GpsTrack;
import ru.toir.mobile.db.tables.Users;
import ru.toir.mobile.db.tables.Task;
import ru.toir.mobile.gps.TestGPSListener;
import android.location.LocationManager;
import android.location.Location;

import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

public class GPSFragment extends Fragment {
    private IMapController mapController;
	private MapView mapView;
	private double curLatitude, curLongitude; 
	Location location;
	TextView gpsLog;
	ArrayList<OverlayItem> aOverlayItemArray;
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gps_layout, container, false);
		LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);
		if (lm != null) {
			TestGPSListener tgpsl = new TestGPSListener((TextView)rootView.findViewById(R.id.gpsTextView),getActivity().getApplicationContext());
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, tgpsl);
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);			
			gpsLog=(TextView)rootView.findViewById(R.id.gpsTextView);
			if (location != null)
				{
				 curLatitude=location.getLatitude();
				 curLongitude=location.getLongitude();
				 gpsLog.append("Altitude:"+String.valueOf(location.getAltitude())+"\n");
				 gpsLog.append("Latitude:"+String.valueOf(location.getLatitude())+"\n");
				 gpsLog.append("Longtitude:"+String.valueOf(location.getLongitude())+"\n");
				}
		}		
		mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(18);
        GeoPoint point2 = new GeoPoint(curLatitude,curLongitude);
        mapController.setCenter(point2);
        
        // добавляем тестовый маркер
        aOverlayItemArray = new ArrayList<OverlayItem>();
        aOverlayItemArray.add(new OverlayItem("We are here", "WAH", new GeoPoint(curLatitude,curLongitude)));        
        ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getActivity().getApplicationContext(), aOverlayItemArray, null);
        mapView.getOverlays().add(aItemizedIconOverlay);
		
		String tagId = "01234567";
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		TaskDBAdapter dbOrder = new TaskDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		EquipmentDBAdapter equips = new EquipmentDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		Users user = users.getUserByTagId(tagId);
		Task order[] = dbOrder.getOrdersByTagId(tagId);
		//Equipment equip = equips.getEquipsByOrderId(orderId);

		equips.close();
		users.close();
		dbOrder.close();
        
        onInit(rootView);
		//location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);			
		return rootView;
	}
	
    public void onInit(View view) {
    	
    } 	     
}
