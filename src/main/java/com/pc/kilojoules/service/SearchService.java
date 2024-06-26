package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Food;
import com.pc.kilojoules.entity.Meal;

import java.util.List;

public interface SearchService {

    List<Food> searchFoodsByName(String query);

    List<Meal> searchMealsByMealName(String query);
}
