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
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.text.DateFormat;
import java.util.Date;

import io.realm.Realm;
import ru.toir.mobile.AuthorizedUser;
import ru.toir.mobile.R;
import ru.toir.mobile.ToirDatabaseContext;
import ru.toir.mobile.db.adapters.GPSDBAdapter;
import ru.toir.mobile.db.realm.User;
import ru.toir.mobile.db.tables.GpsTrack;

public class UserInfoFragment extends Fragment {
    private Realm realmDB;

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
        Toolbar toolbar = (Toolbar)(getActivity()).findViewById(R.id.toolbar);
        toolbar.setSubtitle("Пользователь");

        realmDB = Realm.getDefaultInstance();
		initView(rootView);

		rootView.setFocusableInTouchMode(true);
		rootView.requestFocus();

		return rootView;
	}

	private void initView(View view) {
        TextView tv_user_name;
        TextView tv_user_id;
        TextView tv_user_type;
        TextView tv_user_gps;
        ImageView user_image;
        ImageView edit_image;
        Switch user_status_gps;
        Switch user_status_gprs;
        TextView tv_user_date;
        TextView tv_user_boss;

		tv_user_id = (TextView) view.findViewById(R.id.user_text_uuid);
		tv_user_name = (TextView) view.findViewById(R.id.user_text_name);
		tv_user_type = (TextView) view.findViewById(R.id.user_text_type);
		tv_user_gps = (TextView) view.findViewById(R.id.user_text_location);
        user_image = (ImageView) view.findViewById(R.id.user_image);
        edit_image = (ImageView) view.findViewById(R.id.user_edit_image);
        tv_user_date = (TextView) view.findViewById(R.id.user_text_date);
        tv_user_boss = (TextView) view.findViewById(R.id.user_text_boss);
        user_status_gps = (Switch) view.findViewById(R.id.user_status_gps_switch);
        user_status_gprs = (Switch) view.findViewById(R.id.user_status_gprs_switch);

        edit_image.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.frame_container, FragmentEditUser.newInstance("EditProfile")).commit();
            }
        });

        final User user = realmDB.where(User.class).equalTo("tagId",AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user == null) {
			Toast.makeText(getActivity(), "Нет такого пользователя!",
					Toast.LENGTH_SHORT).show();
		} else {

            realmDB.executeTransaction(new Realm.Transaction() {
                @Override
                public void execute(Realm realm) {
                     user.setContact("+79227000285 Курнаков И.И.");
                     user.setWhoIs("Ведущий инженер");
                     user.setConnectionDate(new Date());
                    }
                });
            if (user.getTagId().length() > 20)
				tv_user_id.setText("ID: " + user.getTagId().substring(0, 20));
			else
				tv_user_id.setText("ID: " + user.getTagId());
			tv_user_name.setText(user.getName());
			tv_user_date.setText(DateFormat.getDateTimeInstance().format(new Date()));
			tv_user_boss.setText(user.getContact());
			tv_user_type.setText(user.getWhoIs());
			GPSDBAdapter gps = new GPSDBAdapter(new ToirDatabaseContext(
					getActivity().getApplicationContext()));
			GpsTrack gpstrack = gps.getGPSByUuid(user.getUuid());

            LocationManager manager = (LocationManager) getActivity().getApplicationContext().getSystemService(Context.LOCATION_SERVICE);
            boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
            if (statusOfGPS)
                user_status_gps.setChecked(true);
            else
                user_status_gps.setChecked(false);

            ConnectivityManager cm = (ConnectivityManager) getActivity().getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
            NetworkInfo activeNetwork = cm.getActiveNetworkInfo();
            if (activeNetwork != null) { // connected to the internet
                if (activeNetwork.getType() == ConnectivityManager.TYPE_WIFI) {
                    // connected to wifi
                    user_status_gprs.setChecked(true);
                } else if (activeNetwork.getType() == ConnectivityManager.TYPE_MOBILE) {
                    // connected to the mobile provider's data plan
                    user_status_gprs.setChecked(false);
                }
            } else {
                user_status_gprs.setChecked(false);
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
}
