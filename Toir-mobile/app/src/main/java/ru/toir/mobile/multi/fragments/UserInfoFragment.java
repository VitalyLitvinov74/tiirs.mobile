package ru.toir.mobile.multi.fragments;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
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
import java.util.Locale;

import io.realm.Realm;
import ru.toir.mobile.multi.AuthorizedUser;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.db.realm.User;
import ru.toir.mobile.multi.utils.IconTextView;

import static ru.toir.mobile.multi.utils.RoundedImageView.getResizedBitmap;

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
    public View onCreateView(@NonNull LayoutInflater inflater,
                             @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.user_layout, container, false);
        FragmentActivity activity = getActivity();
        if (activity != null) {
            Toolbar toolbar = activity.findViewById(R.id.toolbar);
            toolbar.setSubtitle(getString(R.string.menu_user));

            realmDB = Realm.getDefaultInstance();
            initView(rootView);

            rootView.setFocusableInTouchMode(true);
            rootView.requestFocus();

            return rootView;
        } else {
            return null;
        }
    }

    private void initView(View view) {
        TextView tv_user_name = view.findViewById(R.id.user_text_name);
        TextView tv_user_id = view.findViewById(R.id.user_text_uuid);
        TextView tv_user_type = view.findViewById(R.id.user_text_type);
        TextView tv_user_gps = view.findViewById(R.id.user_text_location);
        ImageView user_image = view.findViewById(R.id.user_image);
        IconTextView call_image = view.findViewById(R.id.user_boss_contact);
        Switch user_status_gps = view.findViewById(R.id.user_status_gps_switch);
        TextView tv_user_date = view.findViewById(R.id.user_text_date);
        TextView tv_user_boss = view.findViewById(R.id.user_text_boss);

        final User user = realmDB.where(User.class).equalTo("tagId", AuthorizedUser.getInstance().getTagId()).findFirst();
        if (user == null) {
            Toast.makeText(getActivity(), "Нет такого пользователя!", Toast.LENGTH_SHORT).show();
        } else {
            //if (user.getTagId().length() > 20) tv_user_id.setText("ID: " + user.getTagId().substring(4, 24));
            tv_user_id.setText(getString(R.string.id, user.getTagId()));
            tv_user_name.setText(user.getName());
            tv_user_date.setText(DateFormat.getDateTimeInstance().format(new Date()));
            tv_user_boss.setText(user.getContact());
            tv_user_type.setText(user.getWhoIs());

            FragmentActivity activity = getActivity();
            if (activity != null) {
                LocationManager manager = (LocationManager) activity.getApplicationContext()
                        .getSystemService(Context.LOCATION_SERVICE);
                int permission = ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
                if (manager != null && permission == PackageManager.PERMISSION_GRANTED) {
                    boolean statusOfGPS = manager.isProviderEnabled(LocationManager.GPS_PROVIDER);
                    String provider = manager.getBestProvider(new Criteria(), false);
                    Location location = manager.getLastKnownLocation(provider);
                    if (statusOfGPS && location != null) {
                        user_status_gps.setChecked(true);
                        String result = String.format(Locale.ROOT, "%.4f", location.getLongitude()) +
                                ", " + String.format(Locale.ROOT, "%.4f", location.getLatitude());
                        tv_user_gps.setText(result);
                    } else {
                        user_status_gps.setChecked(false);
                        tv_user_gps.setText("не определено");
                    }
                }
            }

/*
            if (activity != null) {
                ConnectivityManager cm = (ConnectivityManager) activity.getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);
                if (cm != null) {
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
                }
            }
*/

            if (user.getContact() != null) {
                call_image.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        Intent intent = new Intent(Intent.ACTION_DIAL, Uri.parse("tel:" + user.getContact()));
                        startActivity(intent);
                    }
                });
            }

            String path = getActivity().getExternalFilesDir("/" + User.getImageRoot()) + File.separator;
            Bitmap user_bitmap = getResizedBitmap(path, user.getImage(), 0, 600, user.getChangedAt().getTime());
            if (user_bitmap != null) {
                user_image.setImageBitmap(user_bitmap);
            }
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        realmDB.close();
    }
}
