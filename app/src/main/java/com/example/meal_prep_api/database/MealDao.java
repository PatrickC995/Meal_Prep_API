package com.example.meal_prep_api.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import java.util.List;

@Dao
public interface MealDao {
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMeals(MealEntity... meals);

    @Query("SELECT * FROM meals ORDER BY meal")
    List<MealEntity> getAllMeals();

    @Query("SELECT COUNT(*) FROM meals")
    int getMealCount();
}
