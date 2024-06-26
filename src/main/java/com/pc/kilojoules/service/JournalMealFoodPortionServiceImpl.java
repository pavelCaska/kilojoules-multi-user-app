package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.JournalMealFoodPortion;
import com.pc.kilojoules.repository.JournalMealFoodPortionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class JournalMealFoodPortionServiceImpl implements JournalMealFoodPortionService {

    private final JournalMealFoodPortionRepository journalMealFoodPortionRepository;

    @Autowired
    public JournalMealFoodPortionServiceImpl(JournalMealFoodPortionRepository journalMealFoodPortionRepository) {
        this.journalMealFoodPortionRepository = journalMealFoodPortionRepository;
    }

    @Override
    public void saveJmfp(JournalMealFoodPortion jmfp) {
        journalMealFoodPortionRepository.save(jmfp);
    }
}
