package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Food;
import com.pc.kilojoules.entity.Portion;

public interface PortionService {

    Portion createPortion(Food food, Portion portion);

    Portion deletePortionById(Long id);

    boolean existsPortionByIdAndFoodId(Long portionId, Long foodId);
}
