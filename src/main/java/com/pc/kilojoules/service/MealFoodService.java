package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.MealFood;
import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.model.MealFormDTO;
import jakarta.transaction.Transactional;

public interface MealFoodService {

    MealFood saveMealFood(MealFood mealFood);

    MealFood getMealFoodById(Long id);

    @Transactional
    MealFood deleteMealFoodById(Long mealId, Long id, User user);

    boolean existsMealFoodByMealIdAndId(Long mealId, Long foodId);

    @Transactional
    MealFood updateMealFood(Long mealId, Long mealFoodId, MealFormDTO mealFormDTO, User user);

    boolean isFoodAssociatedToMealFood(Long foodId);

    @Transactional
    void deleteMealFood(MealFood mealFood);
}
