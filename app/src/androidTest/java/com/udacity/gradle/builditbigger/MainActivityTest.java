package com.udacity.gradle.builditbigger;

import android.support.test.espresso.matcher.ViewMatchers;
import android.support.test.rule.ActivityTestRule;
import android.support.test.runner.AndroidJUnit4;
import android.test.suitebuilder.annotation.LargeTest;
import android.util.Log;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.assertion.ViewAssertions.matches;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withId;
import static android.support.test.espresso.matcher.ViewMatchers.withText;


@RunWith(AndroidJUnit4.class)
@LargeTest
public class MainActivityTest {
    private final static String TAG = "LEE: <" + MainActivityTest.class.getSimpleName() + ">";

    @Rule
    public final ActivityTestRule<MainActivity> main = new ActivityTestRule<>(MainActivity.class);

    @Test
    public void applicationMainActivityTest() {
        Log.v(TAG, "applicationMainActivityTest");
        String delicious = main.getActivity().getResources().getString(R.string.instructions);

        onView(withText(R.string.instructions)).check(matches(isDisplayed()))
                .check(matches(ViewMatchers.withText(delicious)));

        onView(withId(R.id.tell_joke_button)).check(matches(isDisplayed()));
    }

}
