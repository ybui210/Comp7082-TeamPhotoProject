package com.example.photogallery;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.action.ViewActions.typeText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;

import androidx.test.ext.junit.rules.ActivityScenarioRule;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

@RunWith(AndroidJUnit4.class)
public class TestPhotoGallery {
    @Rule
    public ActivityScenarioRule<MainActivity> activityActivityScenarioRule =
            new ActivityScenarioRule<MainActivity>(MainActivity.class);

    @Test
    public void secondTestApp(){
        onView(withId(R.id.btnSearch)).perform(click());
        onView(withId(R.id.etFromDateTime)).perform(replaceText("2021‐09‐01 00:00:00"), closeSoftKeyboard());
        onView(withId(R.id.etToDateTime)).perform(typeText(""), closeSoftKeyboard());
        onView(withId(R.id.etKeywords)).perform(typeText("caption"), closeSoftKeyboard());
        onView(withId(R.id.go)).perform(click());
        onView(withId(R.id.etCaption)).check(matches(withText("caption")));
        onView(withId(R.id.btnNext)).perform(click());
        onView(withId(R.id.btnPrev)).perform(click());
    }
}
