package com.udacity.gradle.builditbigger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.udacity.gradle.builditbigger.util.AsyncJokeTaskInterface;
import com.udacity.gradle.builditbigger.util.PerformAsyncJokeTask;

import at.grabner.circleprogress.CircleProgressView;


/**
 * A placeholder fragment containing a simple paid view.
 */
public class MainActivityFragment extends Fragment {
    private final static String TAG = "LEE: <" + MainActivityFragment.class.getSimpleName() + ">";

    private static MainActivityFragment mainActivityFragment;

    private CircleProgressView mCircleView;
    private String deepJoke;
    private String mJOKE_API_KEY;
    private PerformAsyncJokeTask mLoadJokeTask;

    public MainActivityFragment() {
        Log.v(TAG, "MainActivityFragment (paid)");
        mainActivityFragment = this;
    }

    public static MainActivityFragment getMainActivityFragment() {
        Log.v(TAG, "getMainActivityFragment (paid)");
        return mainActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mCircleView = (CircleProgressView) getActivity().findViewById(R.id.circle_view);
        mJOKE_API_KEY = getResources().getString(R.string.joke_api_key);
        return root;
    }

    public void retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt(@SuppressWarnings("UnusedParameters") boolean withInterstitialAd, String someDeepJoke) {
        Log.v(TAG, "retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt - someDeepJoke="+someDeepJoke);
        deepJoke = someDeepJoke;
        mLoadJokeTask = new PerformAsyncJokeTask((AsyncJokeTaskInterface) getActivity(), getCircleView());
        mLoadJokeTask.execute(mJOKE_API_KEY, deepJoke);
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
        if (mLoadJokeTask != null) {
            mLoadJokeTask.cancel(true);
        }
    }

    public CircleProgressView getCircleView() {
        return mCircleView;
    }

}
