package com.example.meal_prep_api;

import androidx.test.core.app.ActivityScenario;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import org.junit.Test;
import org.junit.runner.RunWith;

import static androidx.test.espresso.Espresso.onView;
import static androidx.test.espresso.action.ViewActions.click;
import static androidx.test.espresso.action.ViewActions.closeSoftKeyboard;
import static androidx.test.espresso.action.ViewActions.replaceText;
import static androidx.test.espresso.matcher.ViewMatchers.hasErrorText;
import static androidx.test.espresso.matcher.ViewMatchers.withId;
import static androidx.test.espresso.matcher.ViewMatchers.withText;
import static androidx.test.espresso.assertion.ViewAssertions.matches;
import static org.hamcrest.Matchers.containsString;

import android.os.SystemClock;
import android.widget.TextView;
import java.util.concurrent.atomic.AtomicReference;

import static org.junit.Assert.fail;

@RunWith(AndroidJUnit4.class)
public class MealsTests {

    @Test
    public void blankMealNameShowsError() {
        try (ActivityScenario<MealsSearch> screen =
                     ActivityScenario.launch(MealsSearch.class)) {

            onView(withId(R.id.meal_name_input))
                    .perform(replaceText("   "), closeSoftKeyboard());

            onView(withId(R.id.search_button)).perform(click());

            onView(withId(R.id.meal_name_input))
                    .check(matches(hasErrorText("Enter a meal name")));
        }
    }

    @Test
    public void mixedCaseIngredientResultSurvivesRecreation() {
        try (ActivityScenario<LocalMealSearch> screen =
                     ActivityScenario.launch(LocalMealSearch.class)) {

            searchLocal("mUsHrOoM");
            waitForLocalResult(screen, "Chicken Marengo");

            screen.recreate();

            onView(withId(R.id.local_search_input))
                    .check(matches(withText("mUsHrOoM")));

            onView(withId(R.id.local_results_text))
                    .check(matches(withText(containsString("Chicken Marengo"))));
        }
    }

    @Test
    public void unknownIngredientShowsNoMatches() {
        try (ActivityScenario<LocalMealSearch> screen =
                     ActivityScenario.launch(LocalMealSearch.class)) {

            searchLocal("zzzzzzzz");
            waitForLocalResult(screen, "No matching meals found.");
        }
    }

    private void searchLocal(String query) {
        onView(withId(R.id.local_search_input))
                .perform(replaceText(query), closeSoftKeyboard());

        onView(withId(R.id.local_search_button)).perform(click());
    }

    private void waitForLocalResult(ActivityScenario<LocalMealSearch> screen,
                                    String expected) {
        long deadline = SystemClock.elapsedRealtime() + 5000;
        AtomicReference<String> actual = new AtomicReference<>("");

        while (SystemClock.elapsedRealtime() < deadline) {
            screen.onActivity(activity -> {
                TextView results = activity.findViewById(R.id.local_results_text);
                actual.set(results.getText().toString());
            });

            if (actual.get().contains(expected)) {
                return;
            }

            SystemClock.sleep(50);
        }

        fail("Expected: " + expected + "\nActual: " + actual.get());
    }
}