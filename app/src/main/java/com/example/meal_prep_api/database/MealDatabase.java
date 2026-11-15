package com.example.meal_prep_api.database;

import android.content.Context;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Database(
        entities = {MealEntity.class},
        version = 1,
        exportSchema = false
)
public abstract class MealDatabase extends RoomDatabase {

    private static MealDatabase instance;

    public static final ExecutorService databaseExecutor =
            Executors.newSingleThreadExecutor();

    public abstract MealDao mealDao();

    public static synchronized MealDatabase getInstance(Context context) {

        if (instance == null) {

            instance = Room.databaseBuilder(
                            context.getApplicationContext(),
                            MealDatabase.class,
                            "meal_database"
                    )
                    .addCallback(initialMealsCallback)
                    .build();
        }

        return instance;
    }

    private static final RoomDatabase.Callback initialMealsCallback =
            new RoomDatabase.Callback() {

                @Override
                public void onCreate(@NonNull SupportSQLiteDatabase db) {
                    super.onCreate(db);

                    String insertSql =
                            "INSERT OR IGNORE INTO meals " +
                                    "(id, meal, category, area, mealThumb, tags, " +
                                    "youtube, ingredient1, ingredient2, " +
                                    "ingredient3, ingredient4) " +
                                    "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?)";

                    db.execSQL(insertSql, new Object[]{
                            1,
                            "Sweet and Sour Pork",
                            "Pork",
                            "Chinese",
                            "https://www.themealdb.com/images/media/meals/1529442316.jpg",
                            "Sweet",
                            "https://www.youtube.com/watch?v=mdaBIhgEAMo",
                            "Pork",
                            "Egg",
                            "Water",
                            "Salt"
                    });

                    db.execSQL(insertSql, new Object[]{
                            2,
                            "Chicken Marengo",
                            "Chicken",
                            "French",
                            "https://www.themealdb.com/images/media/meals/qpxvuq1511798906.jpg",
                            null,
                            "https://www.youtube.com/watch?v=U33HYUr-0Fw",
                            "Olive Oil",
                            "Mushrooms",
                            "Chicken Legs",
                            "Passata"
                    });
                }
            };
}