package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.*;
import com.pc.kilojoules.model.JournalEntryDTO;
import com.pc.kilojoules.model.JournalMealFormDTO;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

public interface JournalService {

    @Transactional
    Journal addFoodToJournal(User user, Food food, BigDecimal quantity, LocalDate date, String mealType, String foodName);

    @Transactional
    Journal updateJournalFood(Long journalId, BigDecimal quantity, LocalDate date, String mealType, String foodName);

    Journal createJournalEntryWithFood(User user, LocalDate date, String mealType, JournalFood jf);

    Journal createJournalEntryWithMeal(User user, LocalDate date, String mealType, JournalMeal jm);

    @Transactional
    Journal updateJournalWithMeal(Long journalId, JournalMealFormDTO formDTO);

    List<Journal> findJournalsByDateAndUserOrderByMealType(LocalDate date, User user);

    List<JournalEntryDTO> findJournalEntriesByDateAndUserOrderByMealType(LocalDate date, MealType mealType, User user);

    List<JournalEntryDTO> convertJournalListToDTO(List<Journal> entries);

    Journal getJournalEntryById(Long id);

    boolean existsJournalByIdAndJournalFoodId(Long journalId, Long journalFoodId);

    boolean existsJournalByIdAndJournalMealId(Long journalId, Long journalMealId);

    Journal deleteJournal(Long id);

    @Transactional
    Journal createJournalAndJournalMeal(User user, Long id, JournalMealFormDTO formDTO);

    boolean doAllEntitiesExist(Long journalId, Long mealId, Long foodId);

    @Transactional
    String updateJournalMealWithJmf(Long journalId, Long foodId, BigDecimal quantity, BigDecimal portionSize, String foodName);

    @Transactional
    String addFoodToJournalMeal(Long journalId, Long foodId, BigDecimal quantity, BigDecimal portionSize);

    @Transactional
    String deleteJmfByIdAndJournalId(Long id, Long journalId);
}
