package com.udacity.gradle.builditbigger;

import android.app.Application;
import android.content.pm.PackageInfo;
import android.test.ApplicationTestCase;
import android.util.Log;


/**
 * <a href="http://d.android.com/tools/testing/testing_android.html">Testing Fundamentals</a>
 */
public class ApplicationTest extends ApplicationTestCase<Application> {

    private final static String TAG = "LEE: <" + ApplicationTest.class.getSimpleName() + ">";

    private Application application;

    public ApplicationTest() {
        super(Application.class);
        Log.v(TAG, "ApplicationTest");
    }

    protected void setUp() throws Exception {
        super.setUp();
        Log.v(TAG, "setUp");
        createApplication();
        application = getApplication();
    }

    public void testCorrectVersion() throws Exception {
        Log.v(TAG, "testCorrectVersion");
        PackageInfo info = application.getPackageManager().getPackageInfo(application.getPackageName(), 0);
        assertNotNull(info);
        String version = info.versionName;
        Log.v(TAG, "found version="+version);

        //#IFDEF 'free'
        //assertEquals(version, "1.0-Free");
        //#ELSE
        assertEquals(version, "1.0-Paid");
        //#ENDIF
    }

}
