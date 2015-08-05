package ru.toir.mobile.utils;

import java.util.List;

import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.OverlayItem;

//import ru.toir.mobile.MainActivity;
//import ru.toir.mobile.R;
//import ru.toir.mobile.fragments.TaskFragment;
//import android.app.AlertDialog;
//import android.app.DialogFragment;
//import android.support.v4.app.Fragment;
//import android.support.v4.app.FragmentManager;
//import android.support.v4.app.FragmentTransaction;
//import android.support.v4.view.ViewPager;
import android.content.Context;
import android.widget.Toast;

public class TaskItemizedOverlay extends ItemizedIconOverlay<OverlayItem> {
	protected Context mContext;
	
	public TaskItemizedOverlay(final Context context, final List<OverlayItem> aList) {
		super(context, aList, new OnItemGestureListener<OverlayItem>() {
            @Override public boolean onItemSingleTapUp(final int index, final OverlayItem item) {            		
                    return false;
            }
            @Override public boolean onItemLongPress(final int index, final OverlayItem item) {
            		return false;
            }
          } );
    // TODO Auto-generated constructor stub
     mContext = context;
	}

	@Override 
	protected boolean onSingleTapUpHelper(final int index, final OverlayItem item, final MapView mapView) {
		Toast.makeText(mContext, item.getTitle(), Toast.LENGTH_SHORT).show();
		return true;
	}
}