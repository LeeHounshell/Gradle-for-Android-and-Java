package com.udacity.gradle.builditbigger;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import com.udacity.gradle.builditbigger.jokeandroidlibrary.Joke;
import com.udacity.gradle.builditbigger.util.PerformAsyncJokeTask;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.util.Timer;
import java.util.TimerTask;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static junit.framework.Assert.assertFalse;
import static junit.framework.Assert.assertNotNull;
import static junit.framework.Assert.assertTrue;
import static org.junit.Assert.assertNotEquals;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class PerformAsyncJokeTaskTest {
    private final static String TAG = "LEE: <" + PerformAsyncJokeTaskTest.class.getSimpleName() + ">";

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    private final Timer timer = new Timer();
    private final Object lock = new Object();

    // test of jokeBackend request with a known deep-link joke passed in and validated on display
    @Test
    public void deepLinkJokeTest() {
        Log.v(TAG, "deepLinkJokeTest");

        String deepLinkJoke = "Roses are red, Violets are blue. Most poems rhyme, but this one doesn't.";
        String JOKE_API_KEY = main.getActivity().getResources().getString(R.string.joke_api_key);

        PerformAsyncJokeTask loadJokeTask = new PerformAsyncJokeTask(main.getActivity(), main.getActivity().findViewById(R.id.circle_view));
        int seconds = 20; // plenty of time to finish when using a deep-link provided joke
        timer.schedule(new VerifyJokeReceived(), seconds * 1000);
        loadJokeTask.execute(JOKE_API_KEY, deepLinkJoke);

        boolean success = false;
        try {
            Log.v(TAG, "WAIT FOR NOTIFY OR TIMEOUT");
            synchronized (lock) {
                lock.wait(seconds * 1000);
            }
            Log.v(TAG, "DONE WAITING");
            success = true;
        }
        catch (InterruptedException e) {
            Log.e(TAG, "UNABLE TO GET JOKE: e="+e);
        }
        assertTrue(success);

        onView(withId(R.id.joke_text)).check(matches(isDisplayed()))
                .check(matches(ViewMatchers.withText(deepLinkJoke)));
    }

    // test of jokeBackend request and wait then validation of receipt of a valid joke after 30 seconds
    // this test requires the jokeBackend to be running - see settings to access the jokeBackend in strings.xml
    @Test
    public void retrieveJokeFromJokeBackendTest() {
        Log.v(TAG, "retrieveJokeFromJokeBackendTest");

        String JOKE_API_KEY = main.getActivity().getResources().getString(R.string.joke_api_key);

        PerformAsyncJokeTask loadJokeTask = new PerformAsyncJokeTask(main.getActivity(), main.getActivity().findViewById(R.id.circle_view));
        int seconds = 40; // lots of time to finish network request to jokeBackend
        timer.schedule(new VerifyJokeReceived(), seconds * 1000);
        loadJokeTask.execute(JOKE_API_KEY, null);

        boolean success = false;
        try {
            Log.v(TAG, "WAIT FOR NOTIFY OR TIMEOUT");
            synchronized (lock) {
                lock.wait(seconds * 1000);
            }
            Log.v(TAG, "DONE WAITING");
            success = true;
        }
        catch (InterruptedException e) {
            Log.e(TAG, "UNABLE TO GET JOKE: e="+e);
        }
        assertTrue(success);
    }

    class VerifyJokeReceived extends TimerTask {
        public void run() {
            Log.v(TAG, "VerifyJokeReceived.run()");
            synchronized (lock) {
                timer.cancel();
                Joke joke = main.getActivity().getTheJoke();
                assertNotNull(joke);
                String jokeText = joke.getJoke();
                assertNotNull(jokeText);
                String no_joke = main.getActivity().getResources().getString(R.string.no_joke);
                assertNotEquals(jokeText, no_joke);
                assertFalse(jokeText.equals(""));
                Log.v(TAG, "jokeText=" + jokeText);
                System.out.println("=========> JOKE: " + jokeText);
                lock.notify();
            }
        }
    }

}

