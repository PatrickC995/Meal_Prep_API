package com.example.meal_prep_api;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meal_prep_api.database.MealDatabase;
import com.example.meal_prep_api.database.MealEntity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button openMealSearchButton =
                findViewById(R.id.open_meal_search_button);

        openMealSearchButton.setOnClickListener(v -> {

            Intent intent = new Intent(
                    MainActivity.this,
                    MealsSearch.class
            );

            startActivity(intent);
        });

        Button localSearchButton =
                findViewById(R.id.open_local_search_button);

        localSearchButton.setOnClickListener(v -> {
            Intent intent = new Intent(
                    MainActivity.this,
                    LocalMealSearch.class
            );

            startActivity(intent);
        });

        findViewById(R.id.open_meal_title_search_button).setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, MealTitleSearch.class);
            startActivity(intent);
        });

        MealDatabase.databaseExecutor.execute(() -> {

            MealDatabase database =
                    MealDatabase.getInstance(getApplicationContext());

            List<MealEntity> meals =
                    database.mealDao().getAllMeals();

            Log.d("MealDatabase", "Number of meals: " + meals.size());

            for (MealEntity meal : meals) {
                Log.d(
                        "MealDatabase",
                        meal.id + ": " + meal.meal +
                                " | Category: " + meal.category +
                                " | Area: " + meal.area +
                                " | Tags: " + meal.tags
                );
            }
        });
    }
}