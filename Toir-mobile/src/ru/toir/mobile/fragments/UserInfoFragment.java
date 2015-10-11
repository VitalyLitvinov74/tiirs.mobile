package ru.toir.mobile.fragments;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.TOiRDatabaseContext;
import ru.toir.mobile.db.adapters.*;
import ru.toir.mobile.db.tables.*;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import ru.toir.mobile.utils.DataUtils;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class UserInfoFragment extends Fragment {

	private TextView tv_user_name;
	private TextView tv_user_id;
	private TextView tv_user_type;
	private TextView tv_user_gps;
	private TextView tv_user_status;
	private ImageView user_image;
	
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
		View rootView = inflater
				.inflate(R.layout.user_layout, container, false);
		FillListViewTasks(rootView);
		initView(rootView);

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView(View view) {
		tv_user_id = (TextView) view.findViewById(R.id.user_text_id);
		tv_user_name = (TextView) view.findViewById(R.id.user_text_name);
		tv_user_type = (TextView) view.findViewById(R.id.user_text_type);
		tv_user_gps = (TextView) view.findViewById(R.id.user_position);
		tv_user_status = (TextView) view.findViewById(R.id.user_status);
		user_image = (ImageView) view.findViewById(R.id.user_image);
		
		String tagId = AuthorizedUser.getInstance().getTagId();

		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(
				getActivity().getApplicationContext()));
		Users user = users.getUserByTagId(tagId);
		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {
			if (user.getTag_id().length()>20)
				tv_user_id.setText("ID: " + user.getTag_id().substring(0, 20));
			else tv_user_id.setText("ID: " + user.getTag_id());
			tv_user_name.setText("ФИО: " + user.getName());
			tv_user_type.setText("Должность: " + user.getWhoIs());
			tv_user_status.setText("Статус: задание");
			GPSDBAdapter gps = new GPSDBAdapter(new TOiRDatabaseContext(
					getActivity().getApplicationContext()));
			GpsTrack gpstrack = gps.getGPSByUuid(user.getUuid());
			// Toast.makeText(getActivity(), user.getUuid(),
			// Toast.LENGTH_SHORT).show();
			if (gpstrack != null) {
				tv_user_gps.setText(Float.parseFloat(gpstrack.getLatitude()) + " / "
						+ Float.parseFloat(gpstrack.getLongitude()));
			}
			
			if (AuthorizedUser.getInstance().getTagId().equals("3000E2004000860902332580112D")) {
				// ваще хардкодед для демонстрашки
				// TODO реальные фотки должны адресоваться из базы
				File imgFile = new File(Environment.getExternalStorageDirectory().getAbsolutePath() + File.separator + "Android" + File.separator + "data" + File.separator + getActivity().getApplicationContext().getPackageName() + File.separator + "img" + File.separator+ "m_kazantcev.jpg");						
					 if(imgFile.exists() && imgFile.isFile()){
						 Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
						 user_image.setImageBitmap(myBitmap);			    			    
					 }			
				}
		}
	}

	private void FillListViewTasks(View view) {
		String tagId = AuthorizedUser.getInstance().getTagId();
		UsersDBAdapter users = new UsersDBAdapter(new TOiRDatabaseContext(
				getActivity().getApplicationContext()));
		Users user = users.getUserByTagId(tagId);

		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {
			TaskDBAdapter dbOrder = new TaskDBAdapter(new TOiRDatabaseContext(
					getActivity().getApplicationContext()));
			//ArrayList<Task> ordersList = dbOrder.getOrdersByUser(user.getUuid(), "", "");
			ArrayList<Task> ordersList = dbOrder.getOrders();
			TaskStatusDBAdapter taskStatusDBAdapter = new TaskStatusDBAdapter(
					new TOiRDatabaseContext(getActivity()
							.getApplicationContext()));

			Integer cnt = 0;
			List<HashMap<String, String>> aList = new ArrayList<HashMap<String, String>>();
			String[] from = { "name", "img" };
			int[] to = { R.id.lv1_firstLine, R.id.lv1_icon };
			while (cnt < ordersList.size()) {
				HashMap<String, String> hm = new HashMap<String, String>();
				hm.put("name",
						"["
								+ DataUtils.getDate(ordersList.get(cnt)
										.getCreatedAt(), "dd-MM-yyyy hh:mm")
								+ "] Статус: "
								+ taskStatusDBAdapter.getNameByUUID(ordersList
										.get(cnt).getTask_status_uuid()));
				// default

				hm.put("img", Integer.toString(R.drawable.checkmark_32));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_UNCOMPLETED))
					hm.put("img", Integer.toString(R.drawable.forbidden_32));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_COMPLETED))
					hm.put("img", Integer.toString(R.drawable.checkmark_32));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_RECIEVED))
					hm.put("img", Integer.toString(R.drawable.information_32));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_CREATED))
					hm.put("img", Integer.toString(R.drawable.help_32));
				if (ordersList.get(cnt).getTask_status_uuid()
						.equals(TaskStatusDBAdapter.STATUS_UUID_ARCHIVED))
					hm.put("img", Integer.toString(R.drawable.help_32));
				aList.add(hm);
				cnt++;
			}
			SimpleAdapter adapter = new SimpleAdapter(getActivity()
					.getApplicationContext(), aList, R.layout.listview1row,
					from, to);
			// Setting the adapter to the listView
			ListView lv;
			lv = (ListView) view.findViewById(R.id.user_listView_main);
			lv.setAdapter(adapter);
		}
	}
}
