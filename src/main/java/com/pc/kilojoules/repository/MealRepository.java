package com.pc.kilojoules.repository;

import com.pc.kilojoules.entity.Meal;
import com.pc.kilojoules.entity.User;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface MealRepository extends JpaRepository<Meal, Long>, PagingAndSortingRepository<Meal, Long> {

    List<Meal> findAllByMealNameContainingIgnoreCase(@NotBlank String mealName);

    Page<Meal> findAllByUser(User user, Pageable pageable);
}