package com.pc.kilojoules.repository;

import com.pc.kilojoules.entity.Portion;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface PortionRepository extends JpaRepository<Portion, Long> {

    Optional<Portion> findPortionByIdAndFoodId(Long portionId, Long foodId);

}