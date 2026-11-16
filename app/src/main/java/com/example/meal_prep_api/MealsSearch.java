package com.example.meal_prep_api;

import android.annotation.SuppressLint;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import com.example.meal_prep_api.database.MealDatabase;
import com.example.meal_prep_api.database.MealEntity;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

public class MealsSearch extends AppCompatActivity {

    private EditText mealNameInput;
    private Button searchButton;
    private TextView statusText;
    private TextView mealDetailsText;
    private ImageView mealImage;

    private RequestQueue requestQueue;
    private MealDatabase database;

    @SuppressLint("MissingInflatedId")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals_search);

        mealNameInput = findViewById(R.id.meal_name_input);
        searchButton = findViewById(R.id.search_button);
        statusText = findViewById(R.id.status_text);
        mealDetailsText = findViewById(R.id.meal_details_text);
        mealImage = findViewById(R.id.meal_image);

        requestQueue = Volley.newRequestQueue(
                getApplicationContext()
        );

        database = MealDatabase.getInstance(
                getApplicationContext()
        );

        searchButton.setOnClickListener(v -> searchMeal());
    }

    private void searchMeal() {

        String enteredName =
                mealNameInput.getText().toString().trim();

        // Remove the previous result.
        requestQueue.cancelAll("mealSearch");
        mealDetailsText.setText("");
        mealImage.setImageDrawable(null);
        mealImage.setVisibility(View.GONE);
        statusText.setText("");

        // Reject an empty input.
        if (enteredName.isEmpty()) {
            mealNameInput.setError("Enter a meal name");
            return;
        }

        mealNameInput.setError(null);
        searchButton.setEnabled(false);
        statusText.setText("Searching...");

        String url =
                "https://www.themealdb.com/api/json/v1/1/search.php?s="
                        + Uri.encode(enteredName);

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,

                response -> {
                    try {
                        JSONArray meals =
                                response.optJSONArray("meals");

                        // The API found no results.
                        if (meals == null || meals.length() == 0) {
                            statusText.setText(
                                    "No exact match found. Enter the full meal name."
                            );
                            searchButton.setEnabled(true);
                            return;
                        }

                        // Check every returned meal for an exact match.
                        for (int i = 0; i < meals.length(); i++) {

                            JSONObject meal = meals.getJSONObject(i);

                            String returnedName =
                                    getText(meal, "strMeal");

                            if (returnedName.equalsIgnoreCase(enteredName)) {

                                MealEntity foundMeal = createMeal(meal);

                                displayMeal(foundMeal);
                                saveMeal(foundMeal);
                                return;
                            }
                        }

                        // Results existed, but none matched exactly.
                        statusText.setText(
                                "No exact match found. Enter the full meal name."
                        );
                        searchButton.setEnabled(true);

                    } catch (JSONException e) {
                        statusText.setText(
                                "The meal response could not be read."
                        );
                        searchButton.setEnabled(true);
                    }
                },

                error -> {
                    statusText.setText(
                            "Unable to retrieve meals. Check your internet connection and try again."
                    );
                    searchButton.setEnabled(true);
                }
        );

        request.setTag("mealSearch");
        requestQueue.add(request);
    }

    private MealEntity createMeal(JSONObject meal)
            throws JSONException {

        return new MealEntity(
                meal.getInt("idMeal"),
                getText(meal, "strMeal"),
                getText(meal, "strCategory"),
                getText(meal, "strArea"),
                getText(meal, "strMealThumb"),
                getText(meal, "strTags"),
                getText(meal, "strYoutube"),
                getText(meal, "strIngredient1"),
                getText(meal, "strIngredient2"),
                getText(meal, "strIngredient3"),
                getText(meal, "strIngredient4")
        );
    }

    private void displayMeal(MealEntity meal) {

        String details =
                "Meal: " + meal.meal
                        + "\n\nCategory: " + displayValue(meal.category)
                        + "\n\nArea: " + displayValue(meal.area)
                        + "\n\nTags: " + displayValue(meal.tags)
                        + "\n\nYouTube: " + displayValue(meal.youtube)
                        + "\n\nIngredients:"
                        + "\n1. " + displayValue(meal.ingredient1)
                        + "\n2. " + displayValue(meal.ingredient2)
                        + "\n3. " + displayValue(meal.ingredient3)
                        + "\n4. " + displayValue(meal.ingredient4);

        mealDetailsText.setText(details);
        mealImage.setContentDescription(meal.meal);

        if (!meal.mealThumb.isEmpty()) {
            loadMealImage(meal.mealThumb);
        }
    }

    private void loadMealImage(String imageUrl) {

        ImageRequest imageRequest = new ImageRequest(
                imageUrl,

                bitmap -> {
                    mealImage.setImageBitmap(bitmap);
                    mealImage.setVisibility(View.VISIBLE);
                },

                600,
                600,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.RGB_565,
                error -> Toast.makeText(
                        MealsSearch.this,
                        "The meal image could not be loaded.",
                        Toast.LENGTH_SHORT
                ).show()
        );

        imageRequest.setTag("mealSearch");
        requestQueue.add(imageRequest);
    }

    private void saveMeal(MealEntity meal) {

        statusText.setText("Meal found. Saving...");

        MealDatabase.databaseExecutor.execute(() -> {

            try {
                database.mealDao().insertMeals(meal);

                runOnUiThread(() -> {
                    statusText.setText(
                            "Meal found and saved to the database."
                    );
                    searchButton.setEnabled(true);
                });

            } catch (Exception e) {

                runOnUiThread(() -> {
                    statusText.setText(
                            "Meal found, but it could not be saved."
                    );
                    searchButton.setEnabled(true);
                });
            }
        });
    }

    // Convert missing or null API values into empty strings.
    private String getText(JSONObject object, String key) {

        if (object.isNull(key)) {
            return "";
        }

        return object.optString(key, "").trim();
    }

    // Display a readable message for empty optional details.
    private String displayValue(String value) {

        if (value == null || value.trim().isEmpty()) {
            return "Not provided";
        }

        return value;
    }

    @Override
    protected void onDestroy() {
        requestQueue.cancelAll("mealSearch");
        super.onDestroy();
    }
}