package com.udacity.gradle.builditbigger;


import android.os.Bundle;
import android.preference.PreferenceActivity;
import android.preference.PreferenceFragment;
import android.util.Log;

public class SettingsActivity extends PreferenceActivity {
    private final static String TAG = "LEE: <" + SettingsActivity.class.getSimpleName() + ">";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        getFragmentManager().beginTransaction().replace(android.R.id.content, new MyPreferenceFragment()).commit();
    }

    public static class MyPreferenceFragment extends PreferenceFragment
    {
        private final static String TAG = "LEE: <" + MyPreferenceFragment.class.getSimpleName() + ">";

        @Override
        public void onCreate(final Bundle savedInstanceState)
        {
            Log.v(TAG, "onCreate");
            super.onCreate(savedInstanceState);
            addPreferencesFromResource(R.xml.preferences);
            BuildItBiggerApplication.getInstance().trackEvent("Build it Bigger", "Settings", "onCreate");
        }
    }

}
