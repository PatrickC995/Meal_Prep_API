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
        searchButton.setOnClickListener(view -> searchMeals());
    }

    private void searchMeals() {
        String query = titleInput.getText().toString().trim();

        if (query.isEmpty()) {
            titleInput.setError("Enter part of a meal title");
            return;
        }

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
                response -> displayTitles(response.optJSONArray("meals")),
                error -> Toast.makeText(this, "Could not search TheMealDB",
                        Toast.LENGTH_SHORT).show()
        );

        request.setTag("mealSearch");
        request.setShouldCache(false);
        requestQueue.add(request);
    }

    private void displayTitles(JSONArray meals) {
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
    protected void onDestroy() {
        requestQueue.cancelAll("mealSearch");
        requestQueue.cancelAll("mealImage");
        requestQueue.stop();
        super.onDestroy();
    }
}