package com.udacity.gradle.builditbigger.jokeandroidlibrary;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.udacity.gradle.builditbigger.jokeandroidlibrary.R;


public class JokeActivity extends AppCompatActivity {
    private final static String TAG = "LEE: <" + JokeActivity.class.getSimpleName() + ">";

    private TextView mJokeView;
    private Button mTellAnotherJokeButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.v(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        Log.v(TAG, "onCreate - setContentView(activity_joke)");
        setContentView(R.layout.activity_joke);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        Intent theIntent = getIntent();

        mTellAnotherJokeButton = (Button) findViewById(R.id.another_joke);
        mJokeView = (TextView) findViewById(R.id.joke_text);

        if (savedInstanceState != null) {
            // the application is being reloaded
            Log.v(TAG, "load joke from the savedInstanceState..");
            onRestoreInstanceState(savedInstanceState);
        }
        else {
            String jokeText = theIntent.getStringExtra("JOKE");
            if (jokeText == null) {
                jokeText = getResources().getString(R.string.no_joke);
            }
            Log.v(TAG, "jokeText="+jokeText);
            mJokeView.setText(jokeText);
        }

        // check if we should enable the "TELL ANOTHER JOKE" button.. only do so if 'FROM' is set
        String from = theIntent.getStringExtra("FROM");
        if (from != null) {
            Log.v(TAG, "ok to TELL ANOTHER JOKE - name of the invoking class: FROM=" + from);
            mTellAnotherJokeButton.setVisibility(View.VISIBLE);
        }
        else {
            Log.v(TAG, "missing name of the invoking class - TELL ANOTHER BUTON IS INVISIBLE");
            mTellAnotherJokeButton.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    protected void onRestoreInstanceState(Bundle savedInstanceState) {
        super.onRestoreInstanceState(savedInstanceState);
        String joke = savedInstanceState.getString("JOKE");
        Log.v(TAG, "onRestoreInstanceState - joke=" + joke);
        mJokeView.setText(joke);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        Log.v(TAG, "onSaveInstanceState");
        super.onSaveInstanceState(outState);
        outState.putString("JOKE", mJokeView.getText().toString());
    }

    @SuppressWarnings("unused")
    public void tellAnotherJoke(@SuppressWarnings("UnusedParameters") View view){
        Log.v(TAG, "tellAnotherJoke");
        mJokeView.setText(getResources().getString(R.string.empty));
        mTellAnotherJokeButton.setVisibility(View.INVISIBLE);
        // back to the class specified by "FROM" intent extra (if it exists)
        Intent theIntent = getIntent();
        String fromClass = theIntent.getStringExtra("FROM");
        if (fromClass != null) {
            try {
                Log.v(TAG, "STARTACTIVITY - CLASS 'FROM'=" + fromClass);
                Class<?> backToClass = Class.forName(fromClass );
                Intent tellAnotherIntent = new Intent(this, backToClass);
                tellAnotherIntent.putExtra("FROM", JokeActivity.class.getCanonicalName());
                tellAnotherIntent.putExtra("TELL_ANOTHER_JOKE", "true");
                tellAnotherIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                startActivity(tellAnotherIntent);
            }
            catch (ClassNotFoundException e) {
                Log.e(TAG, "*** UNABLE TO LOCATE CLASS 'FROM'=" + fromClass);
            }
        }
        else {
            Log.v(TAG, "unable to tell another joke - 'FROM' missing from intent");
        }
    }

}
