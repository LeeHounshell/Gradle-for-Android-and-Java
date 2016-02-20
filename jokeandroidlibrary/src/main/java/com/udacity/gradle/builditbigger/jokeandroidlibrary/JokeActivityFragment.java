package com.udacity.gradle.builditbigger.jokeandroidlibrary;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;


/**
 * A placeholder fragment containing a simple paid view.
 */
public class JokeActivityFragment extends Fragment {
    private final static String TAG = "LEE: <" + JokeActivityFragment.class.getSimpleName() + ">";

    public JokeActivityFragment() {
        Log.v(TAG, "JokeActivityFragment");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        return inflater.inflate(R.layout.fragment_joke, container, false);
    }

}
