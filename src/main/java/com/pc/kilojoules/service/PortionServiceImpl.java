package com.pc.kilojoules.service;

import com.pc.kilojoules.entity.Food;
import com.pc.kilojoules.entity.Portion;
import com.pc.kilojoules.exception.RecordNameExistsException;
import com.pc.kilojoules.exception.RecordNotDeletableException;
import com.pc.kilojoules.exception.RecordNotFoundException;
import com.pc.kilojoules.repository.PortionRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PortionServiceImpl implements PortionService {

    private final PortionRepository portionRepository;
    private final FoodService foodService;

    @Autowired
    public PortionServiceImpl(PortionRepository portionRepository, FoodService foodService) {
        this.portionRepository = portionRepository;
        this.foodService = foodService;
    }


    @Override
    public Portion createPortion(Food food, Portion portion) throws RecordNameExistsException {
        List<Portion> portionList = food.getPortions();
        for (Portion item : portionList) {
            if(item.getPortionName().equals(portion.getPortionName())) {
                throw new RecordNameExistsException("Portion name already exists for this food.");
            }
        }
        Portion newPortion = Portion.builder()
                .portionName(portion.getPortionName())
                .portionSize(portion.getPortionSize())
                .food(food)
                .build();
        portionList.add(newPortion);
        food.setPortions(portionList);
        foodService.saveFood(food);
        return newPortion;
    }

    @Override
    public Portion deletePortionById(Long id) throws RecordNotDeletableException {
        Portion portion = portionRepository.findById(id).orElseThrow(()-> new RecordNotFoundException("Portion record with id \" + id + \" does not exist!"));
        if(portion.getPortionName().equals("100 g") || portion.getPortionName().equals("1 g")) {
            throw new RecordNotDeletableException("This record cannot be deleted.");
        }
        List<Portion> portionList = portion.getFood().getPortions();
        portionList.remove(portion);
        portion.getFood().setPortions(portionList);
        foodService.saveFood(portion.getFood());
        portionRepository.delete(portion);
        return portion;
    }
    @Override
    public boolean existsPortionByIdAndFoodId(Long portionId, Long foodId) {
        return portionRepository.findPortionByIdAndFoodId(portionId, foodId).isPresent();
    }

}
