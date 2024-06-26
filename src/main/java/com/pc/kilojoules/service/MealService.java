package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Meal;
import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.model.MealDTO;
import com.pc.kilojoules.model.MealFoodDTO;
import com.pc.kilojoules.model.MealFormDTO;
import jakarta.transaction.Transactional;
import org.springframework.data.domain.Page;

import java.util.List;

public interface MealService {
    Meal saveMeal(Meal meal);

    Page<Meal> getMealsByPage(int page, User user);

    Meal getMealByIdAndUser(Long id, User user);

    Meal createMeal(User user, MealFormDTO mealFormDTO, List<Long> foods);

    Meal addFoodToMeal(User user, Long id, MealFormDTO mealFormDTO, List<Long> foods);

    Meal updateMealName(Long id, String mealName, User user);

    List<MealDTO> calculateAndReturnMealDtoList(List<Meal> meals);

    MealDTO calculateAndReturnMealDto(Long id, User user);

    void sumUpMealFoods(MealDTO mealDTO, List<MealFoodDTO> mealFoodsDTO);

    List<MealFoodDTO> calculateAndReturnAdjustedMealFoods(Meal meal);

    @Transactional
    Meal deleteMealById(Long id, User user);
}
