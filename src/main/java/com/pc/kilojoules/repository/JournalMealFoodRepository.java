package com.pc.kilojoules.repository;

import com.pc.kilojoules.entity.JournalMeal;
import com.pc.kilojoules.entity.JournalMealFood;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface JournalMealFoodRepository extends JpaRepository<JournalMealFood, Long> {

    List<JournalMealFood> findAllByJournalMeal(JournalMeal journalMeal);

}