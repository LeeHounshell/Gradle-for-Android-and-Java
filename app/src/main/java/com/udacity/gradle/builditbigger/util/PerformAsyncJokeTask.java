package com.udacity.gradle.builditbigger.util;

import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Handler;
import android.os.SystemClock;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;

import com.google.api.client.extensions.android.http.AndroidHttp;
import com.google.api.client.extensions.android.json.AndroidJsonFactory;
import com.google.api.client.googleapis.services.AbstractGoogleClientRequest;
import com.google.api.client.googleapis.services.GoogleClientRequestInitializer;
import com.harlie.builditbigger.backend.myApi.MyApi;
import com.udacity.gradle.builditbigger.BuildItBiggerApplication;
import com.udacity.gradle.builditbigger.R;
import com.udacity.gradle.builditbigger.jokeandroidlibrary.Joke;

import java.io.IOException;


public class PerformAsyncJokeTask extends AsyncTask<String, Void, Joke> {
    private final static String TAG = "LEE: <" + PerformAsyncJokeTask.class.getSimpleName() + ">";

    private static MyApi myApiService = null;

    private final Handler mHandler;
    private final AsyncJokeTaskInterface mAsyncJokeTaskInterface;
    @SuppressWarnings("CanBeFinal")
    private String mJOKE_SERVER_PROTOCOL;
    @SuppressWarnings("CanBeFinal")
    private String mJOKE_SERVER_HOST;
    @SuppressWarnings("CanBeFinal")
    private String mJOKE_SERVER_PORT;
    private Joke mJoke;
    private final int mDelay = 1000; // post with 1 second delay for visual effect

    @SuppressWarnings("UnusedParameters")
    public PerformAsyncJokeTask(AsyncJokeTaskInterface theAsyncJokeTaskInterface, View view) {
        Log.v(TAG, "new PerformAsyncJokeTask");
        mJoke = null;
        mAsyncJokeTaskInterface = theAsyncJokeTaskInterface;
        mAsyncJokeTaskInterface.showCircleView();
        mHandler = mAsyncJokeTaskInterface.getHandler();

        // set the host or IP for the jokeServer
        //#IFDEF 'release'
        mJOKE_SERVER_PROTOCOL = mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.release_joke_server_protocol);
        mJOKE_SERVER_HOST = mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.release_joke_server_host);
        mJOKE_SERVER_PORT = mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.release_joke_server_port);
        //#ELSE
        //mJOKE_SERVER_PROTOCOL = mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.debug_joke_server_protocol);
        //mJOKE_SERVER_HOST = mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.debug_joke_server_host);
        //mJOKE_SERVER_PORT = mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.debug_joke_server_port);
        //#ENDIF
    }

    @Override
    protected void onPreExecute() {
        Log.v(TAG, "onPreExecute");
    }

    @Override
    protected Joke doInBackground(String... params) {
        Log.v(TAG, "doInBackground: params[0]="+params[0]+", params[1]="+params[1]);
        mJoke = new Joke(params[1]);
        Log.v(TAG, "DEEP-LINK: mJoke="+mJoke.getJoke());
        if (NetworkUtilities.isConnected(mAsyncJokeTaskInterface.getActivity())) {
            Log.v(TAG, "we should have Internet");
            if (mHandler != null) {

                SystemClock.sleep(mDelay);
                mHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        mAsyncJokeTaskInterface.setCircleViewValue(1);
                    }
                });

            }
            mJoke = loadJoke(params);
        }
        else {
            Log.w(TAG, "*** no Internet connection!");
            mJoke = new Joke(mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.no_network_joke));
        }
        Log.v(TAG, "*** doInBackground finished. - mJoke=" + mJoke);
        return mJoke;
    }

    private Joke loadJoke(@SuppressWarnings("UnusedParameters") String... params) {
        Log.v(TAG, "loadJoke");
        Joke joke;
        if (mJoke.getJoke() == null) {
            Log.v(TAG, "using BACKEND joke: mJOKE_SERVER_HOST="+mJOKE_SERVER_HOST);
            joke = new Joke(mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.no_joke));
            if (myApiService == null) {  // Only do this once
                MyApi.Builder builder = new MyApi.Builder(AndroidHttp.newCompatibleTransport(), new AndroidJsonFactory(), null)
                        // options for running against local devappserver
                        // - 10.0.2.2 is localhost's IP address in Android emulator - see 'joke_server_host' in strings.xml
                        // - turn off compression when running against local devappserver
                        // - for GCM deployment: .setRootUrl("https://android-app-backend.appspot.com/_ah/api/");
                        //                        where android-app-backend corresponds to the GCM Project ID
                        .setRootUrl(mJOKE_SERVER_PROTOCOL+"://"+mJOKE_SERVER_HOST+":"+mJOKE_SERVER_PORT+"/_ah/api/")
                        .setGoogleClientRequestInitializer(new GoogleClientRequestInitializer() {
                            @Override
                            public void initialize(AbstractGoogleClientRequest<?> abstractGoogleClientRequest) throws IOException {
                                abstractGoogleClientRequest.setDisableGZipContent(true);
                            }
                        });
                // end options for devappserver

                myApiService = builder.build();

                BuildItBiggerApplication.getInstance().trackEvent("init GCM", "setRootUrl", "setGoogleClientRequestInitializer");
            }

            try {
                String defaultJoke = joke.getJoke();
                SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(mAsyncJokeTaskInterface.getActivity());
                String firstname = prefs.getString("firstname", mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.pref_firstname));
                String lastname = prefs.getString("lastname", mAsyncJokeTaskInterface.getActivity().getResources().getString(R.string.pref_lastname));
                Log.i(TAG, "=========> loadJoke: INVOKE THE "+mJOKE_SERVER_PROTOCOL+" BACKEND ON HOST "+mJOKE_SERVER_HOST+" USING PORT "+mJOKE_SERVER_PORT);
                joke = new Joke(myApiService.getJoke(defaultJoke, firstname, lastname).execute().getData()); // get joke from jokeBackend server (which in turn uses the java library libjokeprovider)
                Log.i(TAG, "=========> loadJoke: RETURN FROM BACKEND - joke=" + joke.getJoke());
                BuildItBiggerApplication.getInstance().trackEvent("backend", "loadJoke", "SUCCESS");
            } catch (IOException e) {
                Log.e(TAG, "ERROR: " + e.getMessage());
                BuildItBiggerApplication.getInstance().trackException(e);
                BuildItBiggerApplication.getInstance().trackEvent("backend", "loadJoke", "FAIL");
            }
        }
        else {
            Log.v(TAG, "using DEEP-LINK joke");
            joke = new Joke(mJoke.getJoke());
        }

        if (mHandler != null) {

            SystemClock.sleep(mDelay);
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    mAsyncJokeTaskInterface.setCircleViewValue(2);
                }
            });

        }
        return joke;
    }

    @Override
    protected void onPostExecute(final Joke theJoke) {
        Log.v(TAG, "*** onPostExecute: theJoke=" + theJoke.getJoke());
        if (mHandler != null) {

            // progress bar will show 100% now..
            mHandler.postDelayed(new Runnable() {
                @Override
                public void run() {
                    mAsyncJokeTaskInterface.setCircleViewValue(3); // 100%

                    // let the progress view show 100% for a moment before we hide it and show the Joke..
                    mHandler.postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            Joke fixedJoke = new Joke( theJoke.getJoke().replaceAll("&amp;", "&").replaceAll("&quot;", "\"") );
                            // now pass the joke (from the Java Library via jokeBackend) to the Android Library
                            mAsyncJokeTaskInterface.passJokeFromJavaLibraryToAndroidLibrary(fixedJoke);
                        }
                    }, mDelay / 2);

                }
            }, mDelay / 2);

        }
    }

}
