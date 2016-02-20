package com.udacity.gradle.builditbigger;

//---------------------------------------------------------------------------------------------------------
// IMPORTANT NOTE: the inline #IFDEF and #ENDIF directives are processed in build.gradle prior to javac
// CODE IN BLOCKS WITH THESE DIRECTIVES DEPENDS ON PRODUCT 'FLAVOR' AND IS DYNAMICALLY EDITED BY GRADLE
//---------------------------------------------------------------------------------------------------------

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

//#IFDEF 'free'
//import android.widget.ImageView;
//import com.google.android.gms.ads.AdView;
//#ENDIF

import com.udacity.gradle.builditbigger.util.AppCompatCircleViewActivityBase;
import com.udacity.gradle.builditbigger.util.AppCompatCircleViewActivityBaseInterface;
import com.udacity.gradle.builditbigger.util.CheckPlayStore;


public class MainActivity
        extends AppCompatCircleViewActivityBase
        implements AppCompatCircleViewActivityBaseInterface
{
    private final static String TAG = "LEE: <" + MainActivity.class.getSimpleName() + ">";

    private TextView mInstructions;
    private Button mTellJokeButton;
    private boolean mWithInterstitialAd = false;
    private Menu mMenu;
    private Intent mIntent;

    //#IFDEF 'free'
    //private ImageView mUpgradePaid;
    //private AdView mAdView;
    //#ENDIF

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate - setContentView(activity_main)");
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        mInstructions = (TextView) findViewById(R.id.instructions_text_view);
        mTellJokeButton = (Button) findViewById(R.id.tell_joke_button);

        //#IFDEF 'free'
        //mUpgradePaid = (ImageView) findViewById(R.id.upgrade_to_paid);
        //mAdView = (AdView) findViewById(R.id.adView);
        //mWithInterstitialAd = true;
        //#ENDIF

        CheckPlayStore.upgradeToPaid(this);

        mIntent = getIntent();
        String action = mIntent.getAction();
        String data = mIntent.getDataString();
        Log.v(TAG, "*** action="+action+", data="+data+" ***"); // DEEP LINK: action=android.intent.action.VIEW, data=harlie://joke
                                                                // FROM MAIN: action=android.intent.action.MAIN, data=null
        if (data != null && action.equals("android.intent.action.VIEW")) {
            Uri dataUri = mIntent.getData();
            String deepJoke = dataUri.getQueryParameter("joke");
            if (deepJoke != null) {
                Log.v(TAG, "*** JOKE DEEP-LINK MESSAGE: "+deepJoke);
                makeInvisible();
                tellJoke(deepJoke);
                return;
            }
        }
        setVisibility();
    }

    private boolean isTellingAnotherJoke() {
        Log.v(TAG, "isTellingAnotherJoke");
        if (mIntent == null) {
            return false;
        }
        String anotherJoke = mIntent.getStringExtra("TELL_ANOTHER_JOKE");
        return (anotherJoke != null && anotherJoke.equalsIgnoreCase("true"));
    }

    private void setVisibility() {
        // check if the "TELL ANOTHER JOKE" button was pressed..
        if (isTellingAnotherJoke()) {
            Log.v(TAG, "we need to TELL_ANOTHER_JOKE");
            makeInvisible();
            tellJoke((View) null);
        } else {
            Log.v(TAG, "we need to prompt TELL_JOKE");
            makeVisible();
        }
    }

    public void makeVisible() {
        Log.v(TAG, "makeVisible");
        mInstructions.setVisibility(View.VISIBLE);
        mTellJokeButton.setVisibility(View.VISIBLE);

        //#IFDEF 'free'
        //mAdView.setVisibility(View.VISIBLE);
        //mUpgradePaid.setVisibility(View.VISIBLE);
        //#ENDIF

        if (mMenu != null) {
            mMenu.getItem(0).setVisible(true);
        }
    }

    @SuppressWarnings("WeakerAccess")
    public void makeInvisible() {
        Log.v(TAG, "makeInvisible");
        mInstructions.setVisibility(View.INVISIBLE);
        mTellJokeButton.setVisibility(View.INVISIBLE);

        //#IFDEF 'free'
        //mAdView.setVisibility(View.INVISIBLE);
        //mUpgradePaid.setVisibility(View.INVISIBLE);
        //#ENDIF

        if (mMenu != null) {
            mMenu.getItem(0).setVisible(false);
        }
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        super.onPause();
    }

    @Override
    protected void onResume() {
        Log.v(TAG, "onResume");
        super.onResume();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        Log.v(TAG, "onCreateOptionsMenu");
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        mMenu = menu; // save so we can make the settings icon invisible during transitions
        if (isTellingAnotherJoke()) {
            // we don't want to show the settings icon
            mMenu.getItem(0).setVisible(false);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        Log.v(TAG, "onOptionsItemSelected");
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            Intent i = new Intent(this, SettingsActivity.class);
            startActivity(i);
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("WeakerAccess")
    public void tellJoke(@SuppressWarnings("UnusedParameters") View view) {
        Log.v(TAG, "retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt");
        makeInvisible();
        MainActivityFragment currentFragment = MainActivityFragment.getMainActivityFragment();
        currentFragment.retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt(mWithInterstitialAd, null);
    }

    @SuppressWarnings("WeakerAccess")
    public void tellJoke(String deepJoke) {
        Log.v(TAG, "retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt: deepJoke="+deepJoke);
        makeInvisible();
        MainActivityFragment currentFragment = MainActivityFragment.getMainActivityFragment();
        currentFragment.retrieveJokeThenLaunchJokeAndroidLibraryToDisplayIt(mWithInterstitialAd, deepJoke);
    }

    @Override
    public void onStop() {
        Log.v(TAG, "onStop");
        super.onStop();
    }

}
