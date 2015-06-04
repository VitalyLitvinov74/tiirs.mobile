package ru.toir.mobile.fragments;

import ru.toir.mobile.MainActivity;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRApplication;
import ru.toir.mobile.utils.ToastUtil;

import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapController;
import org.osmdroid.views.MapView;
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

import ru.toir.mobile.gps.TestGPSListener;
import android.location.LocationManager;
import android.location.Location;

public class GPSFragment extends Fragment {
    private IMapController mapController;
	private MapView mapView;
	private double curLatitude, curLongitude; 
	Location location;
	TextView gpsLog;
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gps_layout, container, false);

		LocationManager lm = (LocationManager) getActivity().getSystemService(getActivity().getApplicationContext().LOCATION_SERVICE);
		if (lm != null) {
	        ToastUtil.showToast(getActivity(), "lm");			
			TestGPSListener tgpsl = new TestGPSListener((TextView)rootView.findViewById(R.id.gpsTextView));
			lm.requestLocationUpdates(LocationManager.GPS_PROVIDER, 3000, 1, tgpsl);
			location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);			
			//ToastUtil.showToast(getActivity(), String.valueOf(location.getAltitude()));
			//ToastUtil.showToast(getActivity(), String.valueOf(location.getLatitude()));
			//ToastUtil.showToast(getActivity(), String.valueOf(location.getLongitude()));
			gpsLog=(TextView)rootView.findViewById(R.id.gpsTextView);
			curLatitude=location.getLatitude();
			curLongitude=location.getLongitude();
			gpsLog.append("Altitude:"+String.valueOf(location.getAltitude())+"\n");
			gpsLog.append("Latitude:"+String.valueOf(location.getLatitude())+"\n");
			gpsLog.append("Longtitude:"+String.valueOf(location.getLongitude())+"\n");
		}		
		mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(18);
        GeoPoint point2 = new GeoPoint(curLatitude,curLongitude);
        mapController.setCenter(point2);        
		//onInit(rootView);
		return rootView;
	}
    public void RecordGPSData(Double Latitude, Double Longitude) {
		curLatitude=location.getAltitude();
		curLongitude=location.getLatitude();    	
		gpsLog.append("Latitude:"+String.valueOf(location.getLatitude())+"\n");
		gpsLog.append("Longitude:"+String.valueOf(location.getLongitude())+"\n");		
    }
    public void onInit(View view) {
    } 	     
}
