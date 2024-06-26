package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Food;
import com.pc.kilojoules.entity.JournalMeal;
import com.pc.kilojoules.entity.JournalMealFood;

import java.math.BigDecimal;

public interface JournalMealFoodService {

    JournalMealFood saveJournalMealFood(JournalMealFood journalMealFood);

    JournalMealFood createJournalMealFoodFromFood(JournalMeal jm, Food food, BigDecimal quantity);

    JournalMealFood updateJournalMealFood(Long id, BigDecimal quantity, BigDecimal portionSize, String foodName);

    JournalMealFood deleteJmfByIdAndMealId(Long id, Long mealId);

    void deleteJmf(JournalMealFood jmf);

    JournalMealFood getJournalMealFoodById(Long id);
}
