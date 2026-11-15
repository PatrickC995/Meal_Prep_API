package com.example.meal_prep_api.database;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "meals")
public class MealEntity {

    @PrimaryKey
    public int id;
    @NonNull
    public String meal;
    @NonNull
    public String category;
    @NonNull
    public String area;
    @NonNull
    public String mealThumb;
    @Nullable
    public String tags;
    @NonNull
    public String youtube;
    @NonNull
    public String ingredient1;
    @NonNull
    public String ingredient2;
    @NonNull
    public String ingredient3;
    @NonNull
    public String ingredient4;

    public MealEntity(
            int id,
            @NonNull String meal,
            @NonNull String category,
            @NonNull String area,
            @NonNull String mealThumb,
            @Nullable String tags,
            @NonNull String youtube,
            @NonNull String ingredient1,
            @NonNull String ingredient2,
            @NonNull String ingredient3,
            @NonNull String ingredient4
    ) {
        this.id = id;
        this.meal = meal;
        this.category = category;
        this.area = area;
        this.mealThumb = mealThumb;
        this.tags = tags;
        this.youtube = youtube;
        this.ingredient1 = ingredient1;
        this.ingredient2 = ingredient2;
        this.ingredient3 = ingredient3;
        this.ingredient4 = ingredient4;
    }
}
