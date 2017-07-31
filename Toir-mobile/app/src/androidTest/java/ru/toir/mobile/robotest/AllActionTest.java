/*
 * This is a test project for toir application
 */

package ru.toir.mobile.robotest;

import com.robotium.solo.Solo;
import ru.toir.mobile.MainActivity;
import android.test.ActivityInstrumentationTestCase2;

import android.view.View;
import android.widget.Spinner;

import ru.toir.mobile.*;
import ru.toir.mobile.R;

public class AllActionTest extends ActivityInstrumentationTestCase2<MainActivity>{

    private Solo solo;
    public AllActionTest() {
        super("ru.toir.mobile",MainActivity.class);
    }

    public void setUp()throws Exception{
        solo=new Solo(getInstrumentation(),getActivity());
    }

    public void testAttendance()throws Exception{
        solo.waitForActivity("MainActivity", 2000);
        View login_view = solo.getView(R.layout.login_layout);
        assertEquals("Desired View is not visible", login_view.getVisibility(),View.VISIBLE);
        View view1 = solo.getView(Spinner.class, 1);
        solo.clickOnView(view1);

        //solo.scrollToTop(); // I put this in here so that it always keeps the list at start
        //solo.clickOnView(solo.getView(TextView.class, 10));
    }
}
