package com.example.meal_prep_api.database;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;
import java.util.List;

@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    void insertMeals(MealEntity... meals);

    @Query("SELECT * FROM meals ORDER BY meal")
    List<MealEntity> getAllMeals();

    @Query("SELECT COUNT(*) FROM meals")
    int getMealCount();

    @Query("SELECT * FROM meals WHERE " +
            "instr(LOWER(meal), LOWER(:search)) > 0 OR " +
            "instr(LOWER(ingredient1), LOWER(:search)) > 0 OR " +
            "instr(LOWER(ingredient2), LOWER(:search)) > 0 OR " +
            "instr(LOWER(ingredient3), LOWER(:search)) > 0 OR " +
            "instr(LOWER(ingredient4), LOWER(:search)) > 0 " +
            "ORDER BY meal")
    List<MealEntity> searchMeals(String search);
}