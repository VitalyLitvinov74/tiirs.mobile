package ru.toir.mobile.fragments;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;

public class PageAdapter extends FragmentPagerAdapter {

	public PageAdapter(FragmentManager fm) {
		super(fm);
	}

	@Override
	public Fragment getItem(int arg0) {
		Fragment fragment;
		switch (arg0) {
		case 5 :
			fragment = new UserInfoFragment();
			break;
		case 1 :
			fragment = new OrdersFragment();
			break;
		case 2 :
			fragment = new ReferenceFragment();
			break;
		case 3 :
			fragment = new RFIDTestFragment();
			break;
		case 4 :
			fragment = new ChartsFragment();
			break;
		case 0 :
			fragment = new GPSFragment();
			break;
		default :
			fragment = null;
			break;
		}
		return fragment;
	}

	@Override
	public int getCount() {
		// пока три вкладки
		return 6;
	}

	/* (non-Javadoc)
	 * @see android.support.v4.view.PagerAdapter#getPageTitle(int)
	 */
	@Override
	public CharSequence getPageTitle(int position) {
		CharSequence title;
		switch (position) {
		case 5 :
			title = "Информация о пользователе";
			break;
		case 1 :
			title = "Наряды";
			break;
		case 2 :
			title = "Справочники";
			break;
		case 3 :
			title = "Тест RFID";
			break;
		case 4 :
			title = "Тест графиков";
			break;
		case 0 :
			title = "Тест GPS";
			break;
		default :
			title = "";
			break;
		}
		return title;
	}

}
