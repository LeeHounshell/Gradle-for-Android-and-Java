package com.udacity.gradle.builditbigger.util;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.jokeandroidlibrary.Joke;
import com.udacity.gradle.builditbigger.jokeandroidlibrary.JokeActivity;

import at.grabner.circleprogress.AnimationState;
import at.grabner.circleprogress.AnimationStateChangedListener;
import at.grabner.circleprogress.CircleProgressView;
import at.grabner.circleprogress.TextMode;


@SuppressLint("Registered")
public class AppCompatCircleViewActivityBase
       extends AppCompatActivity
       implements AsyncJokeTaskInterface
{
    private final static String TAG = "LEE: <" + AppCompatCircleViewActivityBase.class.getSimpleName() + ">";

    private Handler mHandler;
    private CircleProgressView mCircleView;
    private boolean mShowUnit;
    private Joke theJoke;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        mHandler = new Handler();
        mShowUnit = false;
    }

    public void showCircleView() {
        Log.v(TAG, "showCircleView");

        mCircleView = (CircleProgressView) findViewById(R.id.circle_view);
        if (mCircleView == null) {
            Log.w(TAG, "showCircleView: CircleProgressView problem - mCircleView is null!");
            return;
        }
        Log.v(TAG, "showCircleView: mCircleView="+mCircleView);
        initializeCircleViewMaxValue(3);

        mCircleView.post(new Runnable() {
            @Override
            public void run() {

                mShowUnit = false;
                Log.w(TAG, "showCircleView");
                mCircleView.setAutoTextSize(true); // enable auto text size, previous values are overwritten
                mCircleView.setUnitScale(0.9f); // if you want the calculated text sizes to be bigger/smaller
                mCircleView.setTextScale(0.9f); // if you want the calculated text sizes to be bigger/smaller
                mCircleView.setTextColor(Color.RED);
                mCircleView.setText("Loading.."); //shows the given text in the circle view
                mCircleView.setTextMode(TextMode.TEXT); // Set text mode to text to show text
                mCircleView.spin(); // start spinning
                mCircleView.setShowTextWhileSpinning(true); // Show/hide text in spinning mode

                mCircleView.setAnimationStateChangedListener(

                        new AnimationStateChangedListener() {
                            @Override
                            public void onAnimationStateChanged(AnimationState _animationState) {
                                if (mCircleView != null) {
                                    switch (_animationState) {
                                        case IDLE:
                                        case ANIMATING:
                                        case START_ANIMATING_AFTER_SPINNING:
                                            mCircleView.setTextMode(TextMode.PERCENT); // show percent if not spinning
                                            mCircleView.setShowUnit(mShowUnit);
                                            break;
                                        case SPINNING:
                                            mCircleView.setTextMode(TextMode.TEXT); // show text while spinning
                                            mCircleView.setShowUnit(false);
                                        case END_SPINNING:
                                            break;
                                        case END_SPINNING_START_ANIMATING:
                                            break;

                                    }
                                }
                            }
                        }

                );

                mCircleView.setVisibility(View.VISIBLE);
            }
        });

    }

    private void initializeCircleViewMaxValue(@SuppressWarnings("SameParameterValue") final float maxValue) {
        if (mCircleView != null) {
            Log.w(TAG, "initializeCircleViewMaxValue: CircleView MAX=" + maxValue);

            mCircleView.post(new Runnable() {
                @Override
                public void run() {
                    mShowUnit = true;
                    mCircleView.setVisibility(View.INVISIBLE);
                    mCircleView.setValue(0);
                    mCircleView.setUnit("%");
                    mCircleView.setShowUnit(mShowUnit);
                    mCircleView.setTextMode(TextMode.PERCENT); // Shows current percent of the current value from the max value
                    mCircleView.setMaxValue(maxValue);
                }
            });

        }
    }

    public void setCircleViewValue(final float value) {
        if (mCircleView != null) {
            Log.w(TAG, "setCircleViewValue: CircleView value=" + value);

            mCircleView.post(new Runnable() {
                @Override
                public void run() {
                    mCircleView.setValue(value);
                }
            });

        }
    }

    public void passJokeFromJavaLibraryToAndroidLibrary(final Joke aJoke) {
        Log.w(TAG, "passJokeFromJavaLibraryToAndroidLibrary");
        theJoke = aJoke;
        if (mCircleView != null) {
            Log.w(TAG, "passJokeFromJavaLibraryToAndroidLibrary: CircleView HIDDEN");
            final int mDelay = 1000; // post with 1 second delay for visual effect

            final AppCompatCircleViewActivityBaseInterface superActivity = (AppCompatCircleViewActivityBaseInterface) this;
            getHandler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Log.w(TAG, "===> makeVisible <===");
                    superActivity.makeVisible(); // only needed for BACKPRESS to work from the abstracted android library
                                                 // an alternate implementation might refactor so the CircleView is be inside a transition activity.. this is easier for now.
                }
            }, mDelay * 3);

            final String from = superActivity.getClass().getCanonicalName();
            final Activity activity = this;
            mCircleView.postDelayed(new Runnable() {
                @Override
                public void run() {
                    // can be null with unit tests
                    if (mCircleView != null) {
                        mCircleView.stopSpinning();
                        mCircleView.setVisibility(View.INVISIBLE);
                    }

                    // let the progress view disappear for a moment before we display the joke text
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Log.v(TAG, "startActivity - JokeActivity.class - from=" + from);
                            Intent intent = new Intent();
                            intent.putExtra("JOKE", theJoke.getJoke()); // pass the Joke as an Intent extra
                            intent.putExtra("FROM", from); // pass the callback activity classname to enable "TELL ANOTHER JOKE" button in android library
                                                           // if this is not set (default case) then that button will not be visible.
                            intent.setClass(activity, JokeActivity.class);
                            startActivity(intent);
                        }
                    }, mDelay / 2);

                }
            }, mDelay / 2);

        }
    }

    @Override
    public void onDestroy() {
        Log.v(TAG, "onDestroy");
        super.onDestroy();
        mCircleView = null;
        mHandler = null;
    }

    @Override
    public Handler getHandler() {
        return mHandler;
    }

    @Override
    public Activity getActivity() {
        return this;
    }

    public Joke getTheJoke() {
        return theJoke;
    }

}
