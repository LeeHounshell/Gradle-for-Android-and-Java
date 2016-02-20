package com.udacity.gradle.builditbigger.jokeandroidlibrary;

import android.util.Log;


public class Joke {
    private final static String TAG = "LEE: <" + Joke.class.getSimpleName() + ">";

    @SuppressWarnings("CanBeFinal")
    private String mJoke;

    @SuppressWarnings("WeakerAccess")
    public Joke() {
        Log.v(TAG, "Joke");
        mJoke = "";
    }

    public Joke(String aJoke) {
        Log.v(TAG, "Joke");
        mJoke = aJoke;
    }

    public String getJoke() {
        Log.v(TAG, "getJoke");
        return mJoke;
    }

}
