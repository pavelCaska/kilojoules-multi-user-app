package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.*;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.model.JournalMealFormDTO;
import com.pc.kilojoules.model.JournalEntryDTO;
import com.pc.kilojoules.repository.JournalRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class JournalServiceImpl implements JournalService {

    private final JournalRepository journalRepository;
    private final JournalFoodService journalFoodService;
    private final JournalMealService journalMealService;
    private final JournalMealFoodService journalMealFoodService;
    private final FoodService foodService;

    @Autowired
    public JournalServiceImpl(JournalRepository journalRepository, JournalFoodService journalFoodService, JournalMealService journalMealService, JournalMealFoodService journalMealFoodService, FoodService foodService) {
        this.journalRepository = journalRepository;
        this.journalFoodService = journalFoodService;
        this.journalMealService = journalMealService;
        this.journalMealFoodService = journalMealFoodService;
        this.foodService = foodService;
    }

    @Override
    @Transactional
    public Journal addFoodToJournal(User user, Food food, BigDecimal quantity, LocalDate date, String mealType, String foodName) {
        JournalFood jf = journalFoodService.createJournalFood(food, quantity, foodName);
        return journalRepository.save(createJournalEntryWithFood(user, date, mealType, jf));
    }
    @Override
    @Transactional
    public Journal updateJournalFood(Long journalId, BigDecimal quantity, LocalDate date, String mealType, String foodName) {
        Journal journal = journalRepository.findById(journalId).orElseThrow(() -> new RecordNotFoundException("Journal entry with id " + journalId + " does not exist!"));
        journal.getJournalFood().calculateAndUpdate(quantity);
        journal.getJournalFood().setName(foodName);
        journal.setConsumedAt(date);
        journal.setMealType(MealType.valueOf(mealType));
        return journalRepository.save(journal);
    }

    @Override
    public Journal createJournalEntryWithFood(User user, LocalDate date, String mealType, JournalFood jf) {
        Journal journal = Journal.builder()
                .consumedAt(date)
                .mealType(MealType.valueOf(mealType))
                .journalFood(jf)
                .journalMeal(null)
                .user(user)
                .build();
        return journalRepository.save(journal);
    }
    @Override
    public Journal createJournalEntryWithMeal(User user, LocalDate date, String mealType, JournalMeal jm) {
        Journal journal = Journal.builder()
                .consumedAt(date)
                .mealType(MealType.valueOf(mealType))
                .journalFood(null)
                .journalMeal(jm)
                .user(user)
                .build();
        return journalRepository.save(journal);
    }

    @Override
    @Transactional
    public Journal updateJournalWithMeal(Long journalId, JournalMealFormDTO formDTO) {
        try {
            Journal journal = getJournalEntryById(journalId);
            journal.setConsumedAt(formDTO.getDate());
            journal.setMealType(MealType.valueOf(formDTO.getMealType()));
            journal.getJournalMeal().setMealName(formDTO.getMealName());
            return journalRepository.save(journal);

        } catch (RecordNotFoundException e) {
            throw new RecordNotFoundException(e.getMessage());
        }
    }

    @Override
    public List<Journal> findJournalsByDateAndUserOrderByMealType(LocalDate date, User user) {
        return journalRepository.findByConsumedAtAndUserOrderByMealTypeAsc(date, user);
    }
    @Override
    public List<JournalEntryDTO> findJournalEntriesByDateAndUserOrderByMealType(LocalDate date, MealType mealType, User user) {
         List<Journal> journalList = findJournalsByDateAndUserOrderByMealType(date, user).stream()
                .filter(journal -> journal.getMealType().equals(mealType))
                .collect(Collectors.toList());
         return convertJournalListToDTO(journalList);
    }

    @Override
    public List<JournalEntryDTO> convertJournalListToDTO(List<Journal> entries) {
        return entries.stream().map(entry -> {
            JournalEntryDTO dto = new JournalEntryDTO();
            if(entry.getJournalFood() != null) {
                dto.setId(entry.getId());
                dto.setMealType(entry.getMealType());
                dto.setFoodId(entry.getJournalFood().getId());
                dto.setName(entry.getJournalFood().getName());
                dto.setQuantity(entry.getJournalFood().getQuantity());
                dto.setKiloJoules(entry.getJournalFood().getKiloJoules());
                dto.setProteins(entry.getJournalFood().getProteins());
                dto.setCarbohydrates(entry.getJournalFood().getCarbohydrates());
                dto.setFiber(entry.getJournalFood().getFiber());
                dto.setFat(entry.getJournalFood().getFat());
            }
            if(entry.getJournalMeal() != null) {
                dto.setId(entry.getId());
                dto.setMealType(entry.getMealType());
                dto.setMealId(entry.getJournalMeal().getId());
                dto.setName(entry.getJournalMeal().getMealName());
                dto.setQuantity(entry.getJournalMeal().getJournalMealFoods().stream()
                        .map(JournalMealFood::getQuantity).reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setKiloJoules(entry.getJournalMeal().getJournalMealFoods().stream()
                        .map(JournalMealFood::getKiloJoules).reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setProteins(entry.getJournalMeal().getJournalMealFoods().stream()
                        .map(JournalMealFood::getProteins).reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setCarbohydrates(entry.getJournalMeal().getJournalMealFoods().stream()
                        .map(JournalMealFood::getCarbohydrates).reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setFiber(entry.getJournalMeal().getJournalMealFoods().stream()
                        .map(JournalMealFood::getFiber).reduce(BigDecimal.ZERO, BigDecimal::add));
                dto.setFat(entry.getJournalMeal().getJournalMealFoods().stream()
                        .map(JournalMealFood::getFat).reduce(BigDecimal.ZERO, BigDecimal::add));
            }
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public Journal getJournalEntryById(Long id) {
        return journalRepository.findById(id).orElseThrow(()-> new RecordNotFoundException("Journal entry with id " + id + " does not exist!"));
    }

    @Override
    public boolean existsJournalByIdAndJournalFoodId(Long journalId, Long journalFoodId) {
        return journalRepository.findJournalByIdAndJournalFoodId(journalId, journalFoodId).isPresent();
    }
    @Override
    public boolean existsJournalByIdAndJournalMealId(Long journalId, Long journalMealId) {
        return journalRepository.findJournalByIdAndJournalMealId(journalId, journalMealId).isPresent();
    }

    @Override
    public Journal deleteJournal(Long id) {
        Journal journal = journalRepository.findById(id).orElseThrow(()-> new RecordNotFoundException("Journal entry with id " + id + " does not exist!"));
        journalRepository.delete(journal);
        return journal;
    }

    @Override
    @Transactional
    public Journal createJournalAndJournalMeal(User user, Long id, JournalMealFormDTO formDTO) {
        JournalMeal journalMeal = journalMealService.createJournalMeal(id, formDTO.getMealName());
        return createJournalEntryWithMeal(user, formDTO.getDate(), formDTO.getMealType(), journalMeal);
    }

    @Override
    public boolean doAllEntitiesExist(Long journalId, Long mealId, Long foodId) {
        return journalRepository.checkAllEntitiesExistence(journalId, mealId, foodId).isPresent();
    }

    @Override
    @Transactional
    public String updateJournalMealWithJmf(Long journalId, Long foodId, BigDecimal quantity, BigDecimal portionSize, String foodName) {
        Journal journal = journalRepository.findById(journalId).orElseThrow(()-> new RecordNotFoundException("Journal entry with id " + journalId + " does not exist!"));
        JournalMeal jm = journal.getJournalMeal();
        JournalMealFood jmf = journalMealFoodService.updateJournalMealFood(foodId, quantity, portionSize, foodName);

        journalMealService.updateSetWithJournalMealFood(jm, jmf);
        journalMealService.calculateAndSetTotalFieldsFromSet(jm, jm.getJournalMealFoods());
        journalMealService.saveJournalMeal(jm);
        journal.setJournalMeal(jm);
        journalRepository.save(journal);
        return jmf.getName();
    }

    @Override
    @Transactional
    public String addFoodToJournalMeal(Long journalId, Long foodId, BigDecimal quantity, BigDecimal portionSize) {
        Journal journal = journalRepository.findById(journalId).orElseThrow(()-> new RecordNotFoundException("Journal entry with id " + journalId + " does not exist!"));
        JournalMeal jm = journal.getJournalMeal();
        Food food = foodService.getFoodById(foodId);
        BigDecimal savedQuantity = quantity.multiply(portionSize);

        JournalMealFood jmf = journalMealFoodService.createJournalMealFoodFromFood(jm, food, savedQuantity);

        journalMealService.updateSetWithJournalMealFood(jm, jmf);
        journalMealService.addJmfToJournalMealTotals(jm, jmf);
        journalMealService.saveJournalMeal(jm);
        journal.setJournalMeal(jm);
        journalRepository.save(journal);
        return jmf.getName();
    }

    @Override
    @Transactional
    public String deleteJmfByIdAndJournalId(Long id, Long journalId) {
        Journal journal = journalRepository.findById(journalId).orElseThrow(()-> new RecordNotFoundException("Journal entry with id " + journalId + " does not exist!"));
        JournalMeal jm = journal.getJournalMeal();
        JournalMealFood jmf = journalMealFoodService.getJournalMealFoodById(id);
        journalMealService.removeJmfAndSubstractFromJournalMealTotals(jm, jmf);
        journalMealFoodService.deleteJmf(jmf);
        journal.setJournalMeal(jm);
        journalRepository.save(journal);
        return jmf.getName();
    }
}
