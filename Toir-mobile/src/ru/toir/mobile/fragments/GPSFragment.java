package ru.toir.mobile.fragments;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import java.util.ArrayList;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.os.Bundle;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.api.IMapController;
import ru.toir.mobile.db.adapters.EquipmentOperationDBAdapter;
import ru.toir.mobile.db.adapters.UsersDBAdapter;
import ru.toir.mobile.db.adapters.TaskDBAdapter;
import ru.toir.mobile.db.tables.*;
import ru.toir.mobile.gps.TestGPSListener;
import android.content.Context;
import android.location.LocationManager;
import android.location.Location;
import org.osmdroid.views.overlay.ItemizedIconOverlay;

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
		// Hardcoded !!
		String tagId = "01234567";
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		// запрашиваем данные текущего юзера, хотя нам нужен только его uuid (если он будет храниться глобально, то запрашивать постоянно уже не надо будет)		
		Users user = users.getUserByTagId(tagId);
		LocationManager lm = (LocationManager) getActivity().getSystemService(Context.LOCATION_SERVICE);
		if (lm != null) 
			{
			TestGPSListener tgpsl = new TestGPSListener((TextView)rootView.findViewById(R.id.gpsTextView),getActivity().getApplicationContext(), user.getUuid());
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
        //mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setUseDataConnection(false);
        mapView.setTileSource(TileSourceFactory.MAPQUESTOSM);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(17);
        GeoPoint point2 = new GeoPoint(curLatitude,curLongitude);
        mapController.setCenter(point2);
        
        // добавляем тестовый маркер
        aOverlayItemArray = new ArrayList<OverlayItem>();
        aOverlayItemArray.add(new OverlayItem("We are here", "WAH", new GeoPoint(curLatitude,curLongitude)));        
        ItemizedIconOverlay<OverlayItem> aItemizedIconOverlay = new ItemizedIconOverlay<OverlayItem>(getActivity().getApplicationContext(), aOverlayItemArray, null);
        mapView.getOverlays().add(aItemizedIconOverlay);
		
		TaskDBAdapter dbOrder = new TaskDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		EquipmentOperationDBAdapter equips = new EquipmentOperationDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		// запращиваем перечень задач нарядов (активных)

		ArrayList<Task> ordersList = dbOrder.getOrdersByUser(tagId, "", "");

		Integer cnt=0,cnt2=0;
		while (cnt<ordersList.size())
				{
				 // запращиваем перечень оборудования статус - hardcoded!
				 ArrayList<EquipmentOperation> equipOpList = equips.getEquipsByOrderId(ordersList.get(cnt).getUuid(),"",1);
				 cnt2=0;
				 while (cnt2<equipOpList.size())
					{
					 //equipOpList.get(cnt2).getUuid();
					 cnt2 = cnt2 +1;
					}
				 cnt=cnt +1;
				}

		equips.close();
		users.close();
		dbOrder.close();       
		onInit(rootView);
		return rootView;
	}
	
    public void onInit(View view) {
    	
    } 	     
}
