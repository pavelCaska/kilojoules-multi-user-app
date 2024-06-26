package com.pc.kilojoules.repository;

import com.pc.kilojoules.entity.Food;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.PagingAndSortingRepository;

import java.util.List;

public interface FoodRepository extends JpaRepository<Food, Long>, PagingAndSortingRepository<Food, Long> {

    List<Food> findAllByNameContainsIgnoreCase(@NotBlank @Size(max = 255) String name);

}