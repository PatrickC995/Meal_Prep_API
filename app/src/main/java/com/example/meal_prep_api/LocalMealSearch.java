package com.example.meal_prep_api;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meal_prep_api.database.MealDatabase;
import com.example.meal_prep_api.database.MealEntity;

import java.util.List;

public class LocalMealSearch extends AppCompatActivity {

    private EditText searchInput;
    private Button searchButton;
    private Button exitButton;
    private TextView resultsText;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_meal_search);

        searchInput = findViewById(R.id.local_search_input);
        searchButton = findViewById(R.id.local_search_button);
        exitButton = findViewById(R.id.exit_button);
        resultsText = findViewById(R.id.local_results_text);

        exitButton.setOnClickListener(v -> finish());

        if (savedInstanceState != null) {
            resultsText.setText(
                    savedInstanceState.getString("localResults", "")
            );
        }

        searchButton.setOnClickListener(v -> searchLocalMeals());
    }

    private void searchLocalMeals() {

        String search = searchInput.getText().toString().trim();

        if (search.isEmpty()) {
            searchInput.setError("Enter a meal or ingredient");
            resultsText.setText("");
            return;
        }

        searchButton.setEnabled(false);
        resultsText.setText("Searching...");

        // Room queries must run away from the main UI thread.
        // https://developer.android.com/training/data-storage/room/async-queries
        new Thread(() -> {

            try {
                MealDatabase database =
                        MealDatabase.getInstance(getApplicationContext());

                List<MealEntity> meals =
                        database.mealDao().searchMeals(search);

                runOnUiThread(() -> {
                    displayMeals(meals);
                    searchButton.setEnabled(true);
                });

            } catch (Exception e) {
                runOnUiThread(() -> {
                    resultsText.setText("Unable to search saved meals.");
                    searchButton.setEnabled(true);
                });
            }

        }).start();
    }

    private void displayMeals(List<MealEntity> meals) {

        if (meals.isEmpty()) {
            resultsText.setText("No matching meals found.");
            return;
        }

        StringBuilder details = new StringBuilder();

        details.append("Matching meals: ")
                .append(meals.size())
                .append("\n\n");

        for (MealEntity meal : meals) {

            details.append("Meal: ").append(meal.meal).append("\n");
            details.append("Category: ").append(meal.category).append("\n");
            details.append("Area: ").append(meal.area).append("\n");

            details.append("MealThumb: ")
                    .append(meal.mealThumb).append("\n");

            details.append("Tags: ")
                    .append(meal.tags == null ? "None" : meal.tags)
                    .append("\n");

            details.append("YouTube: ")
                    .append(meal.youtube).append("\n");

            details.append("Ingredient 1: ")
                    .append(meal.ingredient1).append("\n");

            details.append("Ingredient 2: ")
                    .append(meal.ingredient2).append("\n");

            details.append("Ingredient 3: ")
                    .append(meal.ingredient3).append("\n");

            details.append("Ingredient 4: ")
                    .append(meal.ingredient4).append("\n");

            details.append("\n--------------------\n\n");
        }

        resultsText.setText(details.toString());
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString(
                "localResults",
                resultsText.getText().toString()
        );
    }
}