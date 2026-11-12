package com.example.meal_prep_api;

import android.os.Bundle;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.meal_prep_api.database.MealDatabase;
import com.example.meal_prep_api.database.MealEntity;

import java.util.List;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

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