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

import java.util.ArrayList;

import org.osmdroid.DefaultResourceProxyImpl;
import org.osmdroid.ResourceProxy;

import android.os.Bundle;
import android.app.Activity;
import android.graphics.drawable.Drawable;

import org.osmdroid.views.overlay.ItemizedOverlay;
import org.osmdroid.views.overlay.OverlayItem;
import org.osmdroid.api.IMapController;

import ru.toir.mobile.gps.LocationGpsOnlyService;

public class GPSFragment extends Fragment {

//	MyItemizedOverlay myItemizedOverlay = null;
	//private ArrayList<OverlayItem> overlayItemList = new ArrayList<OverlayItem>();
	private LocationGpsOnlyService LocationGpsOnly; 

    private IMapController mapController;
	private MapView mapView;
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.gps_layout, container, false);
		//LocationGpsOnly = new LocationGpsOnlyService(getActivity());
        
		mapView = (MapView) rootView.findViewById(R.id.mapview);
        mapView.setTileSource(TileSourceFactory.MAPNIK);
        mapView.setBuiltInZoomControls(true);
        mapController = mapView.getController();
        mapController.setZoom(15);
        GeoPoint point2 = new GeoPoint(51496994, -134733);
        mapController.setCenter(point2);        
		//onInit(rootView);
		return rootView;
	}

    public void onInit(View view) {
/*	        MapView mapView = (MapView) view.findViewById(R.id.mapview);
	        mapView.setBuiltInZoomControls(true);
	         
	        Drawable marker=getResources().getDrawable(android.R.drawable.star_big_on);
	        int markerWidth = marker.getIntrinsicWidth();
	        int markerHeight = marker.getIntrinsicHeight();
	        marker.setBounds(0, markerHeight, markerWidth, 0);
	         
	        ResourceProxy resourceProxy = new DefaultResourceProxyImpl(getActivity().getApplicationContext());
	         
//	        myItemizedOverlay = new MyItemizedOverlay(marker, resourceProxy);
//	        mapView.getOverlays().add(myItemizedOverlay);	         
	        GeoPoint myPoint1 = new GeoPoint(0*1000000, 0*1000000);
	        OverlayItem newItem = new OverlayItem("f1", "f1", myPoint1);
	        overlayItemList.add(newItem);
	        LocationGpsOnly.enableLocationListening();
//	        ToastUtil.showToast(getActivity(), String.valueOf(LocationGpsOnly.getInaccurateLocation().getAltitude()));
//	        LocationGpsOnly.getLatestLocation().getAltitude();
//	        ToastUtil.showToast(getActivity(), String.valueOf(LocationGpsOnly.getLatestLocation().getAltitude()));    
//	        populate(); 
//	        myItemizedOverlay.addItem(myPoint1, "myPoint1", "myPoint1");
//	        GeoPoint myPoint2 = new GeoPoint(50*1000000, 50*1000000);
//	        myItemizedOverlay.addItem(myPoint2, "myPoint2", "myPoint2");*/	         
    } 	     
}
