package ru.toir.mobile.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.os.Environment;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import io.realm.Realm;
import io.realm.RealmResults;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.GPSDBAdapter;
import ru.toir.mobile.db.realm.OrderStatus;
import ru.toir.mobile.db.realm.Orders;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.db.tables.GpsTrack;

public class UserInfoFragment extends Fragment {
    private Realm realmDB;

	private TextView tv_user_name;
	private TextView tv_user_id;
	private TextView tv_user_type;
	private TextView tv_user_gps;
	private TextView tv_user_status;
	private ImageView user_image;
    private ImageView user_status_gps;
    private ImageView user_status_gprs;

	private TextView tv_user_date;
	private TextView tv_user_tasks;
	private TextView tv_user_boss;

    public static UserInfoFragment newInstance() {
        return (new UserInfoFragment());
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
		View rootView = inflater
				.inflate(R.layout.user_layout, container, false);
        realmDB = Realm.getDefaultInstance();
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
		tv_user_date = (TextView) view.findViewById(R.id.user_text_date);
		tv_user_tasks = (TextView) view.findViewById(R.id.user_text_tasks);
		tv_user_boss = (TextView) view.findViewById(R.id.user_text_boss);

        user_status_gps = (ImageView) view.findViewById(R.id.user_status_gps);
        user_status_gprs = (ImageView) view.findViewById(R.id.user_status_gprs);

        user_status_gprs.setImageResource(R.drawable.ic_stat_name);
        user_status_gps.setImageResource(R.drawable.ic_action_name);
		//String tagId = AuthorizedUser.getInstance().getTagId();
		//UsersDBAdapter users = new UsersDBAdapter(new ToirDatabaseContext(
		//		getActivity().getApplicationContext()));
		//Users user = users.getUserByTagId(tagId);
        User user = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findFirst();
        //RealmResults<User> users = realmDB.where(User.class).findAll();
        if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {
			if (user.getTagId().length() > 20)
				tv_user_id.setText("ID: " + user.getTagId().substring(0, 20));
			else
				tv_user_id.setText("ID: " + user.getTagId());
			tv_user_name.setText("ФИО: " + user.getName());
			tv_user_date.setText(DateFormat.getDateTimeInstance().format(new Date()));
			tv_user_tasks.setText("2/2");
			tv_user_boss.setText(user.getContact());

			tv_user_type.setText("Должность: " + user.getWhoIs());
			tv_user_status.setText("Статус: задание");
			GPSDBAdapter gps = new GPSDBAdapter(new ToirDatabaseContext(
					getActivity().getApplicationContext()));
			GpsTrack gpstrack = gps.getGPSByUuid(user.getUuid());
			// Toast.makeText(getActivity(), user.getUuid(),
			// Toast.LENGTH_SHORT).show();

            LocationManager manager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS)
                user_status_gps.setImageResource(R.drawable.checkmark_32);
            else
                user_status_gps.setImageResource(R.drawable.forbidden_32);

            ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    user_status_gprs.setImageResource(R.drawable.wifi_32);
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    user_status_gprs.setImageResource(R.drawable.gprs_32);
                }
            } else {
                user_status_gprs.setImageResource(R.drawable.forbidden_32);
            }

			if (gpstrack != null) {
				tv_user_gps.setText(Float.parseFloat(gpstrack.getLatitude())
						+ " / " + Float.parseFloat(gpstrack.getLongitude()));
			}

			if (AuthorizedUser.getInstance().getTagId()
					.equals("3000E2004000860902332580112D")) {
				// ваще хардкодед для демонстрашки
				// TODO реальные фотки должны адресоваться из базы
				File imgFile = new File(Environment
						.getExternalStorageDirectory().getAbsolutePath()
						+ File.separator
						+ "Android"
						+ File.separator
						+ "data"
						+ File.separator
						+ getActivity().getApplicationContext()
								.getPackageName()
						+ File.separator
						+ "img"
						+ File.separator + "m_kazantcev.jpg");
				if (imgFile.exists() && imgFile.isFile()) {
					Bitmap myBitmap = BitmapFactory.decodeFile(imgFile
							.getAbsolutePath());
					user_image.setImageBitmap(myBitmap);
				}
			}
		}
	}

	private void FillListViewTasks(View view) {
		//String tagId = AuthorizedUser.getInstance().getTagId();
		//UsersDBAdapter users = new UsersDBAdapter(new ToirDatabaseContext(
		//		getActivity().getApplicationContext()));
        User user = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findFirst();
		//Users user = users.getUserByTagId(tagId);
		if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {
			//TaskDBAdapter taskDBAdapter = new TaskDBAdapter(
			//		new ToirDatabaseContext(getActivity()
			//				.getApplicationContext()));
			//ArrayList<Task> taskList = taskDBAdapter.getOrders();

            RealmResults<Orders> orders = realmDB.where(Orders.class).equalTo("userUuid", user.getUuid()).findAll();
            //RealmResults<Orders> orders = realmDB.where(Orders.class).findAll();

			List<HashMap<String, String>> elementList = new ArrayList<HashMap<String, String>>();
			String[] from = { "name", "img" };
			int[] to = { R.id.lv1_firstLine, R.id.lv1_icon };
			String orderStatusUuid;
            String orderStatusTitle="неизвестен";
			HashMap<String, String> element;

			for (Orders item : orders) {
				element = new HashMap<>();
                OrderStatus orderStatus = realmDB.where(OrderStatus.class).equalTo("uuid",item.getOrderStatusUuid()).findFirst();
                if (orderStatus!=null) orderStatusTitle=orderStatus.getTitle();
				element.put(
						"name",
						"["
                                + new SimpleDateFormat("dd.MM.YYYY HH:ss", Locale.US).format(item.getReceiveDate())
                                + "] Статус: "
								+ orderStatusTitle);
				// default
				element.put("img", Integer.toString(R.drawable.checkmark_32));
                orderStatusUuid = item.getOrderStatusUuid();

				if (orderStatusUuid
						.equals(OrderStatus.Status.UNCOMPLETE)) {
					element.put("img",
							Integer.toString(R.drawable.forbidden_32));
				}

				if (orderStatusUuid.equals(OrderStatus.Status.COMPLETE)) {
					element.put("img",
							Integer.toString(R.drawable.checkmark_32));
				}

				if (orderStatusUuid.equals(OrderStatus.Status.IN_WORK)) {
					element.put("img",
							Integer.toString(R.drawable.information_32));
				}

				if (orderStatusUuid.equals(OrderStatus.Status.NEW)) {
					element.put("img", Integer.toString(R.drawable.help_32));
				}

				elementList.add(element);
			}

			SimpleAdapter adapter = new SimpleAdapter(getActivity()
					.getApplicationContext(), elementList,
					R.layout.listview1row, from, to);

			ListView lv;
			lv = (ListView) view.findViewById(R.id.user_listView_main);
			lv.setAdapter(adapter);
		}
	}

}
