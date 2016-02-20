package com.udacity.gradle.builditbigger;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;
import com.udacity.gradle.builditbigger.util.AsyncJokeTaskInterface;
import com.udacity.gradle.builditbigger.util.PerformAsyncJokeTask;

import at.grabner.circleprogress.CircleProgressView;


/**
 * A placeholder fragment containing a simple free view.
 */
public class MainActivityFragment extends Fragment {
    private final static String TAG = "LEE: <" + MainActivityFragment.class.getSimpleName() + ">";

    private static MainActivityFragment mainActivityFragment;

    private CircleProgressView mCircleView;
    private InterstitialAd mInterstitialAd;
    private AdView mAdView;
    private String deepJoke;
    private String mJOKE_API_KEY;
    private PerformAsyncJokeTask mLoadJokeTask;

    public MainActivityFragment() {
        Log.v(TAG, "MainActivityFragment (free)");
        mainActivityFragment = this;
    }

    public static MainActivityFragment getMainActivityFragment() {
        Log.v(TAG, "getMainActivityFragment (free)");
        return mainActivityFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        Log.v(TAG, "onCreateView");
        View root = inflater.inflate(R.layout.fragment_main, container, false);
        mCircleView = (CircleProgressView) this.getActivity().findViewById(R.id.circle_view);
        mJOKE_API_KEY = getResources().getString(R.string.joke_api_key);
        final Activity activity = this.getActivity();

        // setup interstitial ad
        mInterstitialAd = new InterstitialAd(this.getActivity());
        mInterstitialAd.setAdUnitId(getResources().getString(R.string.ad_unit_id));
        mInterstitialAd.setAdListener(new AdListener() {
            @Override
            public void onAdClosed() {
                Log.v(TAG, "*** InterstitialAd: onAdClosed ***");
                BuildItBiggerApplication.getInstance().trackEvent("Tell Joke", "adMob", "onAdClosed");
                requestNewInterstitial();
                mLoadJokeTask = new PerformAsyncJokeTask((AsyncJokeTaskInterface) activity, mCircleView);
                mLoadJokeTask.execute(mJOKE_API_KEY, deepJoke);
            }
        });
        requestNewInterstitial();

        // setup banner ad
        mAdView = (AdView) root.findViewById(R.id.adView);
        if (mAdView != null) {
            AdRequest adRequest = getAdRequest();
            mAdView.loadAd(adRequest);
            BuildItBiggerApplication.getInstance().trackEvent("Tell Joke", "adMob", "loadAd");
        }
        else {
            Log.w(TAG, "mAdView is null!");
        }
        return root;
    }

    private void requestNewInterstitial() {
        Log.v(TAG, "requestNewInterstitial");
        AdRequest adRequest = getAdRequest();
        mInterstitialAd.loadAd(adRequest);
    }

    @NonNull
    private AdRequest getAdRequest() {
        Log.d(TAG, "--> preparing an AdRequest");

        //---------------------------------------------------------------------------------------------------------
        // IMPORTANT NOTE: the following #IFDEF #ELSE and #ENDIF directives are processed in build.gradle prior to javac
        //                 CODE IN THIS BLOCK DEPENDS ON BUILD_TYPE 'release' AND IS EDITED BY THE CUSTOM GRADLE BUILD
        //---------------------------------------------------------------------------------------------------------
        //#IFDEF 'release'
        //Log.d(TAG, "--> using the RELEASE version of AdRequest <--");
        //AdRequest adRequest = new AdRequest.Builder().build();
        //BuildItBiggerApplication.getInstance().trackEvent("adMob", "AdRequest", "RELEASE");
        //#ELSE
        Log.d(TAG, "--> using the TESTING version of AdRequest <--");
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)        // All emulators
                .addTestDevice(getResources().getString(R.string.test_device))
                .build();
        BuildItBiggerApplication.getInstance().trackEvent("adMob", "AdRequest", "TESTING");
        //#ENDIF
        //---------------------------------------------------------------------------------------------------------

        Log.d(TAG, "--> returning the AdRequest");
        return adRequest;
    }

    @Override
    public void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
        // Resume the AdView.
        if (mAdView != null) {
            mAdView.resume();
        }
    }

    @Override
    public void onPause() {
        Log.v(TAG, "onPause");
        // Pause the AdView.
        if (mAdView != null) {
            mAdView.pause();
        }
        super.onPause();
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        // Destroy the AdView.
        if (mAdView != null) {
            mAdView.destroy();
        }
        super.onDestroy();
    }

    public void retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt(boolean withInterstitialAd, String someDeepJoke) {
        Log.v(TAG, "retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt - someDeepJoke="+someDeepJoke);
        deepJoke = someDeepJoke;
        if (! mInterstitialAd.isLoaded()) {
            requestNewInterstitial();
        }
        if (withInterstitialAd && mInterstitialAd.isLoaded()) {
            Log.v(TAG, "mInterstitialAd.isLoaded()");
            mInterstitialAd.show();
        } else {
            Log.v(TAG, "NOT mInterstitialAd.isLoaded()");
            mLoadJokeTask = new PerformAsyncJokeTask((AsyncJokeTaskInterface) this.getActivity(), mCircleView);
            mLoadJokeTask.execute(mJOKE_API_KEY, deepJoke);
        }
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
        if (mLoadJokeTask != null) {
            mLoadJokeTask.cancel(true);
        }
    }

}
