package ru.toir.mobile.fragments;
import java.util.ArrayList;

import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.*;
import ru.toir.mobile.db.tables.*;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.TableRow;

public class UserInfoFragment extends Fragment {

	private TextView tv_user_name;
	private TextView tv_user_id;
	private TextView tv_user_type;
	private TextView tv_user_gps;
	private TableLayout tl_task;
	
	/* (non-Javadoc)
	 * @see android.support.v4.app.Fragment#onCreateView(android.view.LayoutInflater, android.view.ViewGroup, android.os.Bundle)
	 */
	@Override
	public View onCreateView(LayoutInflater inflater,
			@Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
		View rootView = inflater.inflate(R.layout.user_layout, container, false);
		initView(rootView);
		return rootView;
	}
	private void initView(View view) {
		tv_user_id = (TextView) view.findViewById(R.id.EditText07);
		tv_user_name = (TextView) view.findViewById(R.id.EditText06);
		tv_user_type = (TextView) view.findViewById(R.id.EditText05);
		tv_user_gps = (TextView) view.findViewById(R.id.editText7);
		tl_task = (TableLayout) view.findViewById(R.id.TableLayout01);

		// hardcoded for test, tagID for user must be global
		String tagId = "01234567";
		
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
		Users user = users.getUserByTagId(tagId);
		users.close();
		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
		} else {
			//tv_user_id.setText(user.getTag_id());
			//tv_user_name.setText(user.getName());
			//tv_user_type.setText(user.getType());
			//GPSDBAdapter gps = new GPSDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
			//GpsTrack gpstrack = gps.getGPSByUuid(user.getUuid());
			//gps.close();
			//tv_user_gps.setText(gpstrack.getLatitude().toString() + " / " + gpstrack.getLongitude().toString());
			
			//TaskDBAdapter dbOrder = new TaskDBAdapter(new TOiRDatabaseContext(getActivity().getApplicationContext())).open();
			//ArrayList<Task> ordersList = dbOrder.getOrdersByTagId(tagId);
			
			Integer cnt=0;
			//while (cnt<ordersList.size())
					{
				/*
			         // Creation row
			         final TableRow tableRow = new TableRow(getActivity().getApplicationContext());
			         tableRow.setLayoutParams(new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT));

			         // Creation textView
			         final TextView text = new TextView(getActivity().getApplicationContext());
			         //text.setText(ordersList);
			         text.setLayoutParams(new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT));

			         // Creation row
			         final ImageView img = new ImageView(getActivity().getApplicationContext());
			         img.setImageResource(R.drawable.forbidden_32);
			         final TextView textv = new TextView(getActivity().getApplicationContext());			         			         
			         textv.setText("Статус наряда: " + dbOrder.getStatusNameByUUID(ordersList.get(cnt).getTask_status_uuid()) + dbOrder.getCompleteTimeByUUID(ordersList.get(cnt).getTask_status_uuid()));			         
			         tableRow.addView(img);
			         tableRow.addView(textv);
			    	 tl_task.addView(tableRow);
			    	 */
					 cnt= cnt +1;
					 //if (cnt>5) break;
					}
			//users.close();
			//dbOrder.close();       			
		}
	}
}
