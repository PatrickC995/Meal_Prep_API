package com.example.meal_prep_api;

import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.ImageRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;
import org.json.JSONObject;

public class MealTitleSearch extends AppCompatActivity {

    private static final String SEARCH_URL =
            "https://www.themealdb.com/api/json/v1/1/search.php";

    private EditText titleInput;
    private LinearLayout titleResults;
    private ImageView mealImage;
    private RequestQueue requestQueue;
    private TextView statusText;
    private JSONArray displayedMeals = new JSONArray();
    private String selectedTitle = "";
    private String selectedImageUrl = "";
    private String pendingQuery = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meal_title_search);

        titleInput = findViewById(R.id.meal_title_input);
        titleResults = findViewById(R.id.meal_title_results);
        mealImage = findViewById(R.id.selected_meal_image);
        statusText = findViewById(R.id.status_text);
        requestQueue = Volley.newRequestQueue(getApplicationContext());

        Button exitButton = findViewById(R.id.exit_button);
        exitButton.setOnClickListener(v -> finish());

        Button searchButton = findViewById(R.id.search_meal_titles_button);
        searchButton.setOnClickListener(view ->
                searchMeals(titleInput.getText().toString().trim())
        );

        if (savedInstanceState != null) {
            String query = savedInstanceState.getString("pendingQuery", "");

            if (!query.isEmpty()) {
                searchMeals(query);
            } else {
                try {
                    String titles = savedInstanceState.getString("titles", "[]");
                    displayTitles(new JSONArray(titles));
                    statusText.setText(savedInstanceState.getString("titleStatus", ""));
                } catch (org.json.JSONException e) {
                    statusText.setText("Please search for the meal again.");
                }

                String title = savedInstanceState.getString("selectedTitle", "");
                String url = savedInstanceState.getString("selectedImageUrl", "");

                if (!url.isEmpty()) {
                    showMealImage(title, url);
                }
            }
        }
    }

    private void searchMeals(String query) {

        if (query.isEmpty()) {
            titleInput.setError("Enter part of a meal title");
            return;
        }
        pendingQuery = query;
        displayedMeals = new JSONArray();
        selectedTitle = "";
        selectedImageUrl = "";

        requestQueue.cancelAll("mealSearch");
        requestQueue.cancelAll("mealImage");
        titleResults.removeAllViews();
        statusText.setText("");
        mealImage.setImageDrawable(null);
        mealImage.setVisibility(View.GONE);

        String url = Uri.parse(SEARCH_URL).buildUpon()
                .appendQueryParameter("s", query)
                .build()
                .toString();

        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                url,
                null,
                response -> {
                    pendingQuery = "";
                    displayTitles(response.optJSONArray("meals"));
                },
                error -> {
                    pendingQuery = "";
                    statusText.setText(
                            "Could not search TheMealDB. Check your internet connection and try again."
                    );
                }
        );

        request.setTag("mealSearch");
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void displayTitles(JSONArray meals) {
        displayedMeals = new JSONArray();
        if (meals == null || meals.length() == 0) {
            statusText.setText("No matching meals found. Try a different part of the meal title.");
            return;
        }

        int numberToShow = Math.min(5, meals.length());

        for (int i = 0; i < numberToShow; i++) {
            JSONObject meal = meals.optJSONObject(i);
            if (meal == null) {
                continue;
            }

            displayedMeals.put(meal);

            String title = meal.optString("strMeal");
            String imageUrl = meal.optString("strMealThumb");

            Button titleButton = new Button(this);
            titleButton.setText(title);
            titleButton.setAllCaps(false);
            titleButton.setOnClickListener(view -> showMealImage(title, imageUrl));
            titleResults.addView(titleButton);
        }
    }

    private void showMealImage(String title, String imageUrl) {
        selectedTitle = title;
        selectedImageUrl = imageUrl;
        requestQueue.cancelAll("mealImage");
        mealImage.setImageDrawable(null);
        mealImage.setVisibility(View.GONE);

        ImageRequest request = new ImageRequest(
                imageUrl,
                bitmap -> {
                    mealImage.setImageBitmap(bitmap);
                    mealImage.setContentDescription(title);
                    mealImage.setVisibility(View.VISIBLE);
                },
                800,
                800,
                ImageView.ScaleType.FIT_CENTER,
                Bitmap.Config.RGB_565,
                error -> Toast.makeText(this, "Could not load the meal image",
                        Toast.LENGTH_SHORT).show()
        );

        request.setTag("mealImage");
        requestQueue.add(request);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);

        outState.putString("titles", displayedMeals.toString());
        outState.putString("selectedTitle", selectedTitle);
        outState.putString("selectedImageUrl", selectedImageUrl);
        outState.putString("titleStatus", statusText.getText().toString());
        outState.putString("pendingQuery", pendingQuery);
    }

    @Override
    protected void onDestroy() {
        requestQueue.cancelAll("mealSearch");
        requestQueue.cancelAll("mealImage");
        requestQueue.stop();
        super.onDestroy();
    }
}