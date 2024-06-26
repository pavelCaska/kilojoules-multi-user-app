package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Food;
import com.pc.kilojoules.entity.JournalFood;

import java.math.BigDecimal;

public interface JournalFoodService {

    JournalFood createJournalFood(Food food, BigDecimal quantity, String foodName);

}
