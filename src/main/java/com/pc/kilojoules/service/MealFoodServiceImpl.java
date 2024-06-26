package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Meal;
import com.pc.kilojoules.entity.MealFood;
import com.pc.kilojoules.entity.User;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.model.MealFormDTO;
import com.pc.kilojoules.repository.MealFoodRepository;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.List;

@Service
public class MealFoodServiceImpl implements MealFoodService {

    private final MealFoodRepository mealFoodRepository;
    private final MealService mealService;

    @Autowired
    public MealFoodServiceImpl(MealFoodRepository mealFoodRepository, MealService mealService) {
        this.mealFoodRepository = mealFoodRepository;
        this.mealService = mealService;
    }

    @Override
    public MealFood saveMealFood(MealFood mealFood) {
        return mealFoodRepository.save(mealFood);
    }

    @Override
    public MealFood getMealFoodById(Long id) {
        return mealFoodRepository.findById(id).orElseThrow(() -> new RecordNotFoundException("MealFood record with id " + id + " does not exist!"));
    }

    @Override
    @Transactional
    public MealFood deleteMealFoodById(Long mealId, Long id, User user) {
        MealFood mf = mealFoodRepository.findById(id).orElseThrow(()-> new RecordNotFoundException("MealFood record with id " + id + " does not exist!"));
        mealFoodRepository.delete(mf);
        Meal meal = mealService.getMealByIdAndUser(mealId, user);
        meal.getMealFoods().remove(mf);
        mealService.saveMeal(meal);
        return mf;
    }

    @Override
    public boolean existsMealFoodByMealIdAndId(Long mealId, Long id) {
        return mealFoodRepository.findMealFoodByMealIdAndId(mealId, id).isPresent();
    }

    @Override
    @Transactional
    public MealFood updateMealFood(Long mealId, Long mealFoodId, MealFormDTO mealFormDTO, User user) {
        MealFood mealFood = getMealFoodById(mealFoodId);
        Meal meal = mealService.getMealByIdAndUser(mealId, user);

        BigDecimal savedQuantity = mealFormDTO.getQuantity().multiply(mealFormDTO.getPortionSize());

        mealFood.setQuantity(savedQuantity);
        meal.getMealFoods().removeIf(mf -> mf.getId().equals(mealFood.getId()));
        meal.getMealFoods().add(mealFood);

        mealFoodRepository.save(mealFood);
        mealService.saveMeal(meal);
        return mealFood;
    }

    @Override
    public boolean isFoodAssociatedToMealFood(Long foodId) {
        List<MealFood> mealFoodList = mealFoodRepository.findMealFoodByFoodId (foodId);
        return !mealFoodList.isEmpty();
    }

    @Override
    @Transactional
    public void deleteMealFood(MealFood mealFood){
        mealFoodRepository.delete(mealFood);
    }

}
