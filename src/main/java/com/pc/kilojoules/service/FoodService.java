package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Food;
import org.springframework.data.domain.Page;

import java.util.List;

public interface FoodService {

    List<Food> fetchAllFoods();

    Page<Food> getFoodsByPage(int page);

    Food getFoodById(Long id);

    void saveFood(Food food);

    Food updateFood(Food food);

    Food createFoodWithPortions(Food food);

    Food deleteFoodById(Long id);

    List<Food> searchFood(String query);

}
