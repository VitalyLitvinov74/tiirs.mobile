package ru.toir.mobile.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import ru.toir.mobile.fragments.EquipmentsFragment;

public class PageAdapter extends FragmentPagerAdapter {
	
	public final static int USER_FRAGMENT = 0;
	public final static int REFERENCE_FRAGMENT = 1;
	public final static int CHARTS_FRAGMENT = 2;
	public final static int GPS_FRAGMENT = 3;
	public final static int EQUIPMENTS_FRAGMENT = 4;
	public final static int TASK_FRAGMENT = 5;
	public final static int NATIVE_CAMERA_FRAGMENT = 6;
	public final static int QRTEST_FRAGMENT = 7;

	private UserInfoFragment userInfoFragment;
	private ReferenceFragment referenceFragment;
	private ChartsFragment chartsFragment;
	private GPSFragment gpsFragment;
	private EquipmentsFragment equipmentsFragment;
	private TaskFragment taskFragment;
	private NativeCameraFragment nativeCameraFragment;
	private QRTestFragment qrTestFragment;
	
	public PageAdapter(FragmentManager fm) {	
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		switch (arg0) {
		case USER_FRAGMENT :
			if (userInfoFragment == null) {
				userInfoFragment = new UserInfoFragment();
			}
			return userInfoFragment;
		case REFERENCE_FRAGMENT :
			if (referenceFragment == null) {
				referenceFragment = new ReferenceFragment();
			}
			return referenceFragment;
		case CHARTS_FRAGMENT :
			if (chartsFragment == null) {
				chartsFragment = new ChartsFragment();
			}
			return chartsFragment;
		case GPS_FRAGMENT :
			if (gpsFragment == null) {
				gpsFragment = new GPSFragment();
			}
			return gpsFragment;
		case EQUIPMENTS_FRAGMENT :
			if (equipmentsFragment == null) {
				equipmentsFragment = new EquipmentsFragment();
			}
			return equipmentsFragment;
		case TASK_FRAGMENT :
			if (taskFragment == null) {
				taskFragment = new TaskFragment();
			}
			return taskFragment;
		case NATIVE_CAMERA_FRAGMENT :
			if (nativeCameraFragment == null) {
				nativeCameraFragment = new NativeCameraFragment();
			}
			return nativeCameraFragment;
		case QRTEST_FRAGMENT :
			if (qrTestFragment == null) {
				qrTestFragment = new QRTestFragment();
			}
			return qrTestFragment;
		default :
			return null;
		}

	}

	@Override
	public int getCount() {
		return 8;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		CharSequence title;
		switch (position) {
		case USER_FRAGMENT :
			title = "Пользователь";
			break;
		case REFERENCE_FRAGMENT :
			title = "Справочники";
			break;
		case CHARTS_FRAGMENT :
			title = "Графики";
			break;
		case GPS_FRAGMENT :
			title = "Тест GPS";
			break;
		case EQUIPMENTS_FRAGMENT :
			title = "Оборудование";
			break;
		case TASK_FRAGMENT :
			title = "Наряды";
			break;
		case NATIVE_CAMERA_FRAGMENT :
			title = "Фото";
			break;
		case QRTEST_FRAGMENT :
			title = "QR test";
			break;
		default :
			title = "";
			break;
		}
		return title;
	}

}