/*
 * This is a test project for toir application
 */

package ru.toir.mobile.robotest;

import android.test.ActivityInstrumentationTestCase2;
import android.view.View;

import com.robotium.solo.Solo;

import ru.toir.mobile.multi.MainActivity;
import ru.toir.mobile.multi.R;
import ru.toir.mobile.multi.ToirApplication;

public class AllActionTest extends ActivityInstrumentationTestCase2<MainActivity> {

    private Solo solo;

    public AllActionTest() {
        super(ToirApplication.packageName, MainActivity.class);
    }

    public void setUp() throws Exception {
        solo = new Solo(getInstrumentation(), getActivity());
    }

    public void testAttendance() throws Exception {
        solo.waitForActivity("MainActivity", 2000);
        //View login_view = solo.getView(ru.toir.mobile.R.layout.login_layout);
        //assertCurrentActivity("Desired View is not visible", ru.toir.mobile.multi.MainActivity);
        //solo.pressSpinnerItem(ru.toir.mobile.R.id.);
        assertEquals("Это не экран входа", View.VISIBLE,
                solo.getView(R.id.loginButton).getVisibility());

        View view_menu = solo.getView(R.id.menuButton);
        solo.clickOnView(view_menu);
        solo.clickOnText("Основные");
        solo.clickOnText("Сервер системы");
        solo.clearEditText(0);
        solo.enterText(0, "http://toir.tehnosber.ru/api!!");
        solo.clickOnButton(0);
        solo.clickOnText("Сервер системы");
        solo.clearEditText(0);
        solo.enterText(0, "http://toir.tehnosber.ru/api");
        solo.clickOnButton(0);
        solo.clickOnText("Драйвер считывателя RFID");
        solo.clickInList(8);
        solo.goBack();
        solo.goBack();

        View view_login = solo.getView(R.id.loginButton);
        solo.clickOnView(view_login);
        solo.pressSpinnerItem(0, 2);
        solo.clickOnButton(1);

        View view_order = solo.getView(R.id.menu_orders);
        solo.clickOnView(view_order);
        solo.waitForActivity("OrdersFragment", 2000);

        View view_user = solo.getView(R.id.menu_user);
        solo.clickOnView(view_user);
        solo.waitForActivity("UserInfoFragment", 2000);

        View view_equipment = solo.getView(R.id.menu_equipments);
        solo.clickOnView(view_equipment);
        solo.waitForActivity("EquipmentsFragment", 2000);

        View view_gps = solo.getView(R.id.gps_mapview);
        solo.clickOnView(view_gps);
        solo.waitForActivity("GPSFragment", 2000);

        //EditText address = solo.getEditText("serverUrl");

        //View view_login = solo.getView(R.id.loginButton);
        //solo.clickOnView(view_login);

        //View view_order = solo.getView(R.id.menu_orders);
        //solo.clickOnView(view_order);
        //solo.waitForActivity("OrdersFragment", 2000);

        //View view_equipments = solo.getView(R.id.menu_equipments);
        //solo.clickOnView(view_equipments);

        //solo.scrollToTop(); // I put this in here so that it always keeps the list at start
        //solo.clickOnView(solo.getView(TextView.class, 10));
    }
}
