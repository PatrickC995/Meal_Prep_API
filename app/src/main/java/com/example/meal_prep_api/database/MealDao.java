@Dao
public interface MealDao {

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    void insertMeals(MealEntity... meals);

    @Query("SELECT * FROM meals ORDER BY meal")
    List<MealEntity> getAllMeals();

    @Query("SELECT COUNT(*) FROM meals")
    int getMealCount();
}